package gr8pefish.heroreactions.network.hero.message.data;

import java.util.ArrayList;
import java.util.Arrays;

public enum FeedbackTypes {
    SHOCK,
    LAUGHTER,
    APPLAUSE,
    LOVE,
    ANGER,
    NONE;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static FeedbackTypes getFromString(String type) {
        for (FeedbackTypes enumType : FeedbackTypes.values()) {
            if (enumType.toString().equalsIgnoreCase(type)) {
                return enumType;
            }
        }
        return NONE;
    }

    public static FeedbackTypes[] getAllValidTypes() {
        return Arrays.copyOf(FeedbackTypes.values(), FeedbackTypes.values().length - 1); //remove last (NONE) element
    }

    public int getTextureX() {
        return this.ordinal() * 16;
    }
}
