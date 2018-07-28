package gr8pefish.heroreactions.minecraft.proxy;

import gr8pefish.heroreactions.hero.network.LoginClient;
import gr8pefish.heroreactions.minecraft.client.ClientEventHandler;
import gr8pefish.heroreactions.minecraft.command.CommandHeroLogin;
import gr8pefish.heroreactions.minecraft.command.CommandHeroMessage;
import gr8pefish.heroreactions.minecraft.config.ConfigHandler;
import net.minecraftforge.client.ClientCommandHandler;
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

        //register client commands
        ClientCommandHandler.instance.registerCommand(new CommandHeroMessage());
        ClientCommandHandler.instance.registerCommand(new CommandHeroLogin());

        LoginClient.login();

    }

}
