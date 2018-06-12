package gr8pefish.heroreactions.client.gui;

import gr8pefish.heroreactions.hero.HeroUtils;
import gr8pefish.heroreactions.hero.data.HeroData;

public class ReactionRenderHelper {

    //fields
    /**set by {@link HeroUtils#setStageSize()} during a viewer message event */
    public static double stageSize = -1;

    //time tracker
    private final int MAX_RANDOM_MS = 500;

    final int waitTime = (
            16
            + (
                500
                * (1 / (HeroData.FeedbackActivity.activity * 3)) //
                * (1 / (Math.max(stageSize, 0.1) * 5))
                * (1 / (percentage * 5)) //ratio of each reaction to total
                * (0.6 + (Math.random() * 0.8))
                )
            );

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
