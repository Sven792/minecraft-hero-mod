package gr8pefish.heroreactions.hero.client;

import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.hero.data.HeroData;
import scala.actors.threadpool.Arrays;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RenderingUtils {

//    actualValue = previousTickValue + (currentValue - lastTickValue) * partialTicks

    //1777 lines of JS code to recreate in Java
        //need to understand how it works very well, know how to recreate that logic in another syntax including making it work with my own data structures and unique rendering platform (Minecraft)
            //that's a lot of work

    //TODO: Not just reactions, but whole overlay including view count and glow
        //How to organize this (code structure) best?

    /**
     * Render a default reaction set, with a default set of transformation types.
     *
     * @param currentTime
     * @param totalTime
     */
    public static void renderAllReactionsDefault(double currentTime, double totalTime) {
        //Get feedback ratios from data
        ConcurrentHashMap<FeedbackTypes, Double> feedbackRatios = HeroData.FeedbackActivity.getFeedbackRatios();
        //Use Fade/Expand transformation types for now
        List<TransformationTypes> transformationTypes = Arrays.asList(new TransformationTypes[]{TransformationTypes.FADE, TransformationTypes.EXPAND});
        //pass through to method
        renderAllReactions(currentTime, totalTime, feedbackRatios, transformationTypes);
    }


    /**
     * Render a set of reactions with a given set of transformation types.
     *
     * @param currentTime
     * @param totalTime
     * @param feedbackRatios
     * @param transformationTypes
     */
    public static void renderAllReactions(double currentTime, double totalTime, ConcurrentHashMap<FeedbackTypes, Double> feedbackRatios, List<TransformationTypes> transformationTypes) {
        for (Map.Entry<FeedbackTypes, Double> feedbackEntry : feedbackRatios.entrySet()) {
            renderReaction(currentTime, totalTime, feedbackEntry.getKey(), feedbackEntry.getValue(), transformationTypes);
        }
    }

    /**
     * Render a single reaction with a given set of transformation types.
     *
     * @param currentTime
     * @param totalTime
     * @param feedbackType
     * @param feedbackRatioOfTotal
     * @param transformationTypes
     */
    public static void renderReaction(double currentTime, double totalTime, FeedbackTypes feedbackType, double feedbackRatioOfTotal, List<TransformationTypes> transformationTypes) {
        for (TransformationTypes transformationType : transformationTypes) {
            transformationType.render(currentTime, totalTime, feedbackType, feedbackRatioOfTotal);
        }
    }

}
