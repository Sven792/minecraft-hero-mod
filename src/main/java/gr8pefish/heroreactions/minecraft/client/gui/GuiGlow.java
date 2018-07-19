package gr8pefish.heroreactions.minecraft.client.gui;

import gr8pefish.heroreactions.minecraft.client.MinecraftRenderHelper;
import gr8pefish.heroreactions.minecraft.config.ConfigHandler;

import java.awt.*;

/**
 * Shows the glow for the intensity of the reactions
 */
public class GuiGlow {

    /** The parent overlay */
    private GuiIngameOverlay overlay;
    /** The maximum intensity of the glow effect */
    private final int MAX_INTENSITY = ConfigHandler.overlayConfigSettings.maxGlowIntensity; //255 opaque, 0 transparent

    public GuiGlow(GuiIngameOverlay overlay) {
        this.overlay = overlay;
    }

    /** Actually render the glow overlay */
    public void renderOverlay() {
        //get location
        GuiLocations guiLocation = overlay.getGuiLocation();
        //set intensity based on stage size
        //TODO: Intensity calculation more refined - e.g. based on changes over time
        int intensity = (int) (MinecraftRenderHelper.stageSize * MAX_INTENSITY);
        //set top color
        //TODO: Multiple colors
        int topColor = new Color(255, 0, 0, 0).getRGB(); //fully transparent red
        int bottomColor = new Color(255, 0, 0, intensity).getRGB(); //somewhat opaque same color
        //draw a rectangle of the appropriate size
        //TODO: More refined shape - e.g. custom circle shape
        overlay.drawGradientRect(guiLocation.getRescaledXStart(), guiLocation.getRescaledYStart(), guiLocation.getRescaledXStart()+ guiLocation.getRescaledWidth(), guiLocation.getRescaledYStart() + guiLocation.getRescaledHeight() + GuiLocations.paddingVertical, topColor, bottomColor); //have to due to drawGradientRect being protected
    }

}
