package gr8pefish.heroreactions.network.hero.json.types;

public abstract class AbstractJsonMessage {

    public String type;
    public Object data;

    @Override
    public String toString() {
        return "type: "+this.type + " | data: "+this.data;
    }


}
