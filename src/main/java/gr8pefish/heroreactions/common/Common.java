package gr8pefish.heroreactions.common;

import gr8pefish.heroreactions.minecraft.config.ConfigHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

/** Any information that is overlapping with both Minecraft and Hero data. (Should be minimal) */
public class Common {

    /** Logger.*/
    public static final Logger LOGGER = LogManager.getLogger("HeroReactions");
    /** Path for the login files to be stored */
    public static final Path LOGIN_PATH_FROM_CONFIG = Paths.get(ConfigHandler.authConfigSettings.loginFilePath);

}
