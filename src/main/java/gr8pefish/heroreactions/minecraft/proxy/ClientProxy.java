package gr8pefish.heroreactions.minecraft.proxy;

import gr8pefish.heroreactions.hero.network.websocket.WebSocketClient;
import gr8pefish.heroreactions.minecraft.client.ClientEventHandler;
import gr8pefish.heroreactions.minecraft.config.ConfigHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        ClientRegistry.registerKeyBinding(ClientEventHandler.KEY_TOGGLE_OVERLAY);
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        if (ConfigHandler.generalConfigSettings.enableMod) {
            WebSocketClient.establishConnection();
        }

    }

}
