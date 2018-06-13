package gr8pefish.heroreactions.common.client;

import gr8pefish.heroreactions.hero.client.TransformationTypes;
import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.minecraft.client.MinecraftRenderHelper;
import gr8pefish.heroreactions.minecraft.client.gui.GuiReactions;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Theoretically you just replace the Minecraft/GL specific calls with your specific rendering setup and the rest should work.*/
public class CommonRenderHelper {


    public static void renderViewCount(int viewcount) {
        MinecraftRenderHelper.renderViewCount(viewcount);
    }


    //----------------------------------------



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
            transformationType.apply(timeDifference);
        }

        //render the bubble
        renderFeedbackBubble(feedbackType, feedbackRatio);
    }

    private static void renderFeedbackBubble(FeedbackTypes feedbackType, double feedbackRatio) {
        //render bubbling reactions, with an amount depending on how large this is
        int renderCount = getCountToRender(feedbackRatio);
        for (int i = 0; i < renderCount; i++) {
            MinecraftRenderHelper.renderFeedbackBubble(feedbackType);
        }
    }

    //render 10x the proportion (0.5 -> 5), so should be ~10 total rendered at one time //TODO: refine
    private static int getCountToRender(double feedbackRatio) {
        return (int) Math.floor(feedbackRatio * 10);
    }


    //---------------------------------------


    public static void applyFade(long timeDifference) {
        //set transparency
        MinecraftRenderHelper.applyOpacity(timeDifference);
    }

    public static void applyExpand(long timeDifference) {
        //set size
        MinecraftRenderHelper.applySize(timeDifference);
    }

    public static void applySlide(long timeDifference) {
        //TODO
    }
}
