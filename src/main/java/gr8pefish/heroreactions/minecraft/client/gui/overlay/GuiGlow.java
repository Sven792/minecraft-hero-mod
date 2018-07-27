package gr8pefish.heroreactions.minecraft.client.gui.overlay;

import gr8pefish.heroreactions.minecraft.client.MinecraftRenderHelper;
import gr8pefish.heroreactions.minecraft.config.ConfigHandler;

import java.awt.*;

/**
 * Shows the glow for the intensity of the reactions
 */
public class GuiGlow {

    /** The parent overlay */
    private GuiIngameOverlay overlay;

    public GuiGlow(GuiIngameOverlay overlay) {
        this.overlay = overlay;
    }

    /** Actually render the glow overlay */
    public void renderOverlay() {
        //get location
        GuiLocations guiLocation = overlay.getGuiLocation();
        //get maximum intensity
        int max_intensity = ConfigHandler.overlayConfigSettings.maxGlowIntensity; //255 opaque, 0 transparent
        //set actual intensity based on stage size
        //TODO: Intensity calculation more refined - e.g. based on changes over time
        int intensity = (int) (MinecraftRenderHelper.stageSize * max_intensity);
        //set top color
        //TODO: Multiple colors
        int topColor = new Color(255, 0, 0, 0).getRGB(); //fully transparent red
        int bottomColor = new Color(255, 0, 0, intensity).getRGB(); //somewhat opaque same color
        //draw a rectangle of the appropriate size
        //TODO: More refined shape - e.g. custom circle shape
        overlay.drawGradientRect(guiLocation.getRescaledXStart(), guiLocation.getRescaledYStart(), guiLocation.getRescaledXStart()+ guiLocation.getRescaledWidth(), guiLocation.getRescaledYStart() + guiLocation.getRescaledHeight() + GuiLocations.paddingVertical, topColor, bottomColor); //have to due to drawGradientRect being protected
    }

    //==============----------------- Notes below here -------------=====================

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

}
