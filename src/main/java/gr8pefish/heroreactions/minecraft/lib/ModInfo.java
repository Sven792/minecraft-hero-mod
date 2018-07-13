package gr8pefish.heroreactions.minecraft.lib;

import java.util.Locale;

public class ModInfo {

    /** The internally used mod ID **/
    public static final String MODID = "heroreactions";
    /** The externally used mod name **/
    public static final String MOD_NAME = "Hero Reactions";


    public static final String DOMAIN = MODID.toLowerCase(Locale.ENGLISH) + ":"; //for resources
    public static final String VERSION = "@VERSION@";

    public static final String COMMON_PROXY = "gr8pefish.heroreactions.minecraft.proxy.CommonProxy";
    public static final String CLIENT_PROXY = "gr8pefish.heroreactions.minecraft.proxy.ClientProxy";
}
