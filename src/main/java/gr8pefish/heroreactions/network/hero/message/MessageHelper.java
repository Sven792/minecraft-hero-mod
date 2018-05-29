package gr8pefish.heroreactions.network.hero.message;

import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.network.hero.json.types.PingPongJsonMessage;
import gr8pefish.heroreactions.network.hero.json.types.SubscribeJsonMessage;
import gr8pefish.heroreactions.network.hero.message.data.FeedbackTypes;
import gr8pefish.heroreactions.network.hero.message.data.StreamData;
import gr8pefish.heroreactions.network.hero.websocket.WebSocketClient;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHelper {

    //TODO: More error handling for if connection not perfect

    //Send json data to server over websocket
    public static void sendJson(String jsonMessage) {
        HeroReactions.LOGGER.info("Sending JSON Message: "+jsonMessage);
        WebSocketFrame frame = new TextWebSocketFrame(jsonMessage);
        WebSocketClient.WEBSOCKET_CHANNEL.writeAndFlush(frame);
    }


    /**
     * Subscribe to all relevant options.
     */
    public static void subscribeAll() {
        subscribeToEvent(SubscribeJsonMessage.SubscribeTopics.ONLINE);
        subscribeToEvent(SubscribeJsonMessage.SubscribeTopics.VIEWERS);
        subscribeToEvent(SubscribeJsonMessage.SubscribeTopics.FEEDBACK_ACTIVITY);
    }

    //TODO: Optimize data collection/cache
    /**
     * Helper method to get data from the stream as an array of strings.
     *
     * @return - ArrayList of strings, each entry will display as a new line
     */
    public static ArrayList<String> getStreamData() {
        ArrayList<String> returnList = new ArrayList<>();
        if (WebSocketClient.WEBSOCKET_CHANNEL.isOpen()) { //connected TODO: Helper method isConnected()
            //isOnline
            returnList.add("Online: "+StreamData.Online.isOnline);
            //viewerCount
            returnList.add("Viewers [Direct/Indirect]: "+StreamData.Viewers.direct+"/"+ StreamData.Viewers.indirect);
            //feedback (list)
            returnList.add("Feedback/Reactions -> Count:");
            ConcurrentHashMap<FeedbackTypes, Integer> feedback = StreamData.FeedbackActivity.getFeedbackActivity();
            for (Map.Entry<FeedbackTypes, Integer> entry : feedback.entrySet()) {
                returnList.add(entry.getKey().toString()+" - "+entry.getValue()); //feedback type - count
            }
        }
        return returnList;
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
