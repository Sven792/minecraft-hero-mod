package gr8pefish.heroreactions.network.hero.message;

import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.hero.data.HeroData;
import gr8pefish.heroreactions.hero.data.enums.Reactions;
import gr8pefish.heroreactions.network.hero.json.JsonMessageHelper;
import gr8pefish.heroreactions.network.hero.json.variants.PingPongJsonMessage;
import gr8pefish.heroreactions.network.hero.json.variants.SubscribeJsonMessage;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum HeroMessages {

    PING {
        @Override
        public void onMessageReceived(TextWebSocketFrame message) {
            //when ping received, send a pong back
            PONG.send(PingPongJsonMessage.PingPongEnum.PONG);
        }
        @Override
        public void send(Object message) {
            HeroReactions.LOGGER.debug("Sending ping");
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
            HeroReactions.LOGGER.debug("Sending pong");
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
            HeroReactions.LOGGER.debug("Sending text message");
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
            HeroReactions.LOGGER.debug("Sending subscribe message");
            //create serialized JSON string of the subscribe message and send it off
            MessageHelper.sendJson(JsonMessageHelper.createSerializedSubscribeMessage((SubscribeJsonMessage.SubscribeTopics) message));
        }
    },
    FEEDBACK { //sampled individual feedback events
        @Override
        public void onMessageReceived(TextWebSocketFrame message) {
            //parse json, retrieving data and storing it in the appropriate location in HeroData
            JsonMessageHelper.setMessageData(message, this);
            //manipulate data uniquely
            HeroReactions.LOGGER.debug("Got FEEDBACK message: [type] "+ HeroData.Feedback.reaction.toString());
            HeroReactions.LOGGER.debug("Got FEEDBACK message: [count] "+ HeroData.Feedback.count);
        }

        @Override
        public void send(Object message) {
            //No-op
        }
    },
    FEEDBACK_ACTIVITY { //aggregated feedback activity events
        @Override
        public void onMessageReceived(TextWebSocketFrame message) {
            //parse json, retrieving data and storing it in the appropriate location in HeroData
            JsonMessageHelper.setMessageData(message, this);
            //manipulate data uniquely
            HeroReactions.LOGGER.debug("Got FEEDBACK_ACTIVITY message");
            //get data, iterate through
            ConcurrentHashMap<Reactions, Integer> feedback = HeroData.FeedbackActivity.getFeedbackActivity();
            for (Map.Entry<Reactions, Integer> entry : feedback.entrySet()) {
                HeroReactions.LOGGER.debug("Feedback: "+entry.getKey().toString()+" - "+entry.getValue()); //feedback type - count
            }
        }

        @Override
        public void send(Object message) {
            //No-op
        }
    },
    ONLINE { //channel online status
        @Override
        public void onMessageReceived(TextWebSocketFrame message) {
            //parse json, retrieving data and storing it in the appropriate location in HeroData
            JsonMessageHelper.setMessageData(message, this);
            //manipulate data uniquely
            HeroReactions.LOGGER.debug("Got ONLINE message, is online: "+ HeroData.Online.isOnline);
        }

        @Override
        public void send(Object message) {
            //No-op
        }
    },
    VIEWERS { //channel viewer count
        @Override
        public void onMessageReceived(TextWebSocketFrame message) {
            //parse json, retrieving data and storing it in the appropriate location in HeroData
            JsonMessageHelper.setMessageData(message, this);
            //manipulate data uniquely
            HeroReactions.LOGGER.debug("Got VIEWER message: [direct] "+ HeroData.Viewers.direct);
            HeroReactions.LOGGER.debug("Got VIEWER message: [indirect] "+ HeroData.Viewers.indirect);

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
