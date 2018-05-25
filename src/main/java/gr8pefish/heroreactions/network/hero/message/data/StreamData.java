package gr8pefish.heroreactions.network.hero.message.data;

import java.util.concurrent.ConcurrentHashMap;

public class StreamData {

    //Might as well make it thread safe
    private static final ConcurrentHashMap<FeedbackTypes, Integer> feedbackActivity = new ConcurrentHashMap<>();

    public static class Viewers {
        public static int direct;
        public static int indirect;
    }

    public static class Online {
        public static boolean isOnline;
    }

    public static class Feedback {
        public static FeedbackTypes feedbackType;
        public static int count;
    }

    public static class FeedbackActivity {

        //Use feedbackActivity map
        public static ConcurrentHashMap<FeedbackTypes, Integer> getFeedbackActivity() {
            return feedbackActivity;
        }

    }

}
