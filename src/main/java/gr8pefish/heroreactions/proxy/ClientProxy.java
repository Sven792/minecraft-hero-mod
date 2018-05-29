package gr8pefish.heroreactions.proxy;

import gr8pefish.heroreactions.client.ClientEventHandler;
import gr8pefish.heroreactions.network.hero.websocket.WebSocketClient;
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

        WebSocketClient.establishConnection();
    }

}
