package gr8pefish.heroreactions.hero.client;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.common.client.CommonRenderHelper;
import gr8pefish.heroreactions.hero.client.elements.Bubble;
import gr8pefish.heroreactions.hero.data.FeedbackTypes;

public enum TransformationTypes {
    FADE, //opacity changes for a fade-in/out effect
    EXPAND, //grow/shrink over time
    SLIDE; //translate up/down over time

    public void apply(Bubble bubble) {
        switch(this) {
            case FADE:
                CommonRenderHelper.applyFade(bubble);
                break;
            case EXPAND:
                CommonRenderHelper.applyExpand(bubble);
                break;
            case SLIDE:
                CommonRenderHelper.applySlide(bubble);
                break;
            default:
                Common.LOGGER.warn("Invalid transformation type!");
        }
    }

}
