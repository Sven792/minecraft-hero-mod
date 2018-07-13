package gr8pefish.heroreactions.minecraft.client.gui;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import gr8pefish.heroreactions.common.client.CommonRenderHelper;
import gr8pefish.heroreactions.hero.client.TransformationTypes;
import gr8pefish.heroreactions.hero.client.elements.Bubble;
import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.hero.data.HeroData;
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

public class
GuiReactions {

    /** Icons for the reaction emoticons */
    private static final ResourceLocation REACTION_ICONS_TEX_PATH = new ResourceLocation(ModInfo.MODID,"textures/gui/reaction_icons.png");

    private GuiIngameOverlay overlay;
    private ConcurrentHashMap<FeedbackTypes, Double> feedbackRatios;

    //init bubbles
    public static ConcurrentSet<Bubble> bubbles;

    private final double maxBubbleTime = 1000;
    private double timestampOpacity = 0;
    private double timestampSize = 0;
    private final double maxStartTimeOffset = maxBubbleTime * 1.5; //in ms, NOT inclusive

    //setup basic variables
    public static final int imageTextureWidth = 16; //16 pixel square
    public static final int imageTextureHeight = 16; //16 pixel square
    public static double scalingRatio = 0.4; //size of bubbles

    public int xBase;
    public int yText;
    public int yImage;

    public static double growthRatio = 1.25; //how much the bubble expands by initially (cut into quarters so 1.25)

    GuiReactions(GuiIngameOverlay overlay) {
        this.overlay = overlay;

        //feedback data
        feedbackRatios = HeroData.FeedbackActivity.getFeedbackRatios();

        bubbles = new ConcurrentSet<>();
    }

    /** Renders all bubble reactions */
    public void renderOverlay() {
        //loop through bubbles
        for (Bubble bubble : bubbles) {

            //update timestamp
            bubble.setTimestamp(bubble.getTimestamp() + overlay.timeDifference);

            //if temporary bubble should disappear, do so
            if (bubble.isTemporary() && bubble.getTimestampWithOffset() >= bubble.getMaxTimeWithOffset()) {
                bubbles.remove(bubble);
//                System.out.println("Bubble removed");
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

    public void removeOldBubbles() {
        //loop through bubbles
        for (Bubble bubble : bubbles) {

            //update timestamp
            bubble.setTimestamp(bubble.getTimestamp() + overlay.timeDifference);

            //if temporary bubble should disappear, do so
            if (bubble.isTemporary() && bubble.getTimestampWithOffset() >= bubble.getMaxTimeWithOffset()) {
                bubbles.remove(bubble);
//                System.out.println("Bubble removed");
            }
        }
    }


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
        opacity = (float) MathHelper.clamp(timestampSize / maxBubbleTime, 0, 0.9); // clamp to limit min transparency to be at least 10% opaque
        opacity = 1 - opacity; //inverse, make more transparent over time

        //set transparency
        GlStateManager.color(1, 1, 1, opacity); //1=fully opaque, 0=fully transparent
    }

    //defines how much the bubble "pops"
    public void setSize(Bubble bubble) {

        //base scale of 0 (invisible)
        double scale = 0;
        //local variables for simplicity
        double timestampSize = bubble.getTimestampWithOffset();
        double maxBubbleTime = bubble.getMaxTimeWithOffset();

        //smaller number is more pop (must be at least 3 b/c of if statements)
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

    public void setRotation(Bubble bubble) {
        GlStateManager.rotate(bubble.getRotationAngle(), 0, 0, 1f);
    }


    /** Helper method to get a pseudo-random x position in the rendering box.
     * Biases towards minimal overlap by scoring each random location and choosing the best one. */
    @SuppressWarnings("Duplicates")
    private Pair<Integer, Integer> getRandomPositions() {
        //TODO: Eventually use flocking algorithms to have ideal distribution

        //the amount the texture grows when expanding
        int additionalExpansionSizeX = (int)(imageTextureWidth * scalingRatio * growthRatio);
        int additionalExpansionSizeY= (int)(imageTextureHeight * scalingRatio * growthRatio);

        //short circuit, simple random if no bubbles already
        if (bubbles.isEmpty()) {
            System.out.println("short circuit placement");

            //x
            int x = overlay.getGuiLocation().getRescaledXStart(); //min = xStart
            int xMax = x + overlay.getGuiLocation().getRescaledWidth() - additionalExpansionSizeX; //max = edge of box (xStart + width) - texture size - padding


            //y
            int y = overlay.getGuiLocation().getRescaledYStart(); //min = yStart (no padding on top)
            int yMax = y + overlay.getGuiLocation().getRescaledHeight() - additionalExpansionSizeY; //max = edge of box (yStart + height) - largest texture size - padding

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
        for (Object b : bubbles) {
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
                randomPosX = ThreadLocalRandom.current().nextInt(x, xMax);
            }

            //y
            if (!perfectChosenY) {
                y = overlay.getGuiLocation().getRescaledYStart(); //min = yStart (no padding on top)
                yMax = y + overlay.getGuiLocation().getRescaledHeight() - additionalExpansionSizeY; //max = edge of box (yStart + height) - largest texture size - padding
                randomPosY = ThreadLocalRandom.current().nextInt(y, yMax);
            }

            //check against taken positions (to apply scoring)
            double scoreX = 0;
            double scoreY = 0;

            //TODO: check both x and y boundings at once for composite overlap scoring

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

    //get how much the boxes overlap
    private int getOverlapAmount(int startOne, int endOne, int startTwo, int endTwo) {
        int overlap = 0;

        //if same
        if (startOne == startTwo && endOne == endTwo) {
            return 10000; //arbitrary huge number
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

    //Helper method to add a temporary bubble to the render list
    public void addTestBubble(FeedbackTypes type) {
//        System.out.println("adding bubble");
        Pair<Integer, Integer> positions = getRandomPositions(); //x and y, respectively
        bubbles.add(new Bubble(0, getRandomStartTime(), maxBubbleTime, scalingRatio, positions.getKey(), positions.getValue(), getRandomRotationAngle(), type, true));
    }

    private float getRandomRotationAngle() {
        return (float) Math.toDegrees((ThreadLocalRandom.current().nextFloat() * ((Math.PI / 8) * 2)) - (Math.PI / 8));
    }

    private double getRandomStartTime() {
        return ThreadLocalRandom.current().nextDouble(0, maxStartTimeOffset);
    }


    //==============----------------- Notes below here -------------=====================

//TODO: Notes for time from JS code
//        check bottom of PerformerFeedback to see time flow for bubbling

//        timestamp = 0;
//        lastTimestamp = 0;
//        lastTime = Date.now();
//        bubbleTimestamps = {};
//
//        this.startTime = Date.now();
//        this.requestAnimationFrame();
//
//        this.timestamp = timestampParam;
//
//        double minLifespan = 780;
//        double additionalLifespan = 600;
//
//        for (int i = 0; i < this.bubbles.length; i++) {
//            FeedbackTypes bubble = this.bubbles[i];
//
//            double lifetime = timestamp - bubble.timestamp;
//            double inversePercent = ((20 - this.bubbles.length) / 20);
//            if (20 - this.bubbles.length < 0) inversePercent = 0;
//            double lifeLimit = (minLifespan + (inversePercent * additionalLifespan));
//            if (lifetime > lifeLimit) {
//                this.bubbles.splice(i, 1);
//                i--;
//            } else {
//                //blatantly stolen math from Hero's code
//                double currentLife = (lifetime < lifeLimit * 0.17 ?
//                        (Math.pow((lifetime / lifeLimit), 2) * (1 / (Math.pow(0.17, 2)))) :
//                        (1.15 * (0.16 / (lifetime / lifeLimit))) - 0.15) * 2;
//                bubble.alpha = currentLife > 1 ? 1 : currentLife;
//            }
//        }


//TODO: Notes for glow
//the "best" way would involve rendering a "mask" of the opacity of each pixel, applying a blur, and then drawing that
//render the opacity into a texture, and draw with blur and a color
//processing the texture to make a blurred texture might be more effort than it's worth
//oh yeah a "baked" glow would also work lol
//if you have a fixed UI shape
//you can use "box" scaling, where you leave a margin and scale the middle sections
//the idea is you split the image into 9 (3x3) quads
//and scale each row/column based on the numbers
//but keeping the UV coords original
//normal box scaling:
//x0 = xpos; x1 = xpos + left_margin; x2 = xpos + width - right_margin; x3 = xpos + width;
//repeat for y[0..3]/ypos/height
//then the quads are like, {x0y0, x1y0, x1y1, x0y1} ...
//or the other way around, since opengl is different from dx in which direction Y grows



//TODO: Cleanup + multi bubble rendering via input from actual events
//    public void renderFeedbackBubblingFromReactionRatios() {
//        ConcurrentHashMap<FeedbackTypes, Double> feedbackRatios = HeroData.FeedbackActivity.getFeedbackRatios();
//        for (Map.Entry<FeedbackTypes, Double> entry : feedbackRatios.entrySet()) {
//            renderBubblingReactions(xBase, yImage, imageTextureWidth * feedbackRatios.size(), imageTextureHeight + paddingVertical, entry.getKey(), entry.getValue(), HeroData.FeedbackActivity.totalFeedbackCount);
//        }
//    }
//
//    public void renderFeedbackBubbleWithTransformation(FeedbackTypes feedbackType, List<TransformationTypes> transformations, long timeDifference) {
//        for (TransformationTypes transformation : transformations) {
//            transformation.apply(timeDifference);
//            renderFeedbackBubbleOnly(feedbackType);
//        }
//    }

}
