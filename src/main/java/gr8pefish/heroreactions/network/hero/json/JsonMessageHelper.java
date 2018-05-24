package gr8pefish.heroreactions.network.hero.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gr8pefish.heroreactions.network.hero.json.types.AbstractJsonMessage;
import gr8pefish.heroreactions.network.hero.json.types.PingPongJsonMessage;
import gr8pefish.heroreactions.network.hero.json.types.TextJsonMessage;
import gr8pefish.heroreactions.network.hero.message.types.EnumMessage;

public class JsonMessageHelper {

    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    //Create valid JSON String representation to send to API from a string "ping" or "pong" passed in

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
    public static String createSerializedTextJsonString(String text){
        TextJsonMessage msg = new TextJsonMessage(text);
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
    public static String createSerializedPingPongJsonString(PingPongJsonMessage.PingPongEnum pingpong){
        PingPongJsonMessage msg = new PingPongJsonMessage(pingpong);
        return createSerializedJsonString(msg);
    }


    //Get type of message based on the contents (currently only ping supported)
    public static EnumMessage getMessageTypeFromJson(String string) {
        PingPongJsonMessage message = gson.fromJson(string, PingPongJsonMessage.class);
        switch (message.type) {
            case "ping":
                return EnumMessage.PING;
            case "pong":
                return EnumMessage.PONG;
            default:
                return EnumMessage.NONE;
        }
    }
}
