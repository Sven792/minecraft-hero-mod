package gr8pefish.heroreactions.hero.client.elements;

import gr8pefish.heroreactions.hero.data.FeedbackTypes;

public class Bubble {


    /** Time counter for rendering */
    private double timestamp;
    /** Time counter for maximum rendering time */
    private double maxTime;
    /** Modifier for scaling (0-1, default 0.5) */
    private double sizeModifier;
    /** x coordinate for rendering */
    private int xLocation;
    /** y coordinate for rendering */
    private int yLocation;
    /** The {@link FeedbackTypes} type of feedback this bubble is */
    private FeedbackTypes type;
    /** If this bubble will die after rendering once */
    private final boolean temporary;

    public Bubble(double timestamp, double maxTime, double sizeModifier, int xLocation, int yLocation, FeedbackTypes type, boolean temporary) {
        this.timestamp = timestamp;
        this.maxTime = maxTime;
        this.sizeModifier = sizeModifier;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.type = type;
        this.temporary = temporary;
    }

    // Getters/Setters

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public double getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(double maxTime) {
        this.maxTime = maxTime;
    }

    public double getSizeModifier() {
        return sizeModifier;
    }

    public void setSizeModifier(double sizeModifier) {
        this.sizeModifier = sizeModifier;
    }

    public int getXLocation() {
        return xLocation;
    }

    public void setXLocation(int xLocation) {
        this.xLocation = xLocation;
    }

    public int getYLocation() {
        return yLocation;
    }

    public void setYLocation(int yLocation) {
        this.yLocation = yLocation;
    }

    public FeedbackTypes getFeedbackType() {
        return type;
    }

    public void setFeedbackType(FeedbackTypes type) {
        this.type = type;
    }

    public boolean isTemporary() {
        return temporary;
    }

    // Helper methods

    //When resetting, just spawn in a new location (temporary)
    public void reset(int randomXPos, int randomYPos) {
        if (!isTemporary()) {
            setTimestamp(0);
            setXLocation(randomXPos);
            setYLocation(randomYPos);
        }
    }

}
