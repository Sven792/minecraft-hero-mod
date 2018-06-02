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

    public GuiIngameOverlay(Minecraft minecraft) {
        //super call
        super(minecraft);
        //client minecraft instance (for rendering)
        this.mc = minecraft;
        //view count
        this.viewCount = 0;
        //reactions
        this.reactions = new GuiReactions(minecraft);
    }

    @Override
    public void renderGameOverlay(float partialTicks) {
        res = new ScaledResolution(mc);
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        fontRenderer = mc.fontRenderer;

        renderOverlay(width, height);
    }

    public void renderOverlay(int width, int height) {
        //render main
        renderOverlayMain(MessageHelper.getStreamData(), height);
        //render child
        reactions.renderOverlay(width, height, this);
    }

    public void renderOverlayMain(ArrayList<String> msgArray, int height) {

        //draw data in top left corner
        int top = 2;
        for (String msg : msgArray) {
            if (msg == null || msg.isEmpty()) continue;
            Gui.drawRect(1, top - 1, 2 + fontRenderer.getStringWidth(msg) + 1, top + fontRenderer.FONT_HEIGHT - 1, -1873784752);
            fontRenderer.drawString(msg, 2, top, 14737632);
            top += fontRenderer.FONT_HEIGHT;
        }

        //render view count
        int viewers = StreamData.Viewers.direct;
        //TODO: center correctly
        drawString(fontRenderer, String.valueOf(viewers), 2 + (8) + (1 * 16) - 2, height - 30 - 2 - 10, 14737632); //TODO: Constants in GuiReactions access for no magic numbers
    }

}
