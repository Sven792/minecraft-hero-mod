package gr8pefish.heroreactions.minecraft.client;

import gr8pefish.heroreactions.hero.client.elements.Bubble;
import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.hero.data.HeroUtils;
import gr8pefish.heroreactions.minecraft.client.gui.overlay.GuiReactions;

/**
 * Helper class to delegate all the common calls to the internal rendering methods.
 * THis abstraction allows for the internal implementation to change wildly, and all that has to change here is a method call.
 */
public class MinecraftRenderHelper {


    // Fields


    /**
     * Determines how large of an area to render everything in.
     *
     * Set by {@link HeroUtils#setStageSize()} during a viewer message event.
     * Bound between 0 and 1 (inclusive).
     */
    public static double stageSize = 1; //TODO: Determine glow intensity/spawn box size/reaction count via more refined algorithm than just stageSize (i.e. viewCount)


    // Public Methods


    //Render bubble without effects
    public static void renderBubble(Bubble bubble) {
        getReactionOverlay().renderFeedbackBubbleOnly(bubble);
    }

    //Add transparency effect
    public static void applyOpacity(Bubble bubble) {
        getReactionOverlay().setOpacity(bubble);
    }

    //Add growth/shrink effect
    public static void applySize(Bubble bubble) {
        getReactionOverlay().setSize(bubble);
    }

    //Add rotation effect
    public static void applyRotation(Bubble bubble) {
        getReactionOverlay().setRotation(bubble);
    }

    //Add movement effect - unimplemented
    public static void applyMove(Bubble bubble) {
        getReactionOverlay().setPosition(bubble);
    }

    //Update view count, it will automatically render this info next render tick
    public static void renderViewCount(int viewCount) {
        ClientEventHandler.overlay.setViewCount(viewCount);
    }

    //Add a new bubble to the render list
    public static void addBubbleToRenderList(FeedbackTypes type) {
        getReactionOverlay().addBubbleToRenderList(type);
    }

    //Not currently necessary, but keeping in case that changes
    //At one point is was called inside CommonRenderHelper#renderAllFeedbackBubbles to clear old data before adding the new
    public static void clearOldBubbles() {
        //clear bubbles from render list
        //update time
        ClientEventHandler.overlay.updateTime();
        //remove if relevant
        getReactionOverlay().removeOldBubbles();
    }

    //Rescale spawn area to fit the current stage size
    public static void updateSpawnBoxForStageSize() {
        ClientEventHandler.overlay.getGuiLocation().applyStageSizeScaling(stageSize);
    }


    // Helper methods


    private static GuiReactions getReactionOverlay() {
        return ClientEventHandler.overlay.getReactions();
    }



    //==============----------------- Notes below here -------------=====================

    //TODO: More advanced time tracking for bubble lifespans

// Misc notes/values (converted to Java from JS):

//    private final int MAX_RANDOM_MS = 500;
//
//    final double waitTime = ( //int?
//            16
//                    + (
//                    500
//                            * (1 / (HeroData.FeedbackActivity.activity * 3)) //
//                            * (1 / (Math.max(stageSize, 0.1) * 5))
////                * (1 / (percentage * 5)) //ratio of each reaction to total
//                            * (0.6 + (Math.random() * 0.8))
//            )
//    );

//    if (timediff >= waitTime && percentage !== 0 && this.activity !== 0)
//    this.bubbleTimestamps[type] = timestamp; //emit
//    this.bubbleTimestamps[type] = timestamp + (Math.random() * MAX_RANDOM_MS); //pass

// Check bottom of PerformerFeedback to see time flow for bubbling

//------------------------------------------------------

// Ideally the final code would look something like this:

//        timestamp = 0;
//        lastTimestamp = 0;
//        lastTime = Date.now();
//        bubbleTimestamps = {};
//
//        this.startTime = Date.now();
//        this.requestAnimationFrame();
//
//        this.timestamp = timestampParam;
//
//        double minLifespan = 780;
//        double additionalLifespan = 600;
//
//        for (int i = 0; i < this.bubbles.length; i++) {
//            FeedbackTypes bubble = this.bubbles[i];
//
//            double lifetime = timestamp - bubble.timestamp;
//            double inversePercent = ((20 - this.bubbles.length) / 20);
//            if (20 - this.bubbles.length < 0) inversePercent = 0;
//            double lifeLimit = (minLifespan + (inversePercent * additionalLifespan));
//            if (lifetime > lifeLimit) {
//                this.bubbles.splice(i, 1);
//                i--;
//            } else {
//                //blatantly stolen math from Hero's code
//                double currentLife = (lifetime < lifeLimit * 0.17 ?
//                        (Math.pow((lifetime / lifeLimit), 2) * (1 / (Math.pow(0.17, 2)))) :
//                        (1.15 * (0.16 / (lifetime / lifeLimit))) - 0.15) * 2;
//                bubble.alpha = currentLife > 1 ? 1 : currentLife;
//            }
//        }

}
