package gr8pefish.heroreactions.minecraft.client.gui;

import com.google.common.collect.ConcurrentHashMultiset;
import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.common.client.CommonRenderHelper;
import gr8pefish.heroreactions.hero.client.TransformationTypes;
import gr8pefish.heroreactions.hero.client.elements.Bubble;
import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.hero.data.HeroData;
import gr8pefish.heroreactions.minecraft.api.HeroReactionsInfo;
import gr8pefish.heroreactions.minecraft.config.ConfigHandler;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class GuiReactions {

    /** Icons for the reaction emoticons */
    private static final ResourceLocation REACTION_ICONS_TEX_PATH = new ResourceLocation(HeroReactionsInfo.MODID,"textures/gui/reaction_icons.png");

    private GuiIngameOverlay overlay;
    private ConcurrentHashMap<FeedbackTypes, Double> feedbackRatios;

    //init bubbles
    public ConcurrentSet<Bubble> bubbles;

    private final double maxBubbleTime = 2000;
    private double timestampOpacity = 0;
    private double timestampSize = 0;

    //setup basic variables
    public static final int imageTextureWidth = 16; //16 pixel square
    public static final int imageTextureHeight = 16; //16 pixel square
    public static double scalingRatio = 0.5; //size of bubbles

    public int xBase;
    public int yText;
    public int yImage;

    private Random random;

    public static double growthRatio = 1.25; //how much the bubble expands by initially (cut into quarters so 1.25)

    GuiReactions(GuiIngameOverlay overlay) {
        this.overlay = overlay;

        //setup randomizer
        this.random = new Random();

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
            if (bubble.isTemporary() && bubble.getTimestamp() >= bubble.getMaxTime()) {
                bubbles.remove(bubble);
                continue; //no need to render this one
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

        //if over total time, reset
        if (bubble.getTimestamp() >= bubble.getMaxTime()) {
            bubble.reset(getRandomXPos(), getRandomYPos()); //reset total //TODO: end spawning?
        //otherwise set opacity
        } else {
            opacity = (float) MathHelper.clamp(bubble.getTimestamp() / bubble.getMaxTime(), 0, 1); //simply progress over lifespan ratio (clamp shouldn't theoretically be necessary)
            opacity = 1 - opacity; //inverse, make more transparent over time
        }

        //set transparency
        GlStateManager.color(1, 1, 1, opacity); //1=fully opaque, 0=fully transparent
    }


    public void setSize(Bubble bubble) {

        //base scale of 0 (invisible)
        double scale = 0;
        //local variables for simplicity
        double timestampSize = bubble.getTimestamp();
        double maxBubbleTime = bubble.getMaxTime();

        //if over total time, reset
        if (timestampSize >= maxBubbleTime) {
            bubble.reset(getRandomXPos(), getRandomYPos());
        //otherwise set scale
        } else if (timestampSize < maxBubbleTime / 4) { //first quarter growth to 1.25 size
            scale = 1 + (timestampSize / maxBubbleTime); //increase by time amount
        } else if (timestampSize < maxBubbleTime / 2){ //second quarter shrink to base size
            scale = 1 + (timestampSize / maxBubbleTime); //old growth, need to use this to keep it smooth
            double z = timestampSize / (maxBubbleTime / 2); //modifier of how much to shrink
            scale -= ((timestampSize / maxBubbleTime) * z); //apply modifier to shrink from what it was to 1
        } else { //second half shrink from base size to 0
            scale = (timestampSize / maxBubbleTime); //get time spent as modifier
            scale = ((scale - 0.5d) / 0.5d); //normalize to 0-1 (instead of 0.5-1)
            scale = 1 - scale; //inverse, make smaller over time
        }

        //move + scale proportionally
        GlStateManager.translate(bubble.getXLocation(), bubble.getYLocation(), 0);
        GlStateManager.scale(scale, scale, 0);
    }

    public void setRotation(Bubble bubble) {
        double randomRotationAngle = ((random.nextFloat() * ((Math.PI / 8) * 2)) - (Math.PI / 8));
        GlStateManager.rotate((float) randomRotationAngle, 1.0f, 0, 0);
    }


    /** Helper method to get a random x position in the rendering box */
    private int getRandomXPos() { //TODO: smarter algo somehow here
        int x = overlay.getGuiLocation().getRescaledXStart(); //min = xStart
        int xMax = x + overlay.getGuiLocation().getRescaledWidth() - (int)(imageTextureWidth * scalingRatio * growthRatio); //max = edge of box (xStart + width) - texture size - padding
        return (x + random.nextInt(xMax - x + 1));
    }

    /** Helper method to get a random y position in the rendering box */
    private int getRandomYPos() { //TODO: bias towards middle Y
        int y = overlay.getGuiLocation().getRescaledYStart(); //min = yStart (no padding on top)
        int yMax = y + overlay.getGuiLocation().getRescaledHeight() - ((int)(imageTextureHeight * scalingRatio * growthRatio)); //max = edge of box (yStart + height) - largest texture size - padding
        return (y + random.nextInt(yMax - y + 1));
    }

    //Helper test method to add a couple bubbles
    public void addTestBubbles() {
        bubbles.add(new Bubble(0, maxBubbleTime, scalingRatio, getRandomXPos(), getRandomYPos(), FeedbackTypes.LOVE, false));
        bubbles.add(new Bubble(700, maxBubbleTime, scalingRatio, getRandomXPos(), getRandomYPos(), FeedbackTypes.APPLAUSE, false));
        bubbles.add(new Bubble(1400, maxBubbleTime, scalingRatio, getRandomXPos(), getRandomYPos(), FeedbackTypes.LAUGHTER, false)); //setting base timestamp doesn't do anything?
    }

    //Helper method to add a temporary bubble to the render list
    public void addTestBubble(FeedbackTypes type) {
        bubbles.add(new Bubble(0, maxBubbleTime, scalingRatio, getRandomXPos(), getRandomYPos(), type, true));
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
