package gr8pefish.heroreactions.hero.client.elements;

import gr8pefish.heroreactions.hero.data.FeedbackTypes;

public class Bubble {


    /** Time counter for opacity rendering */
    private double timestampOpacity;
    /** Time counter for size rendering */
    private double timestampSize;
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

    public Bubble(double timestampOpacity, double timestampSize, double maxTime, double sizeModifier, int xLocation, int yLocation, FeedbackTypes type) {
        this.timestampOpacity = timestampOpacity;
        this.timestampSize = timestampSize;
        this.maxTime = maxTime;
        this.sizeModifier = sizeModifier;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.type = type;
    }

    public double getTimestampOpacity() {
        return timestampOpacity;
    }

    public void setTimestampOpacity(double timestamp) {
        this.timestampOpacity = timestamp;
    }

    public double getTimestampSize() {
        return timestampSize;
    }

    public void setTimestampSize(double timestampSize) {
        this.timestampSize = timestampSize;
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

}
