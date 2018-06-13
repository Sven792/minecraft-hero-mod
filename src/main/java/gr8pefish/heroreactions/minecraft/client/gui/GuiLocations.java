package gr8pefish.heroreactions.minecraft.client.gui;

import gr8pefish.heroreactions.common.Common;
import net.minecraft.client.gui.ScaledResolution;

public enum GuiLocations {
    LEFT,
    RIGHT;

    int xStart;
    int yStart;
    int width;
    int height;

    public static final int HOTBAR_WIDTH = 91;

    GuiLocations() {
        //empty constructor (data set during applyScaling)
    }

    public static void applyPositionScaling(String side, ScaledResolution scaledResolution) {
        switch (GuiLocations.valueOf(side)){
            case LEFT:
                Common.LOGGER.error("NOT YET IMPLEMENTED");
                break; //TODO: left side position logic
            case RIGHT:
                RIGHT.xStart = (scaledResolution.getScaledWidth() / 2) + HOTBAR_WIDTH;
                RIGHT.width = scaledResolution.getScaledWidth() - RIGHT.xStart;
                RIGHT.yStart = scaledResolution.getScaledHeight() - 34; //4 (padding) + 16 (texture size) + 4 (padding) + 8 (font size) + 2 (padding)
                RIGHT.height = scaledResolution.getScaledHeight() - RIGHT.yStart;
                break;
            default:
                Common.LOGGER.error("INVALID POSITION FOR OVERLAY, WRITE 'left' OR 'right' IN THE CONFIG!");
        }
    }

    public int getMiddleX(int textureSizeX) {
        return (this.xStart + (this.width / 2)) - (textureSizeX / 2); //middle of box, minus half the length of the texture - centered
    }

    public int getMiddleY(int textureSizeY) {
        return (this.yStart + (this.height / 2)) - (textureSizeY / 2);  //middle of box, minus half the length of the texture - centered
    }

}
