package gr8pefish.heroreactions.minecraft.client.gui;

import gr8pefish.heroreactions.hero.network.message.MessageHelper;
import gr8pefish.heroreactions.minecraft.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;

/**
 * A custom overlay for the Hero data.
 * Note: Only one overlay created/rendered (in {@link gr8pefish.heroreactions.minecraft.client.ClientEventHandler}), the data is just updated as needed.
 */
public class GuiIngameOverlay extends Gui {

    //Fields
    private final Minecraft mc;
    private ScaledResolution scaledResolution;
    private GuiLocations guiLocation;
    private int viewCount;
    private GuiReactions reactions;
    private GuiGlow glow;

    public long lastTime;
    public long currentTime;
    public long timeDifference;
    public long baseTime;

    public GuiIngameOverlay(Minecraft minecraft) {
        //client minecraft instance (for rendering)
        this.mc = minecraft;
        //scaled resolution
        this.scaledResolution = new ScaledResolution(minecraft);
        //guiLocation
        this.guiLocation = GuiLocations.valueOf(ConfigHandler.overlayConfigSettings.overlayPos.toUpperCase()); //TODO: Error handling

        //view count
        this.viewCount = 0;
        //reactions
        this.reactions = new GuiReactions(this);
        //glow
        this.glow = new GuiGlow(this);

        //time
        this.lastTime = Minecraft.getSystemTime();
        this.baseTime = Minecraft.getSystemTime();
    }

    //Getters + Setters

    public Minecraft getMinecraft() {
        return mc;
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }

    public GuiLocations getGuiLocation() {
        return guiLocation;
    }

    public GuiReactions getReactions() {
        return reactions;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    //Render

    public void renderOverlay(ScaledResolution scaledResolution) {
        //set scaled resolution
        this.scaledResolution = scaledResolution;

        //set time
        this.currentTime = Minecraft.getSystemTime();
        this.timeDifference = currentTime - lastTime;
        this.lastTime = currentTime;

        //draw view count
        String viewers = String.valueOf(viewCount);
        drawString(mc.fontRenderer, viewers, guiLocation.getMiddleX(mc.fontRenderer.getStringWidth(viewers)), guiLocation.yStart - mc.fontRenderer.FONT_HEIGHT, 14737632); //-2 for more y padding (text is on top of spawn box for reactions), 14737632 is the vanilla white text color

        //render glow
        glow.renderOverlay();

        //render reactions
        reactions.renderOverlay();

        //draw debug data (hardcoded to top left)
        if (ConfigHandler.overlayConfigSettings.showDebug) {
            ArrayList<String> msgArray = MessageHelper.getStreamData();
            int top = 2;
            for (String msg : msgArray) {
                if (msg == null || msg.isEmpty()) continue;
                Gui.drawRect(1, top - 1, 2 + mc.fontRenderer.getStringWidth(msg) + 1, top + mc.fontRenderer.FONT_HEIGHT - 1, -1873784752);
                mc.fontRenderer.drawString(msg, 2, top, 14737632);
                top += mc.fontRenderer.FONT_HEIGHT;
            }
        }

    }

}
