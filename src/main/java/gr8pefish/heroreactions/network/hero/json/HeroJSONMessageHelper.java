package gr8pefish.heroreactions.network.hero.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gr8pefish.heroreactions.network.hero.message.types.EnumHeroMessage;

public class HeroJSONMessageHelper {

    //Create valid JSON String representation to send to API from a string "ping" or "pong" passed in
    public static String createSerializedJsonString(String pingpong){
        Gson gson = new GsonBuilder().serializeNulls().create(); //need to account for nulls
        PingPongJsonMessage msg = new PingPongJsonMessage(pingpong);
        return gson.toJson(msg);
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
