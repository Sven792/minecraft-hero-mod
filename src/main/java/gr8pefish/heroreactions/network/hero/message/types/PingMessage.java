package gr8pefish.heroreactions.network.hero.message.types;

import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.network.hero.json.JsonMessageHelper;
import gr8pefish.heroreactions.network.hero.json.types.PingPongJsonMessage;
import gr8pefish.heroreactions.network.hero.message.MessageHelper;
import gr8pefish.heroreactions.network.hero.message.core.IHeroMessageReceive;
import gr8pefish.heroreactions.network.hero.message.core.IHeroMessageSend;

public class PingMessage implements IHeroMessageReceive, IHeroMessageSend {

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
