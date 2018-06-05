package gr8pefish.heroreactions.client.gui;

import gr8pefish.heroreactions.api.HeroReactionsInfo;
import gr8pefish.heroreactions.network.hero.message.data.FeedbackTypes;
import gr8pefish.heroreactions.network.hero.message.data.StreamData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GuiReactions implements IRenderOverlay {

    /** Icons for each reaction (static) */
    public static final ResourceLocation REACTION_ICONS_TEX_PATH = new ResourceLocation(HeroReactionsInfo.MODID,"textures/gui/reaction_icons.png");

    private final Minecraft mc;

    private ConcurrentHashMap<FeedbackTypes, Integer> feedback;

    //setup basic variables
    private final int imageTextureWidth = 16; //16 pixel square
    private final int imageTextureHeight = 16; //16 pixel square
    private int paddingHorizontal = 6; //padding from sides of screen and in-between elements
    private int paddingVertical = 4; //padding in-between elements

    private int xBase;
    private int yText;
    private int yImage;

    public int centerAboveY = -1;

    public GuiReactions(Minecraft minecraft, int width, int height, int middle) {
        this.mc = minecraft;

        //feedback data
        feedback = StreamData.FeedbackActivity.getFeedbackActivity();
        int feedbackCount = feedback.size();

        //direct variables used in rendering
        xBase = (feedbackCount % 2 == 0) ? middle - (feedbackCount / 2) - (((feedbackCount / 2) / 2) * paddingHorizontal) : middle - (imageTextureWidth / 2) - ((imageTextureWidth + paddingHorizontal) * (feedbackCount / 2)); //start centered, depends on even or odd
        yText = height - paddingVertical - 8; //bottom, padding, height of actual number
        yImage = yText - paddingVertical - imageTextureHeight; //text height, padding, image height
        //ToDo: Check y limits (with padding) against edge of screen and hotbar/offhand bar

        //setup vars for public access
        //TODO: refactor (with above vars to class level)
        centerAboveY = yImage - (paddingVertical + (paddingVertical / 2)) - 8; //image top, 1.5x padding, height of actual number

    }

    @Override
    public void renderOverlay() {
        mc.getTextureManager().bindTexture(REACTION_ICONS_TEX_PATH);
    }

    public void renderOverlay(int width, int height, int middle, GuiIngameOverlay gui) {

        //for looping through feedback icons
        int feedbackIterator = 0;

        //loop through elements and draw
        for (Map.Entry<FeedbackTypes, Integer> entry : feedback.entrySet()) {

            //unsure why, but seems necessary to bind more than just in the beginning
            mc.getTextureManager().bindTexture(REACTION_ICONS_TEX_PATH);

            //variables
            int xImage = xBase + (feedbackIterator * (imageTextureWidth + paddingHorizontal));
            int xText = xImage + (imageTextureWidth / 2) - (gui.getFontRenderer().getStringWidth(entry.getValue().toString()) / 2);

            //half size - translate then scale (push/pop matrix as well)
            GlStateManager.pushMatrix();
            GlStateManager.translate(xImage, yImage, 0);
            GlStateManager.scale(0.5, 0.5, 0);

            //enable transparency (not working?)
            GlStateManager.enableAlpha(); //can cause weird transparent cutout issues, but positive affects performance (dependent on transparent pixel %) if no issues present
            GlStateManager.enableBlend(); //enable blending
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA); //black magic that is necessary
            GlStateManager.color(1, 1, 1, 0.5f); //halve opacity

            //use system time for keeping track (otherwise it will lag)

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
            gui.drawTexturedModalRect(
                    0,  //screen x
                    0, //screen y
                    entry.getKey().getTextureX(), //texture x
                    0, //texture y
                    imageTextureWidth, //width
                    imageTextureHeight); //height

            GlStateManager.popMatrix();

            //draw count of each underneath
            gui.drawString(
                    gui.getFontRenderer(), //fontRenderer
                    entry.getValue().toString(), //what to draw
                    xText, //screen x
                    yText, //screen y
                    14737632);

            //increment
            feedbackIterator++;
        }
    }

}
