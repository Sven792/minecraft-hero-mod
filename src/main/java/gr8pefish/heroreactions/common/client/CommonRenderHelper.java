package gr8pefish.heroreactions.common.client;

import gr8pefish.heroreactions.hero.client.TransformationTypes;
import gr8pefish.heroreactions.hero.client.elements.Bubble;
import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.hero.data.HeroData;
import gr8pefish.heroreactions.minecraft.client.MinecraftRenderHelper;

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
            int renderCount = getCountToRender(entry.getValue());
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
     * TODO: Refine - a lot
     *
     * @param feedbackRatio - the proportion of this feedback type as a ratio of all current feedback types (0 - 1, inclusive)
     * @return - the count of how many bubbles of this type to display
     */
    private static int getCountToRender(double feedbackRatio) {
        double x = MinecraftRenderHelper.stageSize * 1.75 > 1 ? 1 : MinecraftRenderHelper.stageSize * 1.75;
        double y = Math.floor(feedbackRatio * 10 * x);
        return (int) y;
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
