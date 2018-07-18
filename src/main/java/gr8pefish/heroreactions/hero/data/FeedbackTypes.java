package gr8pefish.heroreactions.hero.data;

import javax.annotation.Nonnegative;

/**
 * An enum holding all of the variants of reactions
 *
 * THE ORDER MATTERS - the code uses {@link Enum#ordinal} for the texture location
 */
public enum FeedbackTypes {
    SHOCK,
    LAUGHTER,
    APPLAUSE,
    LOVE,
    ANGER;

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
        return null;
    }

    /**
     * Gets the texture's horizontal starting location from a reaction.
     * Assumes 16 pixel width image
     *
     * @return - the x coordinate of the texture's start point
     */
    @Nonnegative
    public int getTextureX() {
        return this.ordinal() * 16;
    }
}
