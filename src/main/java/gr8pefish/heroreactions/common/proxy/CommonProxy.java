package gr8pefish.heroreactions.common.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        //No-op
    }

    @Override
    public void init(FMLInitializationEvent event) {
        //No-op
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        //No-op
    }

    @Override
    public EntityPlayer getClientPlayer(){
        return null; //nothing on server
    }

    @Override
    public World getClientWorld() {
        return null; //Nothing on server
    }

}
