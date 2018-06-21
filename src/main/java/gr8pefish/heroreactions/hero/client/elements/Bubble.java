package gr8pefish.heroreactions.hero.client.elements;

import gr8pefish.heroreactions.hero.data.FeedbackTypes;

import java.util.Vector;

public class Bubble {


    /** Time counter for rendering */
    private double timestamp;
    /** The counter for when to render */
    private double renderTimeStartOffset;
    /** Time counter for maximum rendering time */
    private double maxTime;
    /** Modifier for scaling (0-1, default 0.5) */
    private double sizeModifier;
    /** x coordinate for rendering */
    private int xLocation;
    /** y coordinate for rendering */
    private int yLocation;
    /** The amount to rotate the image by (in degrees)*/
    private float rotationAngle;
    /** The {@link FeedbackTypes} type of feedback this bubble is */
    private FeedbackTypes type;
    /** If this bubble will die after rendering once */
    private final boolean temporary;

    public Bubble(double timestamp, double renderTimeStartOffset, double maxTime, double sizeModifier, int xLocation, int yLocation, float rotationAngle, FeedbackTypes type, boolean temporary) {
        this.timestamp = timestamp;
        this.renderTimeStartOffset = renderTimeStartOffset;
        this.maxTime = maxTime;
        this.sizeModifier = sizeModifier;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.rotationAngle = rotationAngle;
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

    public double getRenderTimeStartOffset() {
        return renderTimeStartOffset;
    }

    public void setRenderTimeStartOffset(double renderTimeStartOffset) {
        this.renderTimeStartOffset = renderTimeStartOffset;
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

    public float getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(float rotationAngle) {
        this.rotationAngle = rotationAngle;
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

    //Need to include offset time when doing rendering calculations
    public double getTimestampWithOffset() {
        return timestamp - renderTimeStartOffset;
    }

    //Need to include offset time when doing rendering calculations
    public double getMaxTimeWithOffset() {
        return maxTime + renderTimeStartOffset;
    }

    //TODO
    public Vector getBoundingBox() {
        return new Vector();
    }

    //When resetting, just spawn in a new location (temporary)
    public void reset(int randomXPos, int randomYPos) {
        if (!isTemporary()) {
            setTimestamp(0);
            setXLocation(randomXPos);
            setYLocation(randomYPos);
        }
    }

}
