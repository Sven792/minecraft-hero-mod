package gr8pefish.heroreactions.hero.client;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.common.client.CommonRenderHelper;
import gr8pefish.heroreactions.hero.data.FeedbackTypes;

public enum TransformationTypes {
    FADE, //opacity changes for a fade-in/out effect
    EXPAND, //grow/shrink over time
    SLIDE; //translate up/down over time

    public void render(long currentTime, long totalTime, FeedbackTypes feedbackType, double feedbackRatioOfTotal) {
        switch (this) {
            case FADE:
                CommonRenderHelper.renderFade(currentTime, totalTime, 0L, feedbackType, feedbackRatioOfTotal);
            case EXPAND:
                CommonRenderHelper.renderExpand(currentTime, totalTime, feedbackType, feedbackRatioOfTotal);
            case SLIDE:
                CommonRenderHelper.renderSlide(currentTime, totalTime, feedbackType, feedbackRatioOfTotal);
            default:
                Common.LOGGER.warn("Invalid transformation type!");
        }
    }

}
