package gr8pefish.heroreactions.minecraft.client.gui;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.minecraft.config.ConfigHandler;
import net.minecraft.client.gui.ScaledResolution;

import java.util.Arrays;
import java.util.Random;

public enum GuiLocations {
    LEFT,
    RIGHT;

    private static GuiLocations DEFAULT = RIGHT;

    private int xStart;
    private int yStart;
    private int width;
    private int height;

    private int rescaledXStart;
    private int rescaledYStart;
    private int rescaledWidth;
    private int rescaledHeight;
    public static int MIN_SIZE_X = (int)(GuiReactions.imageTextureWidth * GuiReactions.scalingRatio * GuiReactions.growthRatio);
    public static int MIN_SIZE_Y = (int)(GuiReactions.imageTextureHeight * GuiReactions.scalingRatio * GuiReactions.growthRatio);

    public static final int HOTBAR_WIDTH = 91;

    public static int paddingHorizontal = 4; //padding from sides of screen and in-between elements
    public static int paddingVertical = 4; //padding in-between elements
    private static final int FONT_HEIGHT = 9; //Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    public static int verticalFiller = paddingVertical + GuiReactions.imageTextureWidth + paddingVertical + FONT_HEIGHT; //4 (padding) + 16 (texture size) + 4 (padding) + 9 (font size)

    GuiLocations() {
        //empty constructor (data set during applyScaling)
    }

    // Getters

    public int getStaticXStart() {
        return xStart;
    }

    public int getStaticYStart() {
        return yStart;
    }

    public int getStaticWidth() {
        return width;
    }

    public int getStaticHeight() {
        return height;
    }

    public int getRescaledXStart() {
        return rescaledXStart;
    }

    public int getRescaledYStart() {
        return rescaledYStart;
    }

    public int getRescaledWidth() {
        return rescaledWidth;
    }

    public int getRescaledHeight() {
        return rescaledHeight;
    }

    // Methods

    public static void applyPositionScaling(GuiLocations location, ScaledResolution scaledResolution) {
        switch (location) {
            case LEFT:
                Common.LOGGER.error("NOT YET IMPLEMENTED");
                break; //TODO: left side position logic
            case RIGHT:
                RIGHT.xStart = (scaledResolution.getScaledWidth() / 2) + HOTBAR_WIDTH + (paddingHorizontal / 2);
                RIGHT.width = scaledResolution.getScaledWidth() - RIGHT.xStart - (paddingHorizontal / 2);
                RIGHT.yStart = scaledResolution.getScaledHeight() - verticalFiller + (paddingVertical / 2);
                RIGHT.height = scaledResolution.getScaledHeight() - RIGHT.yStart - (paddingVertical / 2);
                break;
            default:
                Common.LOGGER.error("INVALID POSITION FOR OVERLAY, WRITE 'left' OR 'right' IN THE CONFIG!");
        }
    }

    public void applyStageSizeScaling(double scale) {

//        Common.LOGGER.info("Old spawn box: " + xStart + " " + width + " - " + yStart + " " + height);
//        Common.LOGGER.info("Rescaling to "+(int)(scale * 100)+"%");

        // x

        //get middle of render area
        int middle = this == RIGHT ? xStart + (width / 2): 0; //TODO: left side logic
        //rescale, and reset start pos
        rescaledWidth = (int) (width * scale); //get scaled width, and set width to that value //TODO: extreme small value error handling
        rescaledXStart = middle - (rescaledWidth / 2); //recenter to fit correct new width


        // y

        //get scaled height
        int scaledHeight = (int) (height * scale); //max height * scale //TODO: Borken on tiny value (too high) - just way off somehow (values don't add up)
        if (scaledHeight < MIN_SIZE_Y) {
            int upscaleValue = (int)(MIN_SIZE_Y * ((MIN_SIZE_Y - scaledHeight) / (double) scaledHeight)); //get percentage of difference from min as int (to add to the scaledHeight later)
            scaledHeight = MIN_SIZE_Y + upscaleValue; //upscale depending on how tiny it is (so not always just at min height w/ small values)
        }
        //set values
        rescaledHeight = scaledHeight;
        rescaledYStart = (yStart + height) - scaledHeight; //normal height - scaledHeight


        //print all (debug)
//        Common.LOGGER.info("Scaled spawn box: " + rescaledXStart + " " + rescaledWidth + " - " + rescaledYStart + " " + rescaledHeight);

    }

    public int getMiddleXUnscaledToStageSize(int textureSizeX) {
        return (this.xStart + (this.width / 2)) - (textureSizeX / 2); //middle of box, minus half the length of the texture - centered
    }

    public int getMiddleYUnscaledToStageSize(int textureSizeY) {
        return (this.yStart + (this.height / 2)) - (textureSizeY / 2);  //middle of box, minus half the length of the texture - centered
    }

    public static GuiLocations getLocationFromString(String string) {
        if (Arrays.asList(GuiLocations.values()).contains(string)) {
            return GuiLocations.valueOf(string);
        } else {
            return DEFAULT; //RIGHT
        }
    }

}
