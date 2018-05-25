package gr8pefish.heroreactions.network.hero.message.data;

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
}
