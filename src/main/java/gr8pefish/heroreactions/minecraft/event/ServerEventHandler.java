package gr8pefish.heroreactions.minecraft.event;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ServerEventHandler {

    /**
     * Example event
     *
     * @param event - tick event
     */
    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event){
        //No-op
    }

}
