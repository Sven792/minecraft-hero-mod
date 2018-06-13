package gr8pefish.heroreactions.common.client;

import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.minecraft.client.MinecraftRenderHelper;
import net.minecraft.client.renderer.GlStateManager;

/** Theoretically you just replace the Minecraft/GL specific calls with your specific rendering setup and the rest should work.*/
public class CommonRenderHelper {

    public static void renderFade(long currentTime, long timeDifference, long baseTime, FeedbackTypes feedbackType, double feedbackRatioOfTotal) {
        //push matrix
        GlStateManager.pushMatrix();

        //set transparency
        MinecraftRenderHelper.setOpacity(currentTime, timeDifference, baseTime);

        //helper method
        renderFeedbackBubble(feedbackType, feedbackRatioOfTotal);

        //pop matrix
        GlStateManager.popMatrix();
    }


    public static void renderExpand(long timeDifference, FeedbackTypes feedbackType, double feedbackRatioOfTotal) {
        //push matrix
        GlStateManager.pushMatrix();

        //set size
        MinecraftRenderHelper.setSize(timeDifference);

        //helper method
        renderFeedbackBubble(feedbackType, feedbackRatioOfTotal);

        //pop matrix
        GlStateManager.popMatrix();

    }

    public static void renderSlide(double currentTime, double totalTime, FeedbackTypes feedbackType, double feedbackRatioOfTotal) {
        //push matrix
        GlStateManager.pushMatrix();

        //set size
        MinecraftRenderHelper.setPosition(currentTime, totalTime);

        //helper method
        renderFeedbackBubble(feedbackType, feedbackRatioOfTotal);

        //pop matrix
        GlStateManager.popMatrix();
    }

    private static void renderFeedbackBubble(FeedbackTypes feedbackType, double feedbackRatioOfTotal) {
        //render bubbling reactions, with an amount depending on how large this is
        int renderCount = getCountToRender(feedbackRatioOfTotal);
        for (int i = 0; i < renderCount; i++) {
            MinecraftRenderHelper.renderFeedbackBubble(feedbackType);
        }
    }

    //render 10x the proportion (0.5 -> 5), so should be ~10 total rendered at one time //TODO: refine
    private static int getCountToRender(double feedbackRatioOfTotal) {
        return (int) Math.floor(feedbackRatioOfTotal * 10);
    }

    public static void renderViewCount(int viewcount) {
        MinecraftRenderHelper.renderViewCount(viewcount);
    }

}
