package gr8pefish.heroreactions.network.hero.json.types;

/**
 * Format:
 *
 * //Ping
 * {"type": "ping", "data": null }
 *
 * //Pong
 * {"type": "pong", "data": null }
 *
 */
public class PingPongJsonMessage extends AbstractJsonMessage {

    public PingPongJsonMessage(PingPongEnum pingpong) {
        this.type = pingpong.toString().toLowerCase();
        this.data = null;
    }

    public enum PingPongEnum {
        PING,
        PONG
    }

}
