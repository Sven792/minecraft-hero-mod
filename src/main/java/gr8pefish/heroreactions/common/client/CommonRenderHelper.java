package gr8pefish.heroreactions.common.client;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.hero.client.TransformationTypes;
import gr8pefish.heroreactions.hero.client.elements.Bubble;
import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.hero.data.HeroData;
import gr8pefish.heroreactions.minecraft.client.MinecraftRenderHelper;
import gr8pefish.heroreactions.minecraft.config.ConfigHandler;

import java.util.Map;

/**
 * Class for all the common rendering code, where Hero and Minecraft overlap.
 * Theoretically you just replace the Minecraft/GL specific calls with your specific rendering setup and the rest should work.
 */
public class CommonRenderHelper {

    // General

    //update view counter

    /**
     * Show the view counter.
     *
     * @param viewcount - the number of viewers to display
     */
    public static void renderViewCount(int viewcount) {
        MinecraftRenderHelper.renderViewCount(viewcount);
    }

    /**
     * Render all the feedback reaction bubbles.
     * Current implementation adds them to a render list, and the Minecraft-specific implementation deals with the rest.
     */
    public static void renderAllFeedbackBubbles() {
        //Update stage size
        MinecraftRenderHelper.updateSpawnBoxForStageSize();
        //Clear old data/bubbles TODO: Redundant?
//        MinecraftRenderHelper.clearOldBubbles();
        //Loop through all feedback types
        for (Map.Entry<FeedbackTypes, Double> entry : HeroData.FeedbackActivity.getFeedbackRatios().entrySet()) {
            //get how many of each type to display
            int renderCount = getCountToRender(entry.getValue(), entry.getValue() * HeroData.FeedbackActivity.totalFeedbackCount);
            Common.LOGGER.debug(entry.getKey()+ " -> " +renderCount);
            //loop through that amount
            for (int i = 0; i < renderCount; i++) {
                //add each to a render list to display next render tick
                MinecraftRenderHelper.addBubbleToRenderList(entry.getKey());
            }
        }
    }

    /**
     * Render a single bubble.
     *
     * @param bubble - the feedback emoji bubble to render
     */
    public static void renderBubble(Bubble bubble) {
        MinecraftRenderHelper.renderBubble(bubble);
    }

    /**
     * Get the amount of each feedback type to render
     * Complex algorithm that takes into account the percentage of each as a fraction of the total, the count of each inputted, and the stage size.
     *
     * @param percent - the proportion of this feedback type as a ratio of all current feedback types (0 - 1, inclusive)
     * @param countInput - the count of how many time this bubble would appear with no modifications
     * @return - the count of how many bubbles of this type to display
     */
    private static int getCountToRender(double percent, double countInput) {

        //minimum and maximum boundings for the bubble count (recommended: no less/more than 1-10)
        int totalMaxBubbles = ConfigHandler.overlayConfigSettings.maxBubblesOfEachType;
        int totalMinBubbles = ConfigHandler.overlayConfigSettings.minBubblesOfEachType;

        // get the maximum number of bubbles to show at one time, bound to the max and min, and multiplied slightly by the stage size
        int maxBubblesAtOnce = (int) Math.ceil(totalMaxBubbles - ((1 - MinecraftRenderHelper.stageSize) * totalMinBubbles)); //constant multiplied slightly by stage size factor (bound totalMin -> totalMax)
        // take into account the actual inputted count, and modify that by it's percentage, limiting it to the value obtained before
        int countOutput = (countInput * percent) > maxBubblesAtOnce ? maxBubblesAtOnce : (int) Math.ceil(countInput * percent);
        // finally, take the count and multiply it by the percentage as a proportion of the total bubbles to get the end result (useful mostly in the case when the maxBubblesAtOnce are used)
        return (int)(percent * countOutput);
    }

    // Transformations

    //apply all transformation effects to a given bubble
    public static void applyEffects(TransformationTypes[] transformationTypes, Bubble bubble) {
        for (TransformationTypes type : transformationTypes) {
            type.apply(bubble);
        }
    }

    //set transparency
    public static void applyFade(Bubble bubble) {
        MinecraftRenderHelper.applyOpacity(bubble);
    }

    //set size
    public static void applyExpand(Bubble bubble) {
        MinecraftRenderHelper.applySize(bubble);
    }

    //set rotation
    public static void applyRotate(Bubble bubble) {
        MinecraftRenderHelper.applyRotation(bubble);
    }

    //set position
    public static void applySlide(Bubble bubble) {
        MinecraftRenderHelper.applyMove(bubble);
    }

}
