package gr8pefish.heroreactions.minecraft;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.hero.test.MainTest;
import gr8pefish.heroreactions.minecraft.command.CommandHeroLogin;
import gr8pefish.heroreactions.minecraft.command.CommandHeroMessage;
import gr8pefish.heroreactions.minecraft.lib.ModInfo;
import gr8pefish.heroreactions.minecraft.proxy.CommonProxy;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static gr8pefish.heroreactions.minecraft.lib.ModInfo.MODID;
import static gr8pefish.heroreactions.minecraft.lib.ModInfo.MOD_NAME;

@Mod(modid = MODID, name = MOD_NAME, version = ModInfo.VERSION, acceptedMinecraftVersions = "[1.12,1.13)")
public class HeroReactions {

    //Proxies
    @SidedProxy(clientSide = ModInfo.CLIENT_PROXY, serverSide = ModInfo.COMMON_PROXY)
    public static CommonProxy proxy;

    //Mod Instance
    @Mod.Instance (MODID)
    public static HeroReactions instance;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        //init logger
        Common.LOGGER = event.getModLog();

        //init other
        proxy.preInit(event);
    }


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        //register client commands
        ClientCommandHandler.instance.registerCommand(new CommandHeroMessage());
        ClientCommandHandler.instance.registerCommand(new CommandHeroLogin());

        proxy.init(event);
    }


    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        proxy.postInit(event);

        //run test code
        //TODO: Disable in production
        MainTest.mainTest();
    }
}
