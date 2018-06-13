package gr8pefish.heroreactions.minecraft.client.gui;

import gr8pefish.heroreactions.common.client.CommonRenderHelper;
import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.hero.data.HeroData;
import gr8pefish.heroreactions.minecraft.api.HeroReactionsInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GuiReactions {

    /** Icons for the reaction emoticons */
    public static final ResourceLocation REACTION_ICONS_TEX_PATH = new ResourceLocation(HeroReactionsInfo.MODID,"textures/gui/reaction_icons.png");

    private GuiIngameOverlay overlay;
    private ConcurrentHashMap<FeedbackTypes, Double> feedbackRatios;

    public static final double maxFadeInTime = 2500;
    public static double timestampTotal = 0;

    //setup basic variables
    public final int imageTextureWidth = 16; //16 pixel square
    public final int imageTextureHeight = 16; //16 pixel square
    public int paddingHorizontal = 6; //padding from sides of screen and in-between elements
    public int paddingVertical = 4; //padding in-between elements

    public int xBase;
    public int yText;
    public int yImage;

    public GuiReactions(GuiIngameOverlay overlay) {
        this.overlay = overlay;

        //feedback data
        feedbackRatios = HeroData.FeedbackActivity.getFeedbackRatios();

        //direct variables used in rendering
//        xBase = (feedbackCount % 2 == 0) ? middle - (feedbackCount / 2) - (((feedbackCount / 2) / 2) * paddingHorizontal) : middle - (imageTextureWidth / 2) - ((imageTextureWidth + paddingHorizontal) * (feedbackCount / 2)); //start centered, depends on even or odd
//        yText = height - paddingVertical - 8; //bottom, padding, height of actual number
//        yImage = yText - paddingVertical - imageTextureHeight; //text height, padding, image height
        //ToDo: Check y limits (with padding) against edge of screen and hotbar/offhand bar

        //setup vars for public access
        //TODO: refactor (with above vars to class level)
//        centerAboveY = yImage - (paddingVertical + (paddingVertical / 2)) - 8; //image top, 1.5x padding, height of actual number

    }

    public void renderOverlay() {
        overlay.getMinecraft().getTextureManager().bindTexture(REACTION_ICONS_TEX_PATH);

        //TODO: fade in/out
        getAlpha();

        //draw icon
        overlay.drawTexturedModalRect(
                overlay.getGuiLocation().getMiddleX(imageTextureWidth),  //screen x
                overlay.getGuiLocation().getMiddleY(imageTextureHeight), //screen y
                FeedbackTypes.LOVE.getTextureX(), //texture x
                0, //texture y
                imageTextureWidth, //width
                imageTextureHeight); //height
    }

    private void getAlpha() {

        //Too many dependencies/jumping files - bwah
        CommonRenderHelper.renderFade(overlay.currentTime, overlay.timeDifference, overlay.baseTime, FeedbackTypes.LOVE, 0.1f); //yay flickering xD



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

    }

    public void renderOverlay(int width, int height, int middle) {

        //for looping through feedback icons
        int feedbackIterator = 0;

        //loop through elements and draw
        for (Map.Entry<FeedbackTypes, Double> entry : feedbackRatios.entrySet()) {

            //unsure why, but seems necessary to bind more than just in the beginning
            overlay.getMinecraft().getTextureManager().bindTexture(REACTION_ICONS_TEX_PATH);

            //variables
            int xImage = xBase + (feedbackIterator * (imageTextureWidth + paddingHorizontal));
            int xText = xImage + (imageTextureWidth / 2) - (overlay.getMinecraft().fontRenderer.getStringWidth(entry.getValue().toString()) / 2);

            //half size - translate then scale (push/pop matrix as well)
            GlStateManager.pushMatrix();
            GlStateManager.translate(xImage, yImage, 0);
            GlStateManager.scale(0.5, 0.5, 0);

            //enable transparency
            GlStateManager.enableAlpha(); //can cause weird transparent cutout issues, but positive affects performance (dependent on transparent pixel %) if no issues present
            GlStateManager.enableBlend(); //enable blending
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA); //black magic that is necessary
            GlStateManager.color(1, 1, 1, 0.5f); //halve opacity

            //use system time for keeping track (otherwise it will lag) - because doesn't really care about world

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

            //draw icon
            overlay.drawTexturedModalRect(
                    0,  //screen x
                    0, //screen y
                    entry.getKey().getTextureX(), //texture x
                    0, //texture y
                    imageTextureWidth, //width
                    imageTextureHeight); //height

            GlStateManager.popMatrix();

            //draw count of each underneath
            overlay.drawString(
                    overlay.getMinecraft().fontRenderer, //fontRenderer
                    entry.getValue().toString(), //what to draw
                    xText, //screen x
                    yText, //screen y
                    14737632);

            //increment
            feedbackIterator++;
        }
    }

    //Render emojis in a limited area
    private void renderBubblingReactions(int xStart, int yStart, int width, int height, FeedbackTypes reaction, Double percentage, int total) {
        //unsure why, but seems necessary to bind more than just in the beginning
        overlay.getMinecraft().getTextureManager().bindTexture(REACTION_ICONS_TEX_PATH);

        //half size - translate then scale (push/pop matrix as well)
//        GlStateManager.pushMatrix();
//        GlStateManager.translate(xImage, yImage, 0);
//        GlStateManager.scale(0.5, 0.5, 0);
//
//        //enable transparency (not working?)
//        GlStateManager.enableAlpha(); //can cause weird transparent cutout issues, but positive affects performance (dependent on transparent pixel %) if no issues present
//        GlStateManager.enableBlend(); //enable blending
//        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA); //black magic that is necessary
//        GlStateManager.color(1, 1, 1, 0.5f); //halve opacity

        //use system time for keeping track (otherwise it will lag)

        //do it for 10x percent (so .5 = 5)
        for (int i = 0 ; i < percentage * 10; i++) {

            int xImage = xBase + (int)Math.round((Math.random() * 5)* 10); //random location
            int yImageReal = yImage + (int)Math.round((Math.random() * 5)* 3); //random location

            //draw icon
            overlay.drawTexturedModalRect(
                    xImage,  //screen x
                    yImageReal, //screen y
                    reaction.getTextureX(), //texture x
                    0, //texture y
                    imageTextureWidth, //width
                    imageTextureHeight); //height
        }

//        GlStateManager.popMatrix();
    }

    public void renderFeedbackBubblingFromReactionRatios() {
        ConcurrentHashMap<FeedbackTypes, Double> feedbackRatios = HeroData.FeedbackActivity.getFeedbackRatios();
        for (Map.Entry<FeedbackTypes, Double> entry : feedbackRatios.entrySet()) {
            renderBubblingReactions(xBase, yImage, imageTextureWidth * feedbackRatios.size(), imageTextureHeight + paddingVertical, entry.getKey(), entry.getValue(), HeroData.FeedbackActivity.totalFeedbackCount);
        }
    }

    public void renderFeedbackBubble(FeedbackTypes feedbackType) {
        //alpha set, just have to render bubble in location

        overlay.getMinecraft().getTextureManager().bindTexture(REACTION_ICONS_TEX_PATH);

        //TODO: randomize location

        //draw icon
        overlay.drawTexturedModalRect(
                overlay.getGuiLocation().getMiddleX(imageTextureWidth),  //screen x
                overlay.getGuiLocation().getMiddleY(imageTextureHeight), //screen y
                FeedbackTypes.LOVE.getTextureX(), //texture x
                0, //texture y
                imageTextureWidth, //width
                imageTextureHeight); //height
    }
}
