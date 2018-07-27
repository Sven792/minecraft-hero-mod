package gr8pefish.heroreactions.minecraft.client.gui.overlay;

import gr8pefish.heroreactions.common.Common;
import net.minecraft.client.gui.ScaledResolution;

import java.util.Arrays;

/**
 * The location to render the overlay in, on the screen. Choices are bottom [left] or [right].
 */
public enum GuiLocations {
    LEFT,
    RIGHT;

    //Default location is the right
    private static GuiLocations DEFAULT = RIGHT;

    //The boundings of the box
    private int xStart;
    private int yStart;
    private int width;
    private int height;

    //The box boundings rescaled to fit the screen size and stage/view count-modified size
    private int rescaledXStart;
    private int rescaledYStart;
    private int rescaledWidth;
    private int rescaledHeight;

    //The width of the vanilla hotbar, in pixels, for placement purposes
    public static final int HOTBAR_WIDTH = 91;
    //Other fields for static data
    public static int paddingHorizontal = 4; //padding from sides of screen and in-between elements
    public static int paddingVertical = 4; //padding in-between elements
    private static final int FONT_HEIGHT = 9; //Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    public static int verticalFiller = paddingVertical + GuiReactions.imageTextureHeight + paddingVertical + FONT_HEIGHT; //4 (padding) + 16 (texture size) + 4 (padding) + 9 (font size)

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

    /**
     * Apply the position to the actual screen, instantiating all the requisite data.
     *
     * @param location - the location (left/right) to display on
     * @param scaledResolution - the screen's scaled resolution
     */
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

    /**
     * Rescales the stage/location size depending on the scaling factor (from how many viewers watching)
     * Note: Minimum stage size = 1/2 maximum everything (so half max width and half max height)
     *
     * @param scale - between 0 and 1, inclusive - the percentage to scale to
     */
    public void applyStageSizeScaling(double scale) {

//        Common.LOGGER.info("Old spawn box: " + xStart + " " + width + " - " + yStart + " " + height);
//        Common.LOGGER.info("Rescaling to "+(int)(scale * 100)+"%");

        // x

        //get middle of render area
        int middle = this == RIGHT ? xStart + (width / 2): 0; //TODO: left side logic
        //minimum is half of total width
        int min_x = width / 2;
        //upscale from minimum by scaling amount
        int upscaleValueX = (int) (scale * (min_x /2));
        //add upscale value to base to get actual
        rescaledWidth = min_x + upscaleValueX;
        //get new XStart (centered)
        rescaledXStart = middle - (rescaledWidth / 2);

        // y

        //minimum is half of total height
        int min_y = height / 2;
        //upscale from minimum by scaling amount
        int upscaleValueY = (int) (scale * (min_y / 2));
        //add upscale value to base to get actual
        rescaledHeight = min_y + upscaleValueY;
        //get new yStart
        rescaledYStart = (yStart + height) - rescaledHeight; //normal height - scaledHeight

//        Common.LOGGER.info("Scaled spawn box: " + rescaledXStart + " " + rescaledWidth + " - " + rescaledYStart + " " + rescaledHeight);

    }

    /** Unscaled center, useful for static text (i.e. view count) */
    public int getMiddleXUnscaledToStageSize(int textureSizeX) {
        return (this.xStart + (this.width / 2)) - (textureSizeX / 2); //middle of box, minus half the length of the texture - centered
    }

    /** Safe way to access the location from a string (obtained via config) */
    public static GuiLocations getLocationFromString(String string) {
        if (Arrays.asList(GuiLocations.values()).contains(string)) {
            return GuiLocations.valueOf(string);
        } else {
            return DEFAULT; //RIGHT
        }
    }

}
