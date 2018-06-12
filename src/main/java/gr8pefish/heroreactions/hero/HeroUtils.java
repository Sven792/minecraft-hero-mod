package gr8pefish.heroreactions.hero;

import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.client.gui.ReactionRenderHelper;
import gr8pefish.heroreactions.hero.data.HeroData;
import gr8pefish.heroreactions.hero.data.enums.Reactions;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class to interpret the data obtained from the Hero API.
 * Commonly called from {@link gr8pefish.heroreactions.network.hero.json.JsonMessageHelper}, as that is where the messages/events are received.
 */
public class HeroUtils {

    public static void interpretFeedbackMessage() {
        //Get per sentiment ratios
        calculateRatios();
        //Get overall activity percentage
        getActivity();
        //Get top feedback
        getTopFeedback();
    }

    public static void interpretViewerMessage() {
        setStageSize();
    }

    //gets the activity of the feedback as a value 0 < x < 1
    private static void getActivity() {
        int totalActivity = sumMapValues(HeroData.FeedbackActivity.getFeedbackActivity());
        totalActivity = totalActivity > 100 ? 100 : totalActivity; //can't be above 100
        HeroData.FeedbackActivity.activity = totalActivity / 100f;
    }

    //Add together values of feedback, weighting the primary one by .8 and the rest by .1
    private static int sumMapValues(ConcurrentHashMap<Reactions, Integer> map){
        Collection<Integer> values = map.values();
        int largest = Integer.MIN_VALUE;
        float sum = 0;
        for (Integer value : values) {
            if(value > largest){
                largest = value;
            }
            sum += ((float)value) * 0.1f;
        }
        sum += ((float)largest) * 0.7;
        return (int) Math.ceil(sum);

        //Lambda code (fancy syntax, but two iterations, so slower). Leaving here in case you want to switch it in (untested).
//        int max = feedbackActivity.values().stream().max(Integer::compareTo).get();
//        double result = feedbackActivity.values().stream().map(it -> max * (it == max ? 0.8 : 0.1)).reduce((a, b) -> a + b).get();
    }

    private static void getTopFeedback() {

//            this.emitter.emit(EVENT_NAMES.popup, {
//                    id: topFeedback.id,
//                    activity: topFeedback.activity / 100,
//             });

        //render image if current top feedback not the same as previous top feedback
        HeroData.FeedbackActivity.renderTopFeedback = HeroData.FeedbackActivity.previousTopFeedback != null && HeroData.FeedbackActivity.previousTopFeedback != HeroData.FeedbackActivity.currentTopFeedback;
        //set old top feedback to current (for next time through)
        HeroData.FeedbackActivity.previousTopFeedback = HeroData.FeedbackActivity.currentTopFeedback;


    }

    /**
     * Calculates the ratios of reactions received of each type as a function of the total reaction obtained.
     * Stores it in a {@link Map} in {@link HeroData#feedbackRatios}
     */
    private static void calculateRatios() { //TODO: Not being called if reactionsActivity == 0, since that message never fires, so used via test data mostly
        ConcurrentHashMap<Reactions, Integer> feedbackActivity = HeroData.FeedbackActivity.getFeedbackActivity();
        ConcurrentHashMap<Reactions, Float> feedbackRatios = HeroData.FeedbackActivity.getFeedbackRatios();
        for (Map.Entry<Reactions, Integer> entry : feedbackActivity.entrySet()) {
            feedbackRatios.put(entry.getKey(), entry.getValue().floatValue() / HeroData.FeedbackActivity.totalFeedbackCount);
        }
        HeroReactions.LOGGER.info(feedbackRatios.toString());
    }

    private static void setStageSize() {
        ReactionRenderHelper.stageSize = Math.min(Math.log(HeroData.Viewers.total + 1), 10) / 10; //total view count
    }


}
