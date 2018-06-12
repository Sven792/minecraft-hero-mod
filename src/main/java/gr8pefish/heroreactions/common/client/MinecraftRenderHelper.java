package gr8pefish.heroreactions.common.client;

import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.minecraft.client.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;

public class MinecraftRenderHelper {

    public static void renderFade(double currentTime, double totalTime, FeedbackTypes feedbackType, double feedbackRatioOfTotal) {
        //push matrix
        GlStateManager.pushMatrix();

        //set transparency
        RenderHelper.setOpacity(currentTime, totalTime);

        //helper method
        render(feedbackType, feedbackRatioOfTotal);

        //pop matrix
        GlStateManager.popMatrix();
    }


    public static void renderExpand(double currentTime, double totalTime, FeedbackTypes feedbackType, double feedbackRatioOfTotal) {
        //push matrix
        GlStateManager.pushMatrix();

        //set size
        RenderHelper.setSize(currentTime, totalTime);

        //helper method
        render(feedbackType, feedbackRatioOfTotal);

        //pop matrix
        GlStateManager.popMatrix();

    }

    public static void renderSlide(double currentTime, double totalTime, FeedbackTypes feedbackType, double feedbackRatioOfTotal) {
        //push matrix
        GlStateManager.pushMatrix();

        //set size
        RenderHelper.setPosition(currentTime, totalTime);

        //helper method
        render(feedbackType, feedbackRatioOfTotal);

        //pop matrix
        GlStateManager.popMatrix();
    }

    private static void render(FeedbackTypes feedbackType, double feedbackRatioOfTotal) {
        //render bubbling reactions, with an amount depending on how large this is
        int renderCount = getCountToRender(feedbackRatioOfTotal);
        for (int i = 0; i < renderCount; i++) {
            RenderHelper.render(feedbackType);
        }
    }

    //render 10x the proportion (0.5 -> 5), so should be ~10 total rendered at one time //TODO: refine
    private static int getCountToRender(double feedbackRatioOfTotal) {
        return (int) Math.floor(feedbackRatioOfTotal * 10);
    }

}
