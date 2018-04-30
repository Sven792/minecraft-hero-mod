package gr8pefish.heroreactions.config;

import net.minecraftforge.common.config.Config;

import static gr8pefish.heroreactions.api.HeroReactionsInfo.MODID;
import static gr8pefish.heroreactions.api.HeroReactionsInfo.MOD_NAME;

@Config(modid = MODID, name = MOD_NAME + "/" + MODID)
public class ConfigHandler {

    public static General generalConfigSettings = new General();

    /**
     * General config tweaks
     */
    public static class General {
        @Config.Comment({ "Enables the overlay" })
        public boolean enableOverlay = false;
    }

}
