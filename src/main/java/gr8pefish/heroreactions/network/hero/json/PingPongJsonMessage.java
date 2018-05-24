package gr8pefish.heroreactions.network.hero.json;

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
public class PingPongJsonMessage {

    public String type;
    public Object data = null;

    PingPongJsonMessage(String pingpong) {
        this.type = pingpong;
    }

    @Override
    public String toString() {
        return "type: "+this.type + " | data: "+this.data;
    }
}
