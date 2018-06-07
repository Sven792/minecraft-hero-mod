package gr8pefish.heroreactions.network.hero;

import gr8pefish.heroreactions.hero.data.enums.Reactions;

import java.util.concurrent.ConcurrentHashMap;

/**
 * A class to hold all of the received data in a central location for easy access.
 */
public class HeroData {

    //Might as well make it thread safe
    private static final ConcurrentHashMap<Reactions, Integer> feedbackActivity = new ConcurrentHashMap<>();

    public static class Viewers {
        public static int direct;
        public static int indirect;
    }

    public static class Online {
        public static boolean isOnline;
    }

    public static class Feedback {
        public static Reactions reaction;
        public static int count;
    }

    public static class FeedbackActivity {
        //Use feedbackActivity map
        public static ConcurrentHashMap<Reactions, Integer> getFeedbackActivity() {
            return feedbackActivity;
        }
    }

}
