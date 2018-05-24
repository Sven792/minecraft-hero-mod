package gr8pefish.heroreactions.network.hero.message.types;

import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.network.hero.json.JsonMessageHelper;
import gr8pefish.heroreactions.network.hero.json.types.PingPongJsonMessage;
import gr8pefish.heroreactions.network.hero.message.MessageHelper;
import gr8pefish.heroreactions.network.hero.message.core.IMessageReceive;
import gr8pefish.heroreactions.network.hero.message.core.IMessageSend;

public class PongMessage implements IMessageReceive, IMessageSend {

    //When you get a pong, do nothing
    public static void onMessageReceived() {
        //No-op
    }

    //ToDo: Actual PongFrame send back? Need to determine the relationship with this and the JSON.
    public static void send() {
        HeroReactions.LOGGER.info("Sending pong");
        MessageHelper.sendJson(
                JsonMessageHelper.createSerializedPingPongJsonString(PingPongJsonMessage.PingPongEnum.PONG));
    }

}
