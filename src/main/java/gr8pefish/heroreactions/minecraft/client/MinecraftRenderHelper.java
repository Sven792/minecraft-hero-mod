package gr8pefish.heroreactions.minecraft.client;

import gr8pefish.heroreactions.hero.client.elements.Bubble;
import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.hero.data.HeroData;
import gr8pefish.heroreactions.hero.data.HeroUtils;
import gr8pefish.heroreactions.minecraft.client.gui.GuiReactions;

public class MinecraftRenderHelper {

    /**set by {@link HeroUtils#setStageSize()} during a viewer message event. Bound between 0 and 1 (inclusive) */
    public static double stageSize = 1; //TODO: Determine glow intensity/spawn box size/reaction count via more refined algorithm than just stageSize (i.e. viewCount)

    public static void renderBubble(Bubble bubble) {
        getReactionOverlay().renderFeedbackBubbleOnly(bubble);
    }

    public static void applyOpacity(Bubble bubble) {
        getReactionOverlay().setOpacity(bubble);
    }

    public static void applySize(Bubble bubble) {
        getReactionOverlay().setSize(bubble);
    }

    public static void applyRotation(Bubble bubble) {
        getReactionOverlay().setRotation(bubble);
    }

    public static void renderViewCount(int viewCount) {
        //update the data to show, will automatically render new info next view tick
        ClientEventHandler.overlay.setViewCount(viewCount);
    }

    public static void addBubble(FeedbackTypes type) {
        //add a new bubble to the list to render
        getReactionOverlay().addTestBubble(type);
    }

    public static void clearOldBubbles() {
        //clear bubbles from render list
        //update time
        ClientEventHandler.overlay.updateTime();
        //remove if relevant
        getReactionOverlay().removeOldBubbles();
    }


    public static void updateSpawnBoxForStageSize() {
        //rescale spawn area to fit
        ClientEventHandler.overlay.getGuiLocation().applyStageSizeScaling(stageSize);
    }


    //Helper method
    private static GuiReactions getReactionOverlay() {
        return ClientEventHandler.overlay.getReactions();
    }

    // =============== NOTES ================

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


    // More NOTES

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
