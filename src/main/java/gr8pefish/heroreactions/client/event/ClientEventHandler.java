package gr8pefish.heroreactions.client.event;

import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandler extends Gui {

    //=============================================================Rendering In-World for 1st Person Perspective==================================================

    /**
     * For rendering as a perspective projection in-world
     */
    @SubscribeEvent
    public void onRenderOverlay(RenderWorldLastEvent event){
       //ToDo: Determine first person vs overlay
    }


}
