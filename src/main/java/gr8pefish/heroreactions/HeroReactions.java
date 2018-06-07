package gr8pefish.heroreactions;

import gr8pefish.heroreactions.event.ServerEventHandler;
import gr8pefish.heroreactions.lib.ModInfo;
import gr8pefish.heroreactions.network.PacketHandler;
import gr8pefish.heroreactions.network.hero.message.CommandHeroMessage;
import gr8pefish.heroreactions.proxy.CommonProxy;
import gr8pefish.heroreactions.test.MainTest;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Logger;

import static gr8pefish.heroreactions.api.HeroReactionsInfo.MODID;
import static gr8pefish.heroreactions.api.HeroReactionsInfo.MOD_NAME;

@Mod(modid = MODID, name = MOD_NAME, version = ModInfo.VERSION, acceptedMinecraftVersions = "[1.12,1.13)")
public class HeroReactions {

    public static Logger LOGGER;
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
        LOGGER = event.getModLog();

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
