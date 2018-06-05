package gr8pefish.heroreactions.client.gui;

import gr8pefish.heroreactions.network.hero.message.MessageHelper;
import gr8pefish.heroreactions.network.hero.message.data.StreamData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;

//extension may be unnecessary
public class GuiIngameOverlay extends GuiIngame {

    private final Minecraft mc;
    private int viewCount;
    private GuiReactions reactions;

    private ScaledResolution res = null;
    private FontRenderer fontRenderer = null;

    private int middle;

    public GuiIngameOverlay(Minecraft minecraft) {
        //super call
        super(minecraft);
        //client minecraft instance (for rendering)
        this.mc = minecraft;
        //view count
        this.viewCount = 0;
        //reactions
        res = new ScaledResolution(mc);
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        this.middle = (((res.getScaledWidth() / 2) + 91) + res.getScaledWidth()) / 2; //(very middle + hotbar size + total size) / 2 = custom middle
        this.reactions = new GuiReactions(minecraft, width, height, this.middle);
    }

    @Override
    public void renderGameOverlay(float partialTicks) {
//        res = new ScaledResolution(mc);
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
//        int middle = (((res.getScaledWidth() / 2) + 91) + res.getScaledWidth()) / 2; //(very middle + hotbar size + total size) / 2 = custom middle
        fontRenderer = mc.fontRenderer;

        //ToDo: get center from right side to (main) - need to account for offhand if left
        renderOverlay(width, height, this.middle);
    }

    public void renderOverlay(int width, int height, int middle) {
        //render main
        renderOverlayMain(MessageHelper.getStreamData(), height, middle);
        //TODO: test
//        this.drawGradientRect(0, 0, width, height, 1615855616, -1602211792);
        //render child
        reactions.renderOverlay(width, height, middle, this);
    }

    public void renderOverlayMain(ArrayList<String> msgArray, int height, int middle) {

        //draw data in top left corner
        int top = 2;
        for (String msg : msgArray) {
            if (msg == null || msg.isEmpty()) continue;
            Gui.drawRect(1, top - 1, 2 + fontRenderer.getStringWidth(msg) + 1, top + fontRenderer.FONT_HEIGHT - 1, -1873784752);
            fontRenderer.drawString(msg, 2, top, 14737632);
            top += fontRenderer.FONT_HEIGHT;
        }

        //render view count, centered
        String viewers = String.valueOf(StreamData.Viewers.direct + StreamData.Viewers.indirect);
        drawString(fontRenderer, viewers, middle - (fontRenderer.getStringWidth(viewers)/2), reactions.centerAboveY, 14737632); //TODO: Constants in GuiReactions access for no magic numbers
    }

}
