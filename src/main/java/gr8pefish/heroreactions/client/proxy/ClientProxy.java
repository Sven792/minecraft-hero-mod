package gr8pefish.heroreactions.client.proxy;

import gr8pefish.heroreactions.client.event.ClientEventHandler;
import gr8pefish.heroreactions.common.proxy.IProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        //No-op
    }

    @Override
    public void init(FMLInitializationEvent event) {
        //register client events
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        //No-op
    }

    @Override
    public EntityPlayer getClientPlayer(){
        return Minecraft.getMinecraft().player;
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().world;
    }

}
