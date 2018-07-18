package gr8pefish.heroreactions.hero.data;

import java.util.concurrent.ConcurrentHashMap;

/**
 * A class to hold all of the received data in a central location for easy access.
 */
public class HeroData {

    //Feedback mappings

    //Reaction -> count
    private static final ConcurrentHashMap<FeedbackTypes, Integer> feedbackActivity = new ConcurrentHashMap<>();
    //Reaction -> ratio (count/total)
    private static final ConcurrentHashMap<FeedbackTypes, Double> feedbackRatios = new ConcurrentHashMap<>();

    public static class Viewers {
        public static int direct; //directly viewing the stream
        public static int indirect; //indirect views (via things like other players)
        public static int total; //indirect + direct
    }

    public static class Online {
        public static boolean isOnline;
    }

    //Single feedback message - unused
    public static class Feedback {
        public static FeedbackTypes feedbackType;
        public static int count;
    }

    //Aggregated feedback - used
    public static class FeedbackActivity {
        //Use feedbackActivity map
        public static ConcurrentHashMap<FeedbackTypes, Integer> getFeedbackActivity() {
            return feedbackActivity;
        }
        //Use feedbackRatios map
        public static ConcurrentHashMap<FeedbackTypes, Double> getFeedbackRatios() {
            return feedbackRatios;
        }

        //totals
        public static int totalFeedbackCount; //the sum of the feedback counts
        public static double activity; //the total activity ratio (0 < x < 1)

        //top //TODO: Utilize
        public static FeedbackTypes previousTopFeedback; //the previous highest feedback type
        public static FeedbackTypes currentTopFeedback; //the present highest feedback type
        public static boolean renderTopFeedback; //if the top feedback changed, render it
    }

}
