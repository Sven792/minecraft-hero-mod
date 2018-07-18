package gr8pefish.heroreactions.hero.client;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.common.client.CommonRenderHelper;
import gr8pefish.heroreactions.hero.client.elements.Bubble;

/**
 * An enum holding all of the ways to visually manipulate an image - specifically used for the bubbling emojis
 */
public enum TransformationTypes {
    FADE, //opacity changes for a fade-in/out effect
    EXPAND, //grow/shrink over time
    ROTATE, //rotate
    SLIDE; //translate up/down over time

    public void apply(Bubble bubble) {
        switch(this) {
            case FADE:
                CommonRenderHelper.applyFade(bubble);
                break;
            case EXPAND:
                CommonRenderHelper.applyExpand(bubble);
                break;
            case ROTATE:
                CommonRenderHelper.applyRotate(bubble);
                break;
            case SLIDE:
                CommonRenderHelper.applySlide(bubble);
                break;
            default:
                Common.LOGGER.warn("Invalid transformation type!");
        }
    }

}
