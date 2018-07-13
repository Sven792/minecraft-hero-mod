package gr8pefish.heroreactions.hero.network.json.variants;

/**
 * Format:
 * {
 *      "type": "sub",
 *      "data": {
 *          "topic": [topic]
 *      }
 * }
 *
 */
public class SubscribeJsonMessage extends AbstractJsonMessage {

    //MainTest class to hold everything
    public SubscribeJsonMessage(SubscribeTopics topic) {
        this.type = "sub";
        this.data = new SubscribeTopicJsonMessage(topic.stringRepresentation);
    }

    //Inner class to hold the topic
    public class SubscribeTopicJsonMessage {

        public String topic;

        public SubscribeTopicJsonMessage(String topic) {
            this.topic = topic;
        }

    }

    //Enum for all the possible topics to subscribe to
    public enum SubscribeTopics {
        FEEDBACK("feedback"),
        FEEDBACK_ACTIVITY("feedback-top"), //TODO: Change with Hero API update
        ONLINE("online"),
        VIEWERS("viewers");

        public String stringRepresentation;

        SubscribeTopics(String input) {
            this.stringRepresentation = input;
        }
    }

}

