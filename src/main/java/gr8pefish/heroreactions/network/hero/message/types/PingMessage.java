package gr8pefish.heroreactions.network.hero.message.types;

import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.network.hero.json.JsonMessageHelper;
import gr8pefish.heroreactions.network.hero.json.types.PingPongJsonMessage;
import gr8pefish.heroreactions.network.hero.message.MessageHelper;
import gr8pefish.heroreactions.network.hero.message.core.IMessageReceive;
import gr8pefish.heroreactions.network.hero.message.core.IMessageSend;

public class PingMessage implements IMessageReceive, IMessageSend {

    //When you get a ping, send a pong
    public static void onMessageReceived() {
        PongMessage.send();
    }

    public static void send() {
        HeroReactions.LOGGER.info("Sending ping");
        MessageHelper.sendJson(
                JsonMessageHelper.createSerializedPingPongJsonString(PingPongJsonMessage.PingPongEnum.PING));
    }

}
