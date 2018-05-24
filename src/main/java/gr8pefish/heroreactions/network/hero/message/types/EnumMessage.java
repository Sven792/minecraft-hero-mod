package gr8pefish.heroreactions.network.hero.message.types;

public enum EnumMessage {

    PING {
        @Override
        public void onMessageReceived() {
            PingMessage.onMessageReceived();
        }
    },
    PONG {
        @Override
        public void onMessageReceived() {
            PongMessage.onMessageReceived();
        }
    },
    NONE {
        @Override
        public void onMessageReceived() {
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

    EnumMessage() {
        //empty constructor
    }

}
