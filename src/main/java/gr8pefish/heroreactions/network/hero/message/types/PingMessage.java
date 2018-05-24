package gr8pefish.heroreactions.network.hero.message.types;

import gr8pefish.heroreactions.network.hero.message.core.IHeroMessageReceive;

public class PingMessage implements IHeroMessageReceive {

    //When you get a ping, send a pong
    public static void onMessageReceived() {
        PongMessage.send();
    }

}
