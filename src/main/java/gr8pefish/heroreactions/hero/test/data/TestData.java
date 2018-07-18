package gr8pefish.heroreactions.hero.test.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import gr8pefish.heroreactions.hero.network.message.HeroMessages;

import java.util.concurrent.ConcurrentHashMap;

/**
 * The test data used in a simulated message.
 *
 * Contains an incrementing view count and a feedback message.
 * Helper classes of {@link FeedbackTopMessage} and {@link ViewersMessage} are used.
 */
public class TestData {

    private ConcurrentHashMap<HeroMessages, JsonElement> messages = new ConcurrentHashMap<>();
    private ViewersMessage viewersMessage;
    private int viewCount = 10;

    private Gson gson = new Gson();

    public TestData() {
        setViewersData();
        setFeedbackTopData();
    }

    //Private

    //Alter these values manually to change the test data set
    private void setFeedbackTopData() {
        //set up data
        FeedbackTopMessage.FeedbackOptions[] feedbackOptions = new FeedbackTopMessage.FeedbackOptions[]{
                new FeedbackTopMessage.FeedbackOptions("laughter", 12),
                new FeedbackTopMessage.FeedbackOptions("applause", 6),
                new FeedbackTopMessage.FeedbackOptions("love", 2)
        };

        //set as json data
        this.messages.put(HeroMessages.FEEDBACK_ACTIVITY, gson.toJsonTree(new FeedbackTopMessage(feedbackOptions)));
    }

    private void setViewersData() {
        viewersMessage = new ViewersMessage(viewCount, 5);
        this.messages.put(HeroMessages.VIEWERS, gson.toJsonTree(viewersMessage));
    }

    //Public

    public ConcurrentHashMap<HeroMessages, JsonElement> getMessages() {
        return messages;
    }

    public void incrementViewerCount() {
        //if > x reset, otherwise increment
        int incrementViewersAmount = 50;
        viewCount = viewCount + incrementViewersAmount > 565 ? 0 : viewCount + incrementViewersAmount;
        viewersMessage = new ViewersMessage(viewCount, 5);
        this.messages.replace(HeroMessages.VIEWERS, gson.toJsonTree(viewersMessage));
    }

}
