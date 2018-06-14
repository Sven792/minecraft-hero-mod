package gr8pefish.heroreactions.minecraft.client.gui;

import gr8pefish.heroreactions.common.client.CommonRenderHelper;
import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.hero.data.HeroData;
import gr8pefish.heroreactions.minecraft.api.HeroReactionsInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.ConcurrentHashMap;

public class GuiReactions {

    /** Icons for the reaction emoticons */
    private static final ResourceLocation REACTION_ICONS_TEX_PATH = new ResourceLocation(HeroReactionsInfo.MODID,"textures/gui/reaction_icons.png");

    private GuiIngameOverlay overlay;
    private ConcurrentHashMap<FeedbackTypes, Double> feedbackRatios;

    private final double maxBubbleTime = 2000;
    private double timestampOpacity = 0;
    private double timestampSize = 0;

    //setup basic variables
    private final int imageTextureWidth = 16; //16 pixel square
    private final int imageTextureHeight = 16; //16 pixel square
    public int paddingHorizontal = 6; //padding from sides of screen and in-between elements
    public int paddingVertical = 4; //padding in-between elements

    public int xBase;
    public int yText;
    public int yImage;

    private boolean hasBeenResizedOrMoved = false; //to know where to render the end bubble

    GuiReactions(GuiIngameOverlay overlay) {
        this.overlay = overlay;

        //feedback data
        feedbackRatios = HeroData.FeedbackActivity.getFeedbackRatios();
    }

    public void renderOverlay() {
        //TODO: ITS GOD DAMN BEAUTIFUL! :D
        //make the rest like this

        //pop in normal size
        //expand to oversize real quick
        //fade out with increasing transparency and smaller size

        //push matrix
        GlStateManager.pushMatrix();

        //apply effects
        CommonRenderHelper.applyExpand(overlay.timeDifference);
        CommonRenderHelper.applyFade(overlay.timeDifference);

        //draw icon
        renderFeedbackBubbleOnly(FeedbackTypes.LOVE);

        //pop matrix
        GlStateManager.popMatrix();
    }

    private void getAlpha() {

        //Too many dependencies/jumping files - bwah
//        CommonRenderHelper.renderFade(overlay.currentTime, overlay.timeDifference, overlay.baseTime, FeedbackTypes.LOVE, 0.1f);
//        CommonRenderHelper.renderExpand(overlay.timeDifference, FeedbackTypes.ANGER, 0.1f);
    }


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


    public void renderFeedbackBubbleOnly(FeedbackTypes feedbackType) {
        //transformations done, just have to render bubble in location
        overlay.getMinecraft().getTextureManager().bindTexture(REACTION_ICONS_TEX_PATH);

        //move + scale proportionally
        GlStateManager.scale(0.5, 0.5, 0); //default half size (for small bubbles)
        hasBeenResizedOrMoved = true;

        //draw icon
        if (hasBeenResizedOrMoved) { //rescaled, render at 0,0
            overlay.drawTexturedModalRect(
                    0,  //screen x
                    0, //screen y
                    FeedbackTypes.LOVE.getTextureX(), //texture x
                    0, //texture y
                    imageTextureWidth, //width
                    imageTextureHeight); //height
            hasBeenResizedOrMoved = false; //TODO: won't work in a loop of them :(
        }
        else
            overlay.drawTexturedModalRect(
                    getRandomXPos(),  //screen x
                    getRandomYPos(), //screen y
                    FeedbackTypes.LOVE.getTextureX(), //texture x
                    0, //texture y
                    imageTextureWidth, //width
                    imageTextureHeight); //height
    }

    public void setOpacity(long timeDifference) {

        //set GL states
        GlStateManager.enableAlpha(); //can cause weird transparent cutout issues, but positive affects performance (dependent on transparent pixel %) if no issues present
        GlStateManager.enableBlend(); //enable blending
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA); //black magic that is necessary

        //base opacity of 0 (fully transparent)
        float opacity = 0f;
        //add delta to total
        timestampOpacity += timeDifference;

        //if over total time, reset
        if (timestampOpacity >= maxBubbleTime) {
            timestampOpacity = 0; //reset total //TODO: end spawning?
        //otherwise set opacity
        } else {
            opacity = (float) MathHelper.clamp(timestampOpacity / maxBubbleTime, 0, 1); //simply progress over lifespan ratio (clamp shouldn't theoretically be necessary)
        }

        //set transparency
        GlStateManager.color(1, 1, 1, opacity); //1=fully opaque, 0=fully transparent
    }


    public void setSize(long timeDifference) {

        //base scale of 0 (invisible)
        double scale = 0;
        //add delta to total
        timestampSize += timeDifference;

        //if over total time, reset
        if (timestampSize >= maxBubbleTime) {
            timestampSize = 0; //reset total //TODO: end spawning
        //otherwise set scale
        } else if (timestampSize < maxBubbleTime / 4) { //first quarter growth to 1.25 size
            scale = 1 + (timestampSize / maxBubbleTime);
        } else if (timestampSize < maxBubbleTime / 2){ //second quarter shrink to base size
            scale = 1 + (timestampSize / maxBubbleTime);
            double z = timestampSize / (maxBubbleTime / 2);
            scale -= ((timestampSize / maxBubbleTime) * z);
        } else { //second half shrink from base size to 0
            scale = (timestampSize / maxBubbleTime);
            scale = ((scale - 0.5d) / 0.5d); //normalize to 0-1 (instead of 0.5-1)
            scale = 1 - scale; //inverse, make smaller over time
        }

        //move + scale proportionally
        GlStateManager.translate(getRandomXPos(), getRandomYPos(), 0);
        GlStateManager.scale(scale, scale, 0);

        hasBeenResizedOrMoved = true;
    }

    //TODO: better (random) positions
    private int getRandomXPos() {
        return overlay.getGuiLocation().getMiddleX(imageTextureWidth);
    }

    //TODO: better (random) positions
    private int getRandomYPos() {
        return overlay.getGuiLocation().getMiddleY(imageTextureHeight);
    }


}
