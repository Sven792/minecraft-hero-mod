package gr8pefish.heroreactions.config;

import net.minecraftforge.common.config.Config;

import static gr8pefish.heroreactions.api.HeroReactionsInfo.MODID;
import static gr8pefish.heroreactions.api.HeroReactionsInfo.MOD_NAME;

@Config(modid = MODID, name = MOD_NAME + "/" + MODID)
public class ConfigHandler {

    public static General generalConfigSettings = new General();
    public static Auth authConfigSettings = new Auth();
    public static Overlay overlayConfigSettings = new Overlay();

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
     * Overlay config tweaks
     */
    public static class Auth {
        @Config.Comment({ "Store a token" })
        public boolean keepToken = true;
    }

    /**
     * Authorization config tweaks
     */
    public static class Overlay {
        @Config.Comment({ "Overlay Scale" })
        public float overlayScale = 0.5F;

        @Config.Comment({ "Overlay X-Position (center, left, right)" })
        public String overlayXpos = "right";

        @Config.Comment({ "Overlay Y-Position (top, middle, bottom)" })
        public String overlayYpos = "bottom";
    }

}
