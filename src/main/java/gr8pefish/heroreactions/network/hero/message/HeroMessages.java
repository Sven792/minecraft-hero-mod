package gr8pefish.heroreactions.network.hero.message;

import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.network.hero.json.JsonMessageHelper;
import gr8pefish.heroreactions.network.hero.json.types.PingPongJsonMessage;

public enum HeroMessages {

    PING {
        @Override
        public void onMessageReceived() {
            //when ping received, send a pong back
            PONG.send(PingPongJsonMessage.PingPongEnum.PONG);
        }
        @Override
        public void send(Object message) {
            HeroReactions.LOGGER.info("Sending ping");
            //create serialized JSON string of the ping message and send it off
            MessageHelper.sendJson(JsonMessageHelper.createSerializedPingPongJsonString((PingPongJsonMessage.PingPongEnum)message));
        }
    },
    PONG {
        @Override
        public void onMessageReceived() {
            //No-op
        }

        @Override
        public void send(Object message) {
            HeroReactions.LOGGER.info("Sending pong");
            //create serialized JSON string of the pong message and send it off
            MessageHelper.sendJson(JsonMessageHelper.createSerializedPingPongJsonString((PingPongJsonMessage.PingPongEnum)message));
        }
    },
    TEXT {
        @Override
        public void onMessageReceived() {
            //No-op
        }

        @Override
        public void send(Object message) {
            HeroReactions.LOGGER.info("Sending text message");
            //create serialized JSON string of the text message and send it off
            MessageHelper.sendJson(JsonMessageHelper.createSerializedTextJsonString((String)message));
        }
    },
    NONE {
        @Override
        public void onMessageReceived() {
            //No-op
        }
        @Override
        public void send(Object message) {
            //No-op
        }
    };

    //ToDo: Full enum of types, with helpful references to class to enable clean code elsewhere
//    PING(PingMessage.class),
//    PONG(PongMessage.class),
//    SUBSCRIBE(SubscribeMessage.class),
//    FEEDBACK(FeedbackMessage.class),
//    FEEDBACK_ACTIVITY(FeedbackActivityMessage.class),
//    ONLINE(OnlineMessage.class),
//    VIEWERS(ViewersMessage.class);
//

    public abstract void onMessageReceived();
    public abstract void send(Object message);

    HeroMessages() {
        //empty constructor
    }

}
