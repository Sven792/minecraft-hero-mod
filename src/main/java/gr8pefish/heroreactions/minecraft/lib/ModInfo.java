package gr8pefish.heroreactions.minecraft.lib;

import java.util.Locale;

import static gr8pefish.heroreactions.minecraft.api.HeroReactionsInfo.MODID;

public class ModInfo {

    public static final String DOMAIN = MODID.toLowerCase(Locale.ENGLISH) + ":"; //for resources
    public static final String VERSION = "@VERSION@";

    public static final String COMMON_PROXY = "gr8pefish.heroreactions.minecraft.proxy.CommonProxy";
    public static final String CLIENT_PROXY = "gr8pefish.heroreactions.minecraft.proxy.ClientProxy";

    public static final String NETWORK_CHANNEL = "herorxns";

}
