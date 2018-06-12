package gr8pefish.heroreactions.hero.network.json.variants;

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
