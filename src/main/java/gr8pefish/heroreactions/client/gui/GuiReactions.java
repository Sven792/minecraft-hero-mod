package gr8pefish.heroreactions.client.gui;

import gr8pefish.heroreactions.api.HeroReactionsInfo;
import gr8pefish.heroreactions.network.hero.message.data.FeedbackTypes;
import gr8pefish.heroreactions.network.hero.message.data.StreamData;
import net.minecraft.client.Minecraft;
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

            //draw icon
            gui.drawTexturedModalRect(
                    xImage,  //screen x
                    yImage, //screen y
                    entry.getKey().getTextureX(), //texture x
                    0, //texture y
                    imageTextureWidth, //width
                    imageTextureHeight); //height

            //draw count of each underneath (TODO: refine)
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
