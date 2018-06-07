package gr8pefish.heroreactions.hero;

import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.hero.data.HeroData;
import gr8pefish.heroreactions.hero.data.enums.Reactions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HeroUtils {

    /**
     * Calculates the ratios of reactions received of each type as a function of the total reaction obtained.
     * Stores it in a {@link Map} in {@link HeroData#feedbackRatios}
     */
    public static void calculateRatios() { //TODO: Not being called if reactionsActivity == 0, since that message never fires, so used via test data mostly
        ConcurrentHashMap<Reactions, Integer> feedbackActivity = HeroData.FeedbackActivity.getFeedbackActivity();
        ConcurrentHashMap<Reactions, Float> feedbackRatios = HeroData.FeedbackActivity.getFeedbackRatios();
        for (Map.Entry<Reactions, Integer> entry : feedbackActivity.entrySet()) {
            feedbackRatios.put(entry.getKey(), entry.getValue().floatValue() / HeroData.FeedbackActivity.totalFeedbackCount);
        }
        HeroReactions.LOGGER.info(feedbackRatios.toString());
    }

}
