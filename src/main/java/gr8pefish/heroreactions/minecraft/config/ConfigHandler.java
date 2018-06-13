package gr8pefish.heroreactions.minecraft.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static gr8pefish.heroreactions.minecraft.api.HeroReactionsInfo.MODID;
import static gr8pefish.heroreactions.minecraft.api.HeroReactionsInfo.MOD_NAME;

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
        public float overlayScale = 1.0F; //TODO: apply config scaling

        @Config.Comment({ "Overlay Position (left, right)" })
        public String overlayPos = "right";

        @Config.Comment({ "Show debug data "})
        public boolean showDebug = false;
    }

    /**
     * Authorization config tweaks
     */
    public static class Auth {
        @Config.Comment({ "Store a token for automatically logging you in" })
        public boolean keepToken = true;
    }

}
