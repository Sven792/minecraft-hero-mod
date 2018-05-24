package gr8pefish.heroreactions.network.hero.json.types;

/**
 * Format:
 * {"type": [type], "data": [data]}
 */
public abstract class AbstractJsonMessage {

    public String type;
    public Object data;

    @Override
    public String toString() {
        return "type: "+this.type + " | data: "+this.data;
    }


}
