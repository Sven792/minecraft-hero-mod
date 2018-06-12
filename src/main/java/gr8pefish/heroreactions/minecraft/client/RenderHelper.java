package gr8pefish.heroreactions.minecraft.client;

import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.hero.data.HeroUtils;
import gr8pefish.heroreactions.hero.data.HeroData;
import gr8pefish.heroreactions.minecraft.client.gui.GuiReactions;
import net.minecraft.client.renderer.GlStateManager;

public class RenderHelper {

    //fields
    /**set by {@link HeroUtils#setStageSize()} during a viewer message event */
    public static double stageSize = -1;

    //time tracker
    private final int MAX_RANDOM_MS = 500;

    final double waitTime = ( //int?
            16
            + (
                500
                * (1 / (HeroData.FeedbackActivity.activity * 3)) //
                * (1 / (Math.max(stageSize, 0.1) * 5))
//                * (1 / (percentage * 5)) //ratio of each reaction to total
                * (0.6 + (Math.random() * 0.8))
                )
            );

    public static void setOpacity(double currentTime, double totalTime) {
        GlStateManager.enableAlpha(); //can cause weird transparent cutout issues, but positive affects performance (dependent on transparent pixel %) if no issues present
        GlStateManager.enableBlend(); //enable blending
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA); //black magic that is necessary
        float opacity = (float) (currentTime / totalTime); //TODO: will need to halve this eventually, for fade out as well (currently just fade-in)
        GlStateManager.color(1, 1, 1, opacity); //1=fully opaque, 0=fully transparent
    }

    public static void setSize(double currentTime, double totalTime) {
        //TODO
    }

    public static void setPosition(double currentTime, double totalTime) {
        //TODO
    }

    public static void render(FeedbackTypes feedbackType) {
        //bind texture
        //TODO: How Do?
        //render in location
        //TODO
    }


//    if (timediff >= waitTime && percentage !== 0 && this.activity !== 0)
//    this.bubbleTimestamps[type] = timestamp; //emit
//    this.bubbleTimestamps[type] = timestamp + (Math.random() * MAX_RANDOM_MS); //pass

    //fade in


    //fade out


    //expand image


    //contract image


    //translate up


    //translate down


    //translate
}
