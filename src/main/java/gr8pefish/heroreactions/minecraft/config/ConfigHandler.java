package gr8pefish.heroreactions.minecraft.config;

import gr8pefish.heroreactions.minecraft.client.gui.overlay.GuiLocations;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static gr8pefish.heroreactions.minecraft.lib.ModInfo.MODID;

@Config(modid = MODID, name = MODID + "/" + MODID, category = "")
@Mod.EventBusSubscriber(modid = MODID)
public class ConfigHandler {

    @Config.Name("general")
    public static OverlayMain generalConfigSettings = new OverlayMain();
    @Config.Name("overlay")
    public static OverlaySpecifics overlayConfigSettings = new OverlaySpecifics();

    //Add config reloading
    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MODID)) {
            ConfigManager.sync(event.getModID(), Config.Type.INSTANCE); // Sync config values
        }
    }

    /**
     * General config tweaks
     */
    public static class OverlayMain {
        @Config.Comment({ "Enables the overlay" })
        public boolean enableOverlay = true;

        @Config.Comment({ "Show debug data"})
        public boolean showDebug = false;
    }

    /**
     *  Overlay config tweaks
     */
    public static class OverlaySpecifics {
        @Config.Comment({ "The maximum number of emojis of each type you will see visually at a time" })
        public int maxBubblesOfEachType = 5;

        @Config.Comment({ "The minimum number of emojis of each type you will see visually at a time" })
        public int minBubblesOfEachType = 1;

        @Config.Comment({ "Overlay Scale (0-1)" })
        @Config.RangeDouble(min = 0, max = 1)
        public double overlayScale = 0.5; //TODO: apply config scaling

        @Config.Comment({ "Emoji Scale (0-1)" })
        @Config.RangeDouble(min = 0, max = 1)
        public double emojiScale = 0.4;

        @Config.Comment({ "Overlay Position (left, right)" })
        public GuiLocations overlayPos = GuiLocations.RIGHT;

        @Config.Comment({ "Maximum time an emoji appears (in milliseconds)" })
        public double maxEmojiTime = 1000;

        @Config.Comment({ "Maximum time ratio to offset emojis spawning from one another" })
        public double maxEmojiOffsetTimeRatio = 1.5;

        @Config.Comment({ "How intense the glow effect is (0-255)" })
        @Config.RangeInt(min = 0, max = 255)
        public int maxGlowIntensity = 150;

        @Config.Comment({ "Percentage of the minimum transparency an emoji should have when fading away. Default is 10%."})
        @Config.RangeDouble(min = 0, max = 1)
        public double minOpacity = 0.1;
    }

}
