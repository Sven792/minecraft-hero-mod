package gr8pefish.heroreactions.hero.data.enums;

import javax.annotation.Nonnegative;

/**
 * An enum holding all of the variants of reactions
 *
 * THE ORDER MATTERS - the code uses {@link Enum#ordinal} for the texture location
 */
public enum Reactions {
    SHOCK,
    LAUGHTER,
    APPLAUSE,
    LOVE,
    ANGER;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static Reactions getFromString(String type) {
        for (Reactions enumType : Reactions.values()) {
            if (enumType.toString().equalsIgnoreCase(type)) {
                return enumType;
            }
        }
        return null;
    }

    /**
     * Gets the texture's horizontal starting location from a reaction.
     *
     * @return - the x coordinate of the texture's start point
     */
    @Nonnegative
    public int getTextureX() {
        return this.ordinal() * 16;
    }
}
