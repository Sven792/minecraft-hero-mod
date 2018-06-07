package gr8pefish.heroreactions.network.hero.json;

import com.google.gson.*;
import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.network.hero.json.variants.*;
import gr8pefish.heroreactions.network.hero.json.variants.SubscribeJsonMessage;
import gr8pefish.heroreactions.network.hero.message.HeroMessages;
import gr8pefish.heroreactions.hero.data.enums.Reactions;
import gr8pefish.heroreactions.network.hero.HeroData;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.minecraft.util.JsonUtils;

public class JsonMessageHelper {

    //Setup tools
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    private static final JsonParser parser = new JsonParser();

    /** Create a serialized string representation of a message */
    private static String createSerializedJsonString(AbstractJsonMessage message){
        return gson.toJson(message);
    }

    /**
     * Creates a serialized string from JSON formatted text
     *
     * Format: {"type": "text", "data": [text-parameter]}
     *
     * @param text - the text to hold in the message
     * @return - String of the message
     */
    public static String createSerializedTextMessage(String text){
        TextJsonMessage msg = new TextJsonMessage(text);
        return createSerializedJsonString(msg);
    }

    /**
     * Creates a serialized string from JSON formatted subscribe message
     *
     * Format: {"type": "text", "data": [text-parameter]}
     *
     * @param topic - the topic to subscribe to ()
     * @return - String of the message
     */
    public static String createSerializedSubscribeMessage(SubscribeJsonMessage.SubscribeTopics topic){
        SubscribeJsonMessage msg = new SubscribeJsonMessage(topic);
        return createSerializedJsonString(msg);
    }

    /**
     * Creates a serialized string from JSON formatted text
     * Used to ping/pong the server
     *
     * Format: {"type": "[ping] -or- [pong]", "data": null}
     *
     * @param pingpong - either PING or PONG, the type to send
     * @return - String of the message
     */
    public static String createSerializedPingPongMessage(PingPongJsonMessage.PingPongEnum pingpong){
        PingPongJsonMessage msg = new PingPongJsonMessage(pingpong);
        return createSerializedJsonString(msg);
    }

    //Get java object from WebSocket text directly
    public static AbstractJsonMessage getMessageFromTextFrame(String string) {
        return gson.fromJson(string, AbstractJsonMessage.class); //ToDo: Probably won't work (with nested data and whatnot)
    }

    //Get type of message based on the contents (currently only ping supported)
    public static HeroMessages getMessageTypeFromJson(String string) {
        String messageType = parser.parse(string).getAsJsonObject().get("type").getAsString();
        switch (messageType) {
            case "ping":
                return HeroMessages.PING;
            case "pong":
                return HeroMessages.PONG;
            case "feedback":
                return HeroMessages.FEEDBACK;
            case "feedback-top": //ToDo: Will change with Hero's API update
                return HeroMessages.FEEDBACK_ACTIVITY;
            case "online":
                return HeroMessages.ONLINE;
            case "viewers":
                return HeroMessages.VIEWERS;
            default:
                return HeroMessages.NONE;
        }
    }

    /**
     * Gets the "data" element of the message, parses it depending on the message type,
     * and stores the resulting data in the appropriate location in {@link HeroData}
     *
     * @param message - The message received (JSON format)
     * @param messageType - The {@link HeroMessages} type of message received
     */
    public static void setMessageData(TextWebSocketFrame message, HeroMessages messageType) {
        JsonElement dataElement = parser.parse(message.text()).getAsJsonObject().get("data");
        switch (messageType) {
            case FEEDBACK:
                //parse message `data` for "feedback" format
                HeroData.Feedback.reaction = Reactions.getFromString(JsonUtils.getString(dataElement.getAsJsonObject(), "option"));
                HeroData.Feedback.count = JsonUtils.getInt(dataElement.getAsJsonObject(), "n");
                return;
            case FEEDBACK_ACTIVITY:
                //parse message `data` for "feedback-activity" format
                JsonArray jsonArray = JsonUtils.getJsonArray(dataElement.getAsJsonObject(), "options");
                for (JsonElement element : jsonArray) {
                    Reactions feedbackType = Reactions.getFromString(JsonUtils.getString(element.getAsJsonObject(), "id"));
                    int count = JsonUtils.getInt(element.getAsJsonObject(), "activity");
                    HeroData.FeedbackActivity.getFeedbackActivity().put(feedbackType, count);
                }
                return;
            case ONLINE:
                HeroData.Online.isOnline = dataElement.getAsBoolean();
                return;
            case VIEWERS:
                //parse message `data` for "viewers" format
                HeroData.Viewers.direct = JsonUtils.getInt(dataElement.getAsJsonObject(), "direct");
                HeroData.Viewers.indirect = JsonUtils.getInt(dataElement.getAsJsonObject(), "indirect");
                return;
            default:
                HeroReactions.LOGGER.error("Invalid type of message!");
        }
    }
}
