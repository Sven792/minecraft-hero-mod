package gr8pefish.heroreactions.network.hero.json.types;

/**
 * Format:
 * {"type": "text", "data": [text]}
 */
public class TextJsonMessage extends AbstractJsonMessage {

    public TextJsonMessage(String text) {
        this.type = "text";
        this.data = text;
    }

}
