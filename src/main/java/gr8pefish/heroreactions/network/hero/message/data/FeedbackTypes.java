package gr8pefish.heroreactions.network.hero.message.data;

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
}
