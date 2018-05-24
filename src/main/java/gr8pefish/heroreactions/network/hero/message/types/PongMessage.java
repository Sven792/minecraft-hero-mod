package gr8pefish.heroreactions.network.hero.message.types;

import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.network.hero.json.HeroJSONMessageHelper;
import gr8pefish.heroreactions.network.hero.message.MessageHelper;
import gr8pefish.heroreactions.network.hero.message.core.IHeroMessageSend;

public class PongMessage implements IHeroMessageSend {

    //ToDo: Actual PongFrame send back? Need to determine the relationship with this and the JSON.
    public static void send() {
        HeroReactions.LOGGER.info("Sending pong back");
        MessageHelper.send(HeroJSONMessageHelper.createSerializedJsonString("pong"));
    }

}
