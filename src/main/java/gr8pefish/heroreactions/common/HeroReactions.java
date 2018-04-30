package gr8pefish.heroreactions.common;


import gr8pefish.heroreactions.common.config.ConfigHandler;
import gr8pefish.heroreactions.common.event.ServerEventHandler;
import gr8pefish.heroreactions.common.lib.ModInfo;
import gr8pefish.heroreactions.common.network.PacketHandler;
import gr8pefish.heroreactions.common.proxy.IProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static gr8pefish.heroreactions.api.HeroReactionsInfo.MODID;
import static gr8pefish.heroreactions.api.HeroReactionsInfo.MOD_NAME;

@Mod(modid = MODID, name = MOD_NAME, version = ModInfo.VERSION, guiFactory = ModInfo.GUI_FACTORY)
public class HeroReactions {

    //Proxies
    @SidedProxy(clientSide = ModInfo.CLIENT_PROXY, serverSide = ModInfo.COMMON_PROXY)
    public static IProxy proxy;

    //Mod Instance
    @Mod.Instance (MODID)
    public static HeroReactions instance;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        //config
//        ConfigHandler.init(event.getSuggestedConfigurationFile());

        //packets
        PacketHandler.init();

        //init renderers and client event handlers
        proxy.preInit(event);
    }


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        //register server events
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        //register config changed event
        MinecraftForge.EVENT_BUS.register(new ConfigHandler());

        proxy.init(event);
    }


    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        proxy.postInit(event);
    }
}
