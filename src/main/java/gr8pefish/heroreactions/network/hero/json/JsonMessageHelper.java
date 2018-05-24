package gr8pefish.heroreactions.network.hero.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gr8pefish.heroreactions.network.hero.json.types.AbstractJsonMessage;
import gr8pefish.heroreactions.network.hero.json.types.PingPongJsonMessage;
import gr8pefish.heroreactions.network.hero.json.types.TextJsonMessage;
import gr8pefish.heroreactions.network.hero.message.types.EnumHeroMessage;

public class JsonMessageHelper {

    private static final Gson gson = new GsonBuilder().serializeNulls().create();



    //Create valid JSON String representation to send to API from a string "ping" or "pong" passed in
    public static String createSerializedJsonString(AbstractJsonMessage msg){
        return gson.toJson(msg);
    }

    //Create valid JSON String representation to send to API from a string "ping" or "pong" passed in
    public static String createSerializedTextJsonString(String text){
        TextJsonMessage msg = new TextJsonMessage(text);
        return createSerializedJsonString(msg);
    }

    //Create valid JSON String representation to send to API from a string "ping" or "pong" passed in
    public static String createSerializedPingPongJsonString(PingPongJsonMessage.PingPongEnum pingpong){
        PingPongJsonMessage msg = new PingPongJsonMessage(pingpong);
        return createSerializedJsonString(msg);
    }


    //Get type of message based on the contents (currently only ping supported)
    public static EnumHeroMessage getMessageTypeFromJson(String string) {
        Gson gson = new Gson();
        PingPongJsonMessage message = gson.fromJson(string, PingPongJsonMessage.class);
        switch (message.type) {
            case "ping":
                return EnumHeroMessage.PING;
            default:
                return EnumHeroMessage.NONE;
        }
    }
}
