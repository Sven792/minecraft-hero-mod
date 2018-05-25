package gr8pefish.heroreactions.network.hero.message;

import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.network.hero.json.JsonMessageHelper;
import gr8pefish.heroreactions.network.hero.json.types.PingPongJsonMessage;
import gr8pefish.heroreactions.network.hero.json.types.SubscribeJsonMessage;
import gr8pefish.heroreactions.network.hero.message.data.StreamData;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public enum HeroMessages {

    PING {
        @Override
        public void onMessageReceived(TextWebSocketFrame message) {
            //when ping received, send a pong back
            PONG.send(PingPongJsonMessage.PingPongEnum.PONG);
        }
        @Override
        public void send(Object message) {
            HeroReactions.LOGGER.info("Sending ping");
            //create serialized JSON string of the ping message and send it off
            MessageHelper.sendJson(JsonMessageHelper.createSerializedPingPongMessage((PingPongJsonMessage.PingPongEnum)message));
        }
    },
    PONG {
        @Override
        public void onMessageReceived(TextWebSocketFrame message) {
            //No-op
        }

        @Override
        public void send(Object message) {
            HeroReactions.LOGGER.info("Sending pong");
            //create serialized JSON string of the pong message and send it off
            MessageHelper.sendJson(JsonMessageHelper.createSerializedPingPongMessage((PingPongJsonMessage.PingPongEnum)message));
        }
    },
    TEXT { //basically just used for testing
        @Override
        public void onMessageReceived(TextWebSocketFrame message) {
            //No-op
        }

        @Override
        public void send(Object message) {
            HeroReactions.LOGGER.info("Sending text message");
            //create serialized JSON string of the text message and send it off
            MessageHelper.sendJson(JsonMessageHelper.createSerializedTextMessage((String)message));
        }
    },
    SUBSCRIBE { //to register to listen to specific events (i.e. those listed below this one)
        @Override
        public void onMessageReceived(TextWebSocketFrame message) {
            //No-op
        }

        @Override
        public void send(Object message) {
            HeroReactions.LOGGER.info("Sending subscribe message");
            //create serialized JSON string of the subscribe message and send it off
            MessageHelper.sendJson(JsonMessageHelper.createSerializedSubscribeMessage((SubscribeJsonMessage.SubscribeTopics) message));
        }
    },
    FEEDBACK { //sampled individual feedback events
        @Override
        public void onMessageReceived(TextWebSocketFrame message) {
            //parse json, retrieving data and storing it in the appropriate location in StreamData
            JsonMessageHelper.setMessageData(message, this);
            //manipulate data uniquely
            HeroReactions.LOGGER.info("Got FEEDBACK message: "); //TODO
        }

        @Override
        public void send(Object message) {
            //No-op
        }
    },
    FEEDBACK_ACTIVITY { //aggregated feedback activity events
        @Override
        public void onMessageReceived(TextWebSocketFrame message) {
            //parse json, retrieving data and storing it in the appropriate location in StreamData
            JsonMessageHelper.setMessageData(message, this);
            //manipulate data uniquely
            HeroReactions.LOGGER.info("Got FEEDBACK_ACTIVITY message: "); //TODO
        }

        @Override
        public void send(Object message) {
            //No-op
        }
    },
    ONLINE { //channel online status
        @Override
        public void onMessageReceived(TextWebSocketFrame message) {
            //parse json, retrieving data and storing it in the appropriate location in StreamData
            JsonMessageHelper.setMessageData(message, this);
            //manipulate data uniquely
            HeroReactions.LOGGER.info("Got ONLINE message, is online: "+StreamData.Online.isOnline);
        }

        @Override
        public void send(Object message) {
            //No-op
        }
    },
    VIEWERS { //channel viewer count
        @Override
        public void onMessageReceived(TextWebSocketFrame message) {
            //parse json, retrieving data and storing it in the appropriate location in StreamData
            JsonMessageHelper.setMessageData(message, this);
            //manipulate data uniquely
            HeroReactions.LOGGER.info("Got VIEWER message: [direct] "+StreamData.Viewers.direct);
            HeroReactions.LOGGER.info("Got VIEWER message: [indirect] "+StreamData.Viewers.indirect);

        }

        @Override
        public void send(Object message) {
            //No-op
        }
    },
    NONE {
        @Override
        public void onMessageReceived(TextWebSocketFrame message) {
            //No-op
        }
        @Override
        public void send(Object message) {
            //No-op
        }
    };

    /**
     * Called whenever the message type is received.
     * @param message - the message obtained
     */
    public abstract void onMessageReceived(TextWebSocketFrame message);

    /**
     * Call to send this message type to the server.
     * @param message - the message details to send
     */
    public abstract void send(Object message);

    HeroMessages() {
        //empty constructor
    }

}
