package gr8pefish.heroreactions.common;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

/** Any information that is overlapping with both Minecraft and Hero data. (Should be minimal) */
public class Common {

    /** Set in {@link gr8pefish.heroreactions.minecraft.HeroReactions#preInit(FMLPreInitializationEvent)}*/
    public static Logger LOGGER;

}
