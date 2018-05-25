package gr8pefish.heroreactions.network.hero.message.data;

import java.util.HashMap;

public class StreamData {

    private static HashMap<FeedbackTypes, Integer> feedbackActivity = new HashMap<>();

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
    }

}
