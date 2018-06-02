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

    public GuiReactions(Minecraft minecraft) {
        this.mc = minecraft;
    }

    @Override
    public void renderOverlay() {
        mc.getTextureManager().bindTexture(REACTION_ICONS_TEX_PATH);
    }

    public void renderOverlay(int width, int height, GuiIngameOverlay gui) {

        //setup variables
        int x = 2; //padding
        int yText = height - 10; //bottom, 2 for padding
        int y = yText - 16 - 2; //16 for texture, 2 for padding
        int textureWidth = 16; //16 pixel square
        int textureHeight = 16; //16 pixel square

        //for looping through feedback icons
        int feedbackIterator = 0;

        ConcurrentHashMap<FeedbackTypes, Integer> feedback = StreamData.FeedbackActivity.getFeedbackActivity();
        for (Map.Entry<FeedbackTypes, Integer> entry : feedback.entrySet()) {

            //unsure why, but I do have to bind more than just in the beginning
            mc.getTextureManager().bindTexture(REACTION_ICONS_TEX_PATH);

            //draw icon
            gui.drawTexturedModalRect(
                    x + (feedbackIterator * (textureWidth + 3)),  //screen x
                    y, //screen y
                    entry.getKey().getTextureX(), //texture x
                    0, //texture y
                    textureWidth, //width
                    textureHeight); //height

            //draw count of each underneath (TODO: refine)
            gui.drawString(gui.getFontRenderer(), entry.getValue().toString(), x + (textureWidth/2) + (feedbackIterator * textureWidth), yText, 14737632);

            //increment
            feedbackIterator++;
        }
    }

}
