package gr8pefish.heroreactions.hero.data;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.common.client.CommonRenderHelper;
import gr8pefish.heroreactions.hero.network.json.JsonMessageHelper;
import gr8pefish.heroreactions.minecraft.client.MinecraftRenderHelper;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class to interpret the data obtained from the Hero API.
 * Commonly called from {@link JsonMessageHelper}, as that is where the messages/events are received.
 */
public class HeroUtils {

    // Public helper methods

    /**
     * When a feedback message is obtained from the Hero API, do these actions
     */
    public static void interpretFeedbackMessage() {
        //Get per sentiment ratios
        calculateRatios();
        //Get overall activity percentage
        getActivity();
        //Get top feedback
        getTopFeedback();
        //Render new data
        CommonRenderHelper.renderAllFeedbackBubbles();
    }

    /**
     * When an activity (i.e. view count) message is obtained from Hero, do these actions
     */
    public static void interpretViewerMessage() {
        //Set size of display based on view count
        setStageSize();
        //Update view count rendered
        CommonRenderHelper.renderViewCount(HeroData.Viewers.total);
    }


    // Private helper methods


    /**
     * Gets the activity of the feedback as a value 0 < x < 1
     * TODO: Utilize
     */
    private static void getActivity() {
        int totalActivity = sumMapValues(HeroData.FeedbackActivity.getFeedbackActivity());
        totalActivity = totalActivity > 100 ? 100 : totalActivity; //can't be above 100
        HeroData.FeedbackActivity.activity = totalActivity / 100f;
    }

    /**
     * Helper method to add together values of feedback, weighting the primary one by .8 and the rest by .1
     * Numbers taken from JS overlay code, code is my own though.
     *
     * @param map - A map containing the feedback counts as the map's values
     * @return - A number between 0 and 100
     */
    private static int sumMapValues(ConcurrentHashMap<FeedbackTypes, Integer> map){
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

    /**
     * Gets the current top feedback, and if it is different from the previous one
     *
     * TODO: Utilize
     */
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
     *
     * Note: Not called if reactionsActivity == 0, as the feedback message never fires
     */
    private static void calculateRatios() {
        ConcurrentHashMap<FeedbackTypes, Integer> feedbackActivity = HeroData.FeedbackActivity.getFeedbackActivity();
        ConcurrentHashMap<FeedbackTypes, Double> feedbackRatios = HeroData.FeedbackActivity.getFeedbackRatios();
        for (Map.Entry<FeedbackTypes, Integer> entry : feedbackActivity.entrySet()) {
            if (HeroData.FeedbackActivity.totalFeedbackCount == 0) {
                feedbackRatios.put(entry.getKey(), 0d);
            } else {
                //Debug printing
                Common.LOGGER.debug("Activity: "+feedbackActivity.toString());
                Common.LOGGER.debug("Ratios: "+feedbackRatios.toString());
                Common.LOGGER.debug("Total: "+HeroData.FeedbackActivity.totalFeedbackCount);
                Common.LOGGER.debug("Adding: "+entry.getKey() + ": " +(entry.getValue().doubleValue() / HeroData.FeedbackActivity.totalFeedbackCount));
                //Actual executing code
                feedbackRatios.put(entry.getKey(), entry.getValue().doubleValue() / HeroData.FeedbackActivity.totalFeedbackCount);
            }
        }
        Common.LOGGER.info("Feedback ratios created: "+feedbackRatios.toString());
    }

    /**
     * Sets the stage size (i.e. the render area) due to the view count.
     * Note: Formula adopted from the JS overlay code.
     */
    private static void setStageSize() {
        int viewCount = HeroData.Viewers.total < 10 ? HeroData.Viewers.total : HeroData.Viewers.total * 10; //TODO: Debug production
        double scale = Math.min(Math.log(viewCount + 1), 10) / 10; //total view count
        if (scale > 1) scale = 1;
        if (scale < 0) scale = 0;
        MinecraftRenderHelper.stageSize = scale;
        Common.LOGGER.info("Stage size: "+Math.floor(MinecraftRenderHelper.stageSize * 100)+"%");
    }


}
