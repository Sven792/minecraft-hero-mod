package gr8pefish.heroreactions.network.hero.message;

import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.network.hero.json.types.PingPongJsonMessage;
import gr8pefish.heroreactions.network.hero.json.types.SubscribeJsonMessage;
import gr8pefish.heroreactions.network.hero.websocket.WebSocketClient;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class MessageHelper {

    //Send json data to server over websocket
    public static void sendJson(String jsonMessage) {
        HeroReactions.LOGGER.info("Sending JSON Message: "+jsonMessage);
        WebSocketFrame frame = new TextWebSocketFrame(jsonMessage);
        WebSocketClient.WEBSOCKET_CHANNEL.writeAndFlush(frame);
    }

    //Helper method to send ping
    public static void sendPing() {
        //Json
        HeroMessages.PING.send(PingPongJsonMessage.PingPongEnum.PING);

        //PingFrame
//        HeroReactions.LOGGER.info("Sending ping - PingFrame");
//        WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[]{8, 1, 8, 1}));
//        WebSocketClient.WEBSOCKET_CHANNEL.writeAndFlush(frame);
    }

    //Helper method to send pong
    public static void sendPong(PingWebSocketFrame ping) {
        //Json
        HeroMessages.PONG.send(PingPongJsonMessage.PingPongEnum.PONG);

        //PongFrame
//        HeroReactions.LOGGER.info("Sending pong - PongFrame");
//        WebSocketFrame frame = new PongWebSocketFrame(ping.content().retain());
//        WebSocketClient.WEBSOCKET_CHANNEL.writeAndFlush(frame);
    }

    //Helper method to send text
    public static void sendText(String text) {
        //Json
        HeroMessages.TEXT.send(text);

        //Basic Text
//        HeroReactions.LOGGER.info("Sending text - no JSON");
//        WebSocketFrame frame = new TextWebSocketFrame(text);
//        WebSocketClient.WEBSOCKET_CHANNEL.writeAndFlush(frame);
    }

    public static void subscribeToEvent(SubscribeJsonMessage.SubscribeTopics topic) {
        //Json
        HeroMessages.SUBSCRIBE.send(topic);
    }

    //Helper method to close connection
    public static void closeConnection() throws InterruptedException {
        //close connection
        WebSocketClient.WEBSOCKET_CHANNEL.writeAndFlush(new CloseWebSocketFrame());
        WebSocketClient.WEBSOCKET_CHANNEL.closeFuture().sync();
        //shutdown group
        WebSocketClient.GROUP.shutdownGracefully();
    }

}