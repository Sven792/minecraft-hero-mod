package gr8pefish.heroreactions.network.hero.json.types;

/**
 * Subscribes to the following convention:
 *
 *  //Ping
    {
        "type": "ping",
        "data": null
    }

    //Pong
    {
        "type": "pong",
        "data": null
    }
 *
 */
//ToDo: Abstract out to generalized type/data(w/ possible nested data inside) format
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
