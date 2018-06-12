package gr8pefish.heroreactions.minecraft;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.hero.test.MainTest;
import gr8pefish.heroreactions.minecraft.event.ServerEventHandler;
import gr8pefish.heroreactions.minecraft.lib.ModInfo;
import gr8pefish.heroreactions.minecraft.network.PacketHandler;
import gr8pefish.heroreactions.minecraft.proxy.CommonProxy;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import static gr8pefish.heroreactions.minecraft.api.HeroReactionsInfo.MODID;
import static gr8pefish.heroreactions.minecraft.api.HeroReactionsInfo.MOD_NAME;

@Mod(modid = MODID, name = MOD_NAME, version = ModInfo.VERSION, acceptedMinecraftVersions = "[1.12,1.13)")
public class HeroReactions {

    public static final SimpleNetworkWrapper NETWORK = new SimpleNetworkWrapper(MODID);

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

        //init packets
        PacketHandler.init();

        //init other
        proxy.preInit(event);
    }


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        //register server events
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());

        //register client command
        ClientCommandHandler.instance.registerCommand(new CommandHeroMessage());

        proxy.init(event);
    }


    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        proxy.postInit(event);

        //run test code
        MainTest.mainTest();
    }
}
