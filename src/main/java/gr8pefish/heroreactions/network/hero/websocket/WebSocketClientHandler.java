package gr8pefish.heroreactions.network.hero.websocket;

import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.network.hero.json.HeroJSONMessageHelper;
import gr8pefish.heroreactions.network.hero.message.MessageHelper;
import gr8pefish.heroreactions.network.hero.message.types.EnumHeroMessage;
import gr8pefish.heroreactions.network.hero.message.types.PingMessage;
import gr8pefish.heroreactions.network.hero.message.types.PongMessage;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

/**
 * Class to handle the channel connections/messages. Utilized by {@link WebSocketClient}.
 */
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    // Private fields
    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    // Constructor
    public WebSocketClientHandler(final WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    // Getter
    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    // Overrides
    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        HeroReactions.LOGGER.info("WebSocket Client disconnected!");
    }

    /**
     * Handles messaging (i.e. the important one).
     *
     * @param ctx - the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler} belongs to
     * @param msg - the message to handle
     * @throws Exception - thrown if an error occurs
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        final Channel ch = ctx.channel();

        //ensure connection firmly established
        if (!handshaker.isHandshakeComplete()) {
            // web socket client connected
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            handshakeFuture.setSuccess();
            return;
        }

        //ensure correct data format
        if (msg instanceof FullHttpResponse) {
            final FullHttpResponse response = (FullHttpResponse) msg;
            throw new Exception("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content="
                    + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        //test (logs message received)
        HeroReactions.LOGGER.info("Message received from server: "+msg.toString());

        //cast message to correct WebSocketFrame and perform fitting action (typically just printing for now)
        final WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof PingWebSocketFrame) { //ToDo: Determine why never triggered
            HeroReactions.LOGGER.info("PING received");
            PingMessage.onMessageReceived(); //send back pong
        } else if (frame instanceof PongWebSocketFrame) { //ToDo: Why do I get a PongFrame when pinging, but don't get a PingFrame when it is sent to me?
            HeroReactions.LOGGER.info("PONG received");
        } else if (frame instanceof TextWebSocketFrame) {
            final TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            HeroReactions.LOGGER.info(textFrame.text()); //uncomment to print request, but do it anyway for testing
            if (MessageHelper.gotPing(textFrame)) { //if got a ping
                PingMessage.onMessageReceived(); //send pong
            }
        } else if (frame instanceof CloseWebSocketFrame)
            ch.close();
        else if (frame instanceof BinaryWebSocketFrame) { //Unused?
            HeroReactions.LOGGER.info(frame.content().toString()); //uncomment to print request, but do it anyway for testing
        }

    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        cause.printStackTrace();
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
}
