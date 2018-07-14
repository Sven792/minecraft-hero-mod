package gr8pefish.heroreactions.minecraft.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static gr8pefish.heroreactions.minecraft.lib.ModInfo.MODID;
import static gr8pefish.heroreactions.minecraft.lib.ModInfo.MOD_NAME;

@Config(modid = MODID, name = MOD_NAME + "/" + MODID)
@Mod.EventBusSubscriber(modid = MODID)
public class ConfigHandler {

    public static General generalConfigSettings = new General();
    public static Auth authConfigSettings = new Auth();
    public static Overlay overlayConfigSettings = new Overlay();

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
    public static class General {
        @Config.RequiresMcRestart
        @Config.Comment({ "Enables the mod" })
        public boolean enableMod = false;

        @Config.Comment({ "Enables the overlay" })
        public boolean enableOverlay = true;
    }

    /**
     *  Overlay config tweaks
     */
    public static class Overlay {
        @Config.Comment({ "Overlay Scale (0-1)" })
        public double overlayScale = 0.5; //TODO: apply config scaling

        @Config.Comment({ "Emoji Scale (0-1)" })
        public double emojiScale = 0.4;

        @Config.Comment({ "Overlay Position (left, right)" })
        public String overlayPos = "right";

        @Config.Comment({ "Maximum time an emoji appears (in milliseconds)" })
        public double maxEmojiTime = 1000;

        @Config.Comment({ "Maximum time ratio to offset emojis spawning from one another" })
        public double maxEmojiOffsetTimeRatio = 1.5;

        @Config.Comment({ "Show debug data "})
        public boolean showDebug = false;
    }

    /**
     * Authorization config tweaks
     */
    public static class Auth {
        @Config.Comment({ "The filepath to store login data at" })
        public String loginFilePath = System.getenv("APPDATA");
    }

}
