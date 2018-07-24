package gr8pefish.heroreactions.hero.network.websocket;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.hero.network.json.JsonMessageHelper;
import gr8pefish.heroreactions.hero.network.message.HeroMessages;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
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
        Common.LOGGER.info("WebSocket Client disconnected!");
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

        //Debug messages
        Common.LOGGER.debug("Message received from server: " + msg.toString());

        //cast message to correct WebSocketFrame and perform fitting action (typically just printing for now)
        final WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {

            //get frame message (and print it if debugging)
            final TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            Common.LOGGER.info("Message contents: "+textFrame.text()); //uncomment to display request, but do it anyway for testing

            //handle message, delegating to the appropriate method
            HeroMessages messageType = JsonMessageHelper.getMessageTypeFromJson(textFrame.text());
            messageType.onMessageReceived(textFrame);

        } else if (frame instanceof CloseWebSocketFrame) {
            ch.close();
        } else {
            Common.LOGGER.warn("New type of frame obtained: "+frame.toString());
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
