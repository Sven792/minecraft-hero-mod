package gr8pefish.heroreactions.common.lib;

import java.util.Locale;

import static gr8pefish.heroreactions.api.HeroReactionsInfo.MODID;

public class ModInfo {

    public static final String DOMAIN = MODID.toLowerCase(Locale.ENGLISH) + ":"; //for resources
    public static final String VERSION = "@VERSION@";
    public static final String GUI_FACTORY = "gr8pefish.heroreactions.client.gui.GuiFactory"; //for in-game config

    public static final String COMMON_PROXY = "gr8pefish.heroreactions.common.proxy.CommonProxy";
    public static final String CLIENT_PROXY = "gr8pefish.heroreactions.client.proxy.ClientProxy";

    public static final String NETWORK_CHANNEL = "herorxns";

}
