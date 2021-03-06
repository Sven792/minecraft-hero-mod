package gr8pefish.heroreactions.minecraft.client.gui.overlay;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.common.client.CommonRenderHelper;
import gr8pefish.heroreactions.hero.client.TransformationTypes;
import gr8pefish.heroreactions.hero.client.elements.Bubble;
import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.minecraft.client.gui.overlay.GuiIngameOverlay;
import gr8pefish.heroreactions.minecraft.config.ConfigHandler;
import gr8pefish.heroreactions.minecraft.lib.ModInfo;
import io.netty.util.internal.ConcurrentSet;
import javafx.util.Pair;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Renders the emoticon 'bubbles' for the main {@link GuiIngameOverlay}.
 */
public class GuiReactions {

    // Fields

    /** Icons' location for the reaction emoticons + more */
    public static final ResourceLocation REACTION_ICONS_TEX_PATH = new ResourceLocation(ModInfo.MODID,"textures/gui/reaction_icons.png");

    //Main overlay
    private GuiIngameOverlay overlay;

    //The list of bubbles to render
    private static ConcurrentSet<Bubble> bubbleRenderList;

    //Variables set by config data
    public double maxBubbleTime = ConfigHandler.overlayConfigSettings.maxEmojiTime; //maximum time the bubble can appear
    public double maxStartTimeOffset = maxBubbleTime * ConfigHandler.overlayConfigSettings.maxEmojiOffsetTimeRatio; //in ms, NOT inclusive
    public double minOpacity = ConfigHandler.overlayConfigSettings.minOpacity;

    //Basic variables
    public static final int imageTextureWidth = 16; //16 pixel square
    public static final int imageTextureHeight = 16; //16 pixel square
    public static double scalingRatio = ConfigHandler.overlayConfigSettings.emojiScale; //size of bubbles
    public static double growthRatio = 1.25; //how much the bubble expands by initially (cut into quarters so 1.25)


    // Constructor


    GuiReactions(GuiIngameOverlay overlay) {
        this.overlay = overlay;
        bubbleRenderList = new ConcurrentSet<>();
    }


    // Public methods


    /** Main loop to render all bubbles */
    public void renderAllBubbles() {
        //loop through bubbles
        for (Bubble bubble : bubbleRenderList) {

            //update timestamp
            bubble.setTimestamp(bubble.getTimestamp() + overlay.timeDifference);

            //if temporary bubble should disappear, do so
            if (bubble.isTemporary() && bubble.getTimestampWithOffset() >= bubble.getMaxTimeWithOffset()) {
                bubbleRenderList.remove(bubble);
                continue; //no need to render this one
            }

            //if bubble shouldn't render (startTimeOffset not taken care of)
            if (bubble.getTimestamp() < bubble.getRenderTimeStartOffset()) {
                continue; //don't render yet
            }

            //push matrix
            GlStateManager.pushMatrix();

            //apply effects (pop in normal, expand to oversize, shrink back to nothing, all while slowly decreasing opacity)
            CommonRenderHelper.applyEffects(new TransformationTypes[]{TransformationTypes.EXPAND, TransformationTypes.FADE, TransformationTypes.ROTATE}, bubble);

            //draw icon
            CommonRenderHelper.renderBubble(bubble);

            //pop matrix
            GlStateManager.popMatrix();
        }
    }

    /**
     * Add a bubble to the render list so it can be displayed next render tick
     *
     * @param type - the {@link FeedbackTypes} of reaction this bubble portrays
     */
    public void addBubbleToRenderList(FeedbackTypes type) {
        Pair<Integer, Integer> positions = getRandomPositions(); //x and y, respectively
        bubbleRenderList.add(new Bubble(0, getRandomStartTime(), maxBubbleTime, scalingRatio, positions.getKey(), positions.getValue(), getRandomRotationAngle(), type, true));
    }

    /** Clear bubbles from render list if they shouldn't appear. Currently unused. */
    public void removeOldBubbles() {
        //loop through bubbles
        for (Bubble bubble : bubbleRenderList) {

            //update timestamp
            bubble.setTimestamp(bubble.getTimestamp() + overlay.timeDifference);

            //if temporary bubble should disappear, do so
            if (bubble.isTemporary() && bubble.getTimestampWithOffset() >= bubble.getMaxTimeWithOffset()) {
                bubbleRenderList.remove(bubble);
            }
        }
    }

    /** Render a single bubble, without effects */
    public void renderFeedbackBubbleOnly(Bubble bubble) {
        //transformations done, just have to render bubble in location
        overlay.getMinecraft().getTextureManager().bindTexture(REACTION_ICONS_TEX_PATH);

        //Default half size (smaller bubbles)
        GlStateManager.scale(bubble.getSizeModifier(), bubble.getSizeModifier(), 0); //TODO: value via config

        //draw icon
        overlay.drawTexturedModalRect(
                0,  //screen x, 0 because translated already (in scaling)
                0, //screen y, 0 because translated already (in scaling)
                bubble.getFeedbackType().getTextureX(), //texture x
                0, //texture y
                imageTextureWidth, //width
                imageTextureHeight); //height
    }


    // Transformations

    /**
     * Add a transparency effect.
     * Fades the bubble out over time (linear)
     *
     * @param bubble - the bubble to apply this effect to
     */
    public void setOpacity(Bubble bubble) {

        //set GL states
        GlStateManager.enableAlpha(); //can cause weird transparent cutout issues, but positive affects performance (dependent on transparent pixel %) if no issues present
        GlStateManager.enableBlend(); //enable blending
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA); //black magic that is necessary

        //base opacity of 0 (fully transparent)
        float opacity = 0f;
        //local variables for simplicity
        double timestampSize = bubble.getTimestampWithOffset();
        double maxBubbleTime = bubble.getMaxTimeWithOffset();

        //simply progress over lifespan ratio
        opacity = (float) MathHelper.clamp(timestampSize / maxBubbleTime, 0, 1 - minOpacity); // clamp to limit min transparency to be at least x opaque
        opacity = 1 - opacity; //inverse, make more transparent over time

        //set transparency
        GlStateManager.color(1, 1, 1, opacity); //1=fully opaque, 0=fully transparent
    }

    //defines how much the bubble "pops"

    /**
     * Adds an effect that alters the size of the bubble over time.
     *
     * It appears initially as normal size,
     * grows to 133% size over the first third of the lifespan (so it "pops" into existence),
     * then fades away to 0% size over the remaining lifespan
     *
     * @param bubble - the bubble to apply this effect to
     */
    public void setSize(Bubble bubble) {

        //base scale of 0 (invisible)
        double scale = 0;
        //local variables for simplicity
        double timestampSize = bubble.getTimestampWithOffset();
        double maxBubbleTime = bubble.getMaxTimeWithOffset();

        //smaller number is more pop (must be at least 3 b/c of 3 if statements, dividing into thirds is the smallest possible division (spawn, grow, shrink))
        int popValue = 3;

        //Set scale
        if (timestampSize < maxBubbleTime / popValue) { //first part growth to 1 + (1/x) size
            scale = 1 + (timestampSize / maxBubbleTime); //increase by time amount
        } else if (timestampSize < maxBubbleTime / popValue){ //second part shrink to base size
            scale = 1 + (timestampSize / maxBubbleTime); //old growth, need to use this to keep it smooth
            double z = timestampSize / (maxBubbleTime / popValue); //modifier of how much to shrink
            scale -= ((timestampSize / maxBubbleTime) * z); //apply modifier to shrink from what it was to 1
        } else { //last part shrink from base size to 0
            scale = (timestampSize / maxBubbleTime); //get time spent as modifier
            scale = ((scale - 0.5d) / 0.5d); //normalize to 0-1 (instead of 0.5-1)
            scale = 1 - scale; //inverse, make smaller over time
        }

        //move + scale proportionally
        GlStateManager.translate(bubble.getXLocation(), bubble.getYLocation(), 0);
        GlStateManager.scale(scale, scale, 0);
    }

    /**
     * Adds a rotation effect.
     * The bubble will appear with a randomly determined angle offset, adding more variance to the overlay.
     *
     * @param bubble - the bubble to apply this effect to
     */
    public void setRotation(Bubble bubble) {
        GlStateManager.rotate(bubble.getRotationAngle(), 0, 0, 1f); //rotation angle determined at the bubble's construction time
    }

    /**
     * Adds movement to the bubble.
     * Not yet implemented, but code is kept here for ease of future implementation.
     *
     * @param bubble - the bubble to apply this effect to
     */
    public void setPosition(Bubble bubble) {
        //TODO: Eventually implement
//        GlStateManager.translate(x, y, z);
    }


    // Private helper methods


    /**
     * Helper method to get a pseudo-random x and y position in the rendering box.
     * Biases towards minimal overlap by scoring randomly generated locations and choosing the best one.
     *
     * Complex in implementation, but simple in theory.
     *
     * All that happens is that the code checks x amount of locations,
     * sees how much overlap each one would have with pre-existing bubbles,
     * and then scores the locations accordingly (i.e. less overlap = better score).
     * The best scoring location is then returned (a Pair containing the XCoordinate and YCoordinate, respectively).
     *
     * Note: A variable number of locations are checked, as a higher number is slower but will give better results, and vice-versa
     * This allows users to change this number depending on their hardware qualities
     */
    @SuppressWarnings("Duplicates")
    private Pair<Integer, Integer> getRandomPositions() {
        //TODO: Eventually use flocking algorithms to have ideal distribution

        //the amount the texture grows when expanding
        int additionalExpansionSizeX = (int)(imageTextureWidth * scalingRatio * growthRatio);
        int additionalExpansionSizeY= (int)(imageTextureHeight * scalingRatio * growthRatio);

        //If no bubbles rendered just get a random position
        if (bubbleRenderList.isEmpty()) {
            Common.LOGGER.debug("Short circuit placement for bubble.");

            //x
            int x = overlay.getGuiLocation().getRescaledXStart(); //min = xStart
            int xMax = x + overlay.getGuiLocation().getRescaledWidth() - additionalExpansionSizeX; //max = edge of box (xStart + width) - texture size - padding
            if (xMax <= x) xMax = x + 1; //make sure proper bounding okay

            //y
            int y = overlay.getGuiLocation().getRescaledYStart(); //min = yStart (no padding on top)
            int yMax = y + overlay.getGuiLocation().getRescaledHeight() - additionalExpansionSizeY; //max = edge of box (yStart + height) - largest texture size - padding
            if (yMax <= y) yMax = y + 1; //make sure proper bounding okay

            //return both positions
            return new Pair(ThreadLocalRandom.current().nextInt(x, xMax), ThreadLocalRandom.current().nextInt(y, yMax));
        }

        //otherwise weight towards non-taken spots
        int maxIterations = 10; //the amount of random spaces to check, higher = less overlap but slower //TODO: Config
        ConcurrentHashMap<Integer, Double> positionXWithScore = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, Double> positionYWithScore = new ConcurrentHashMap<>();

        //get taken x and y positions and put into data structures
        ListMultimap<Integer, Integer> takenPositionXStartEnd = ArrayListMultimap.create();
        ListMultimap<Integer, Integer> takenPositionYStartEnd = ArrayListMultimap.create();
        for (Object b : bubbleRenderList) {
            Bubble bubble =(Bubble) b;
            //x
            int startX = bubble.getXLocation();
            int endX = bubble.getXLocation() + additionalExpansionSizeX;
            takenPositionXStartEnd.put(startX, endX);
            //y
            int startY = bubble.getYLocation();
            int endY = bubble.getYLocation() + additionalExpansionSizeY;
            takenPositionYStartEnd.put(startY, endY);
        }

        //setupVariables
        int x, y, xMax, yMax, randomPosX = -1, randomPosY = -1, bestPosX = -1, bestPosY = -1;
        boolean perfectChosenX = false, perfectChosenY = false; //perfect location found

        //loop x many times to find best location
        for (int i = 0; i < maxIterations; i++) {

            //get random pos

            //x
            if (!perfectChosenX) {
                x = overlay.getGuiLocation().getRescaledXStart(); //min = xStart
                xMax = x + overlay.getGuiLocation().getRescaledWidth() - additionalExpansionSizeX; //max = edge of box (xStart + width) - texture size - padding
                if (xMax <= x) xMax = x + 1; //make sure proper bounding okay
                randomPosX = ThreadLocalRandom.current().nextInt(x, xMax);
            }

            //y
            if (!perfectChosenY) {
                y = overlay.getGuiLocation().getRescaledYStart(); //min = yStart (no padding on top)
                yMax = y + overlay.getGuiLocation().getRescaledHeight() - additionalExpansionSizeY; //max = edge of box (yStart + height) - largest texture size - padding
                if (yMax <= y) yMax = y + 1; //make sure proper bounding okay
                randomPosY = ThreadLocalRandom.current().nextInt(y, yMax);
            }

            //check against taken positions (to apply scoring)
            double scoreX = 0;
            double scoreY = 0;

            //TODO: check both x and y boundings at once for composite overlap scoring for efficiency's sake

            //loop through X
            if (!perfectChosenX) {
                for (Map.Entry<Integer, Integer> takenPos : takenPositionXStartEnd.entries()) {
                    int startX = takenPos.getKey();
                    int endX = takenPos.getValue();

                    int scoreModifier = -1 * getOverlapAmount(randomPosX, randomPosX + additionalExpansionSizeX, startX, endX);
                    scoreX += scoreModifier;
                }

                if (scoreX >= 0) {
                    bestPosX = randomPosX;
                    perfectChosenX = true;
                }

                positionXWithScore.put(randomPosX, scoreX);
            }

            //loop through Y
            if (!perfectChosenY) {
                for (Map.Entry<Integer, Integer> takenPos : takenPositionYStartEnd.entries()) {
                    int startY = takenPos.getKey();
                    int endY = takenPos.getValue();

                    int scoreModifier = -1 * getOverlapAmount(randomPosY, randomPosY + additionalExpansionSizeY, startY, endY);
                    scoreY += scoreModifier;
                }

                if (scoreY >= 0) {
                    bestPosY = randomPosY;
                    perfectChosenY = true;
                }

                positionYWithScore.put(randomPosY, scoreY);
            }

            //perfect solution, stop iterating
            if (perfectChosenX && perfectChosenY) {
                break;
            }
        }

        //return perfect (no overlap) if possible, otherwise just the best scored one
        int bestPosXOverall, bestPosYOverall;

        //x
        if (perfectChosenX) bestPosXOverall = bestPosX;
        else {
            bestPosXOverall = Collections.max(positionXWithScore.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
        }

        //y
        if (perfectChosenY) bestPosYOverall = bestPosY;
        else {
            bestPosYOverall = Collections.max(positionYWithScore.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
        }

        return new Pair(bestPosXOverall, bestPosYOverall);

    }

    /**
     * Helper method to determine how much two bounding boxes overlap (on a single axis)
     * @return - the overlap amount (in pixels), 10000 if complete overlap
     */
    private int getOverlapAmount(int startOne, int endOne, int startTwo, int endTwo) {
        int overlap = 0;

        //if same
        if (startOne == startTwo && endOne == endTwo) {
            return 10000; //arbitrary huge number, return instantly
        }

        //if box 2 overlap on the right/bottom side of box 1
        if (startTwo > startOne && startTwo < endOne) {
            //calc how much in-between
            overlap += (endOne - startTwo);
        }

        //if box 2 overlap on the left/top side of box 2
        if (endTwo > startOne && endTwo < endOne) {
            //calc how much in-between
            overlap += (endTwo - startOne);
        }

        //return whatever
        return overlap;
    }

    /** Gets a random angle for the bubble to display at */
    private float getRandomRotationAngle() {
        return (float) Math.toDegrees((ThreadLocalRandom.current().nextFloat() * ((Math.PI / 8) * 2)) - (Math.PI / 8));
    }

    /** Gets a random offset time for the bubble, so they don't all appear at once - i.e. it looks more natural */
    private double getRandomStartTime() {
        return ThreadLocalRandom.current().nextDouble(0, maxStartTimeOffset);
    }

}
