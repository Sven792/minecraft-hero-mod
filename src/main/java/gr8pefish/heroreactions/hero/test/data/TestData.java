package gr8pefish.heroreactions.hero.test.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.hero.network.message.HeroMessages;

public class TestData {

    private HeroMessages messageType;
    private JsonElement message;

    public TestData() {
        setFeedbackTopData();
    }


    private void setFeedbackTopData() {
        this.messageType = HeroMessages.FEEDBACK_ACTIVITY;

        //set up data
        FeedbackTopMessage.FeedbackOptions[] feedbackOptions = new FeedbackTopMessage.FeedbackOptions[]{
                new FeedbackTopMessage.FeedbackOptions("applause", 12),
                new FeedbackTopMessage.FeedbackOptions("laughter", 6),
                new FeedbackTopMessage.FeedbackOptions("anger", 2)
        };

        //set as json data
        this.message = new Gson().toJsonTree(new FeedbackTopMessage(feedbackOptions));
    }

    public JsonElement getMessage() {
        return message;
    }

    public HeroMessages getMessageType() {
        return messageType;
    }
}
