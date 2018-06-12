package gr8pefish.heroreactions.hero.client;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.common.client.MinecraftRenderHelper;
import gr8pefish.heroreactions.hero.data.FeedbackTypes;

public enum TransformationTypes {
    FADE, //opacity changes for a fade-in/out effect
    EXPAND, //grow/shrink over time
    SLIDE //translate up/down over time
    ;

    public void render(double currentTime, double totalTime, FeedbackTypes feedbackType, double feedbackRatioOfTotal) {
        switch (this) {
            case FADE:
                MinecraftRenderHelper.renderFade(currentTime, totalTime, feedbackType, feedbackRatioOfTotal);
            case EXPAND:
                MinecraftRenderHelper.renderExpand(currentTime, totalTime, feedbackType, feedbackRatioOfTotal);
            case SLIDE:
                MinecraftRenderHelper.renderSlide(currentTime, totalTime, feedbackType, feedbackRatioOfTotal);
            default:
                Common.LOGGER.warn("Invalid transformation type!");
        }
    }

}
