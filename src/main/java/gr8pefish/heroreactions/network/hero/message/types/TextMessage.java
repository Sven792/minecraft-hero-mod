package gr8pefish.heroreactions.network.hero.message.types;

import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.network.hero.json.JsonMessageHelper;
import gr8pefish.heroreactions.network.hero.message.MessageHelper;
import gr8pefish.heroreactions.network.hero.message.core.IMessageSend;

public class TextMessage implements IMessageSend {

    public static void send(String text) {
        HeroReactions.LOGGER.info("Sending text message");
        MessageHelper.sendJson(
                JsonMessageHelper.createSerializedTextJsonString(text));
    }
}
