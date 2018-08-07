package gr8pefish.heroreactions.minecraft.client.gui.overlay;

import gr8pefish.heroreactions.hero.data.UserData;
import gr8pefish.heroreactions.hero.network.message.MessageHelper;
import gr8pefish.heroreactions.minecraft.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * The main class for rendering the feedback overlay.
 *
 * Has helper classes to render the activity glow ({@link GuiGlow} and the individual emoji bubbles ({@link GuiReactions}.
 * Note: Only one overlay created/rendered (in {@link gr8pefish.heroreactions.minecraft.client.ClientEventHandler}), the data is just updated as needed.
 */
public class GuiIngameOverlay extends Gui {

    //Fields
    @Nonnull
    private final Minecraft mc;
    @Nonnull
    private ScaledResolution scaledResolution;
    @Nonnull
    private GuiLocations guiLocation;
    @Nonnegative
    private int viewCount;
    @Nonnull
    private GuiReactions reactions;
    @Nonnull
    private GuiGlow glow;

    private boolean renderPopupURL;
    private String url = "hero.tv/"+ UserData.HASH_ID.retrieve();

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
        this.guiLocation = ConfigHandler.overlayConfigSettings.overlayPos;
        GuiLocations.applyPositionScaling(guiLocation, scaledResolution);

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

    public void setGuiLocation(GuiLocations location) {
        this.guiLocation = location;
    }

    public GuiReactions getReactions() {
        return reactions;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void setRenderPopupURL(boolean renderPopupURL) {
        this.renderPopupURL = renderPopupURL;
    }

    //Render

    public void renderOverlay(ScaledResolution scaledResolution) {
        //set scaled resolution
        this.scaledResolution = scaledResolution;

        //set time
        updateTime();

        //draw view count
        String viewers = String.valueOf(viewCount);
        drawString(mc.fontRenderer, viewers, guiLocation.getMiddleXUnscaledToStageSize(mc.fontRenderer.getStringWidth(viewers)), guiLocation.getStaticYStart(), 14737632); //14737632 is the vanilla white text color

        //render glow
        glow.renderOverlay();

        //render reactions
        reactions.renderAllBubbles();

        //draw debug data (hardcoded to top left)
        if (ConfigHandler.generalConfigSettings.showDebug) {
            ArrayList<String> msgArray = MessageHelper.getStreamData();
            int top = 2;
            for (String msg : msgArray) {
                if (msg == null || msg.isEmpty()) continue;
                Gui.drawRect(1, top - 1, 2 + mc.fontRenderer.getStringWidth(msg) + 1, top + mc.fontRenderer.FONT_HEIGHT - 1, -1873784752);
                mc.fontRenderer.drawString(msg, 2, top, 14737632);
                top += mc.fontRenderer.FONT_HEIGHT;
            }
        }

        //draw popup URL string
        if (renderPopupURL) {
            switch (ConfigHandler.overlayConfigSettings.urlPopupLocation) {
                case 1: //bottom right
                    int padding = 2;
                    int x = scaledResolution.getScaledWidth() - mc.fontRenderer.getStringWidth(url) - padding;
                    int y = scaledResolution.getScaledHeight() - mc.fontRenderer.FONT_HEIGHT - padding;
                    mc.fontRenderer.drawString(url, x, y, 14737632);
                    break;
                case 2: //above view count
                    drawString(mc.fontRenderer, url, guiLocation.getMiddleXUnscaledToStageSize(mc.fontRenderer.getStringWidth(url)), guiLocation.getStaticYStart() - mc.fontRenderer.FONT_HEIGHT - 2, 14737632);
            }
        }

    }

    //propagates this to public scope so it can be called from GuiGlow
    public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        super.drawGradientRect(left, top, right, bottom, startColor, endColor);
    }

    public void updateTime() {
        this.currentTime = Minecraft.getSystemTime();
        this.timeDifference = currentTime - lastTime;
        this.lastTime = currentTime;
    }

}
