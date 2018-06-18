package gr8pefish.heroreactions.minecraft.client.gui;

import gr8pefish.heroreactions.minecraft.client.MinecraftRenderHelper;

import java.awt.*;

public class GuiGlow {

    private GuiIngameOverlay overlay;

    private final int MAX_INTENSITY = 100; //255 opaque, 0 transparent //TODO: Config value

    public GuiGlow(GuiIngameOverlay overlay) {
        this.overlay = overlay;
    }

    //TODO: Multiple colors depending on intensity
    //TODO: Refine color, make not just one rectangle gradient, go sideways as well (may have to do custom circle render from center or something)
    public void renderOverlay() {
        GuiLocations guiLocation = overlay.getGuiLocation();
        //set intensity based on stage size
        int intensity = (int) (MinecraftRenderHelper.stageSize * MAX_INTENSITY);
        //set top color
        int topColor = new Color(255, 0, 0, 0).getRGB(); //fully transparent red
        int bottomColor = new Color(255, 0, 0, intensity).getRGB(); //somewhat opaque same color
        overlay.drawGradientRect(guiLocation.getRescaledXStart(), guiLocation.getRescaledYStart(), guiLocation.getRescaledXStart()+ guiLocation.getRescaledWidth(), guiLocation.getRescaledYStart() + guiLocation.getRescaledHeight() + GuiLocations.paddingVertical, topColor, bottomColor); //have to due to drawGradientRect being protected
    }

}
