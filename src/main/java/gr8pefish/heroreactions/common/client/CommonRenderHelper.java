package gr8pefish.heroreactions.common.client;

import gr8pefish.heroreactions.hero.client.TransformationTypes;
import gr8pefish.heroreactions.hero.client.elements.Bubble;
import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.hero.data.HeroData;
import gr8pefish.heroreactions.minecraft.client.MinecraftRenderHelper;
import gr8pefish.heroreactions.minecraft.client.gui.GuiReactions;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Theoretically you just replace the Minecraft/GL specific calls with your specific rendering setup and the rest should work.*/
public class CommonRenderHelper {

    //update view counter
    public static void renderViewCount(int viewcount) {
        MinecraftRenderHelper.renderViewCount(viewcount);
    }

    //add bubbles to render
    public static void renderAllFeedbackBubbles() {
        MinecraftRenderHelper.updateSpawnBoxForStageSize();
//        MinecraftRenderHelper.clearOldBubbles(); //TODO: Include?
        for (Map.Entry<FeedbackTypes, Double> entry : HeroData.FeedbackActivity.getFeedbackRatios().entrySet()) {
            //render bubbling reactions, with an amount depending on how large this is
            int renderCount = getCountToRender(entry.getValue());
            for (int i = 0; i < renderCount; i++) {
                MinecraftRenderHelper.addBubble(entry.getKey());
            }
        }
    }

    //render 10x the proportion (e.g. 0.5 -> 5), multiplied by the stage size (usually hovers around 50%), so should be ~5 total rendered at one time //TODO: refine a lot
    private static int getCountToRender(double feedbackRatio) {
        return (int) Math.floor(feedbackRatio * 10 * (MinecraftRenderHelper.stageSize * 1.75 > 1 ? 1 : MinecraftRenderHelper.stageSize * 1.75));
    }

    //---------------------------------------

    //apply all transformation effects
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
        //TODO
    }

    //render bubble
    public static void renderBubble(Bubble bubble) {
        MinecraftRenderHelper.renderBubble(bubble);
    }


    //---------------------------------------- USELESS? BELOW HERE? --------------------------------------

    public static void renderAllFeedbackBubblesWithTransformations(ConcurrentHashMap<FeedbackTypes, Double> feedbackRatios, List<TransformationTypes> transformationTypes, long timeDifference) {

        //push matrix
        GlStateManager.pushMatrix();

        //loop through each entry
        for (Map.Entry<FeedbackTypes, Double> entry : feedbackRatios.entrySet()) {
            renderFeedbackBubbleWithTransformations(entry.getKey(), entry.getValue(), transformationTypes, timeDifference);
        }

        //pop matrix
        GlStateManager.popMatrix();
    }


    private static void renderFeedbackBubbleWithTransformations(FeedbackTypes feedbackType, double feedbackRatio, List<TransformationTypes> transformationTypes, long timeDifference) {

        //loop through each transformation type and apply it
        for (TransformationTypes transformationType : transformationTypes) {
//            transformationType.apply(timeDifference);
        }

        //render the bubble
        renderFeedbackBubble(feedbackType, feedbackRatio);
    }

    private static void renderFeedbackBubble(FeedbackTypes feedbackType, double feedbackRatio) {
        //render bubbling reactions, with an amount depending on how large this is
        int renderCount = getCountToRender(feedbackRatio);
        for (int i = 0; i < renderCount; i++) {
//            MinecraftRenderHelper.renderFeedbackBubble(feedbackType);
        }
    }


}
