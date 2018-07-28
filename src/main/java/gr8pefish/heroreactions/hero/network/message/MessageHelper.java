package gr8pefish.heroreactions.hero.network.message;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.hero.data.HeroData;
import gr8pefish.heroreactions.hero.data.UserData;
import gr8pefish.heroreactions.hero.network.json.variants.PingPongJsonMessage;
import gr8pefish.heroreactions.hero.network.json.variants.SubscribeJsonMessage;
import gr8pefish.heroreactions.hero.network.websocket.WebSocketClient;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class with more human readable method call names to delegate message related actions to their appropriate inner methods.
 */
public class MessageHelper {

    /**
     * Send a JSON message to the server over the websocket connection.
     *
     * @param jsonMessage - correctly formatted JSON message
     */
    public static void sendJson(String jsonMessage) {
        Common.LOGGER.info("Sending JSON Message: "+jsonMessage);
        WebSocketFrame frame = new TextWebSocketFrame(jsonMessage);
        WebSocketClient.sendMessage(frame); //safe call that ensures connection is okay before sending
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
        if (WebSocketClient.isConnected()) { //connected
            //AccountID - for debug purposes, inefficient code so commented out for production
//            returnList.add("ID: " + UserData.ACCOUNT_ID.retrieve());
            //isOnline
            returnList.add("Online: "+ HeroData.Online.isOnline);
            //viewerCount
            returnList.add("Viewers [Direct/Indirect]: "+ HeroData.Viewers.direct+"/"+ HeroData.Viewers.indirect);
            //feedback (list)
            returnList.add("Feedback/FeedbackTypes -> Count:");
            ConcurrentHashMap<FeedbackTypes, Integer> feedback = HeroData.FeedbackActivity.getFeedbackActivity();
            for (Map.Entry<FeedbackTypes, Integer> entry : feedback.entrySet()) {
                returnList.add(entry.getKey().toString()+" - "+entry.getValue()); //feedback type - count
            }
        }
        return returnList;
    }

    //Helper methods to easily/publicly send all types of messages

    /** Helper method to send ping */
    public static void sendPing() {
        //Json
        HeroMessages.PING.send(PingPongJsonMessage.PingPongEnum.PING);
    }

    /** Helper method to send pong - unused */
    public static void sendPong(PingWebSocketFrame ping) {
        //Json
        HeroMessages.PONG.send(PingPongJsonMessage.PingPongEnum.PONG);
    }

    /** Helper method to send text */
    public static void sendText(String text) {
        //Json
        HeroMessages.TEXT.send(text);
    }

    /** Helper method to subscribe to an event */
    public static void subscribeToEvent(SubscribeJsonMessage.SubscribeTopics topic) {
        //Json
        HeroMessages.SUBSCRIBE.send(topic);
    }

}
