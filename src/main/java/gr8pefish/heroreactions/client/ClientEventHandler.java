package gr8pefish.heroreactions.client;

import gr8pefish.heroreactions.api.HeroReactionsInfo;
import gr8pefish.heroreactions.client.gui.GuiIngameOverlay;
import gr8pefish.heroreactions.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientEventHandler {

    //=============================================================Rendering In-World for 1st Person Perspective==================================================

    //ToDo: Data caching locally as small optimization?
    //ToDo: Choose corner to render in via (in-game) config, then hand off to helper methods to do so in that location
    @SubscribeEvent
    public void onRenderOverlayGUI(RenderGameOverlayEvent.Text event) { //can do pre/post also
        if (ConfigHandler.generalConfigSettings.enableOverlay) {
            //render info
            GuiIngameOverlay overlay = new GuiIngameOverlay(Minecraft.getMinecraft());
            //"reset" GL states (just in case)
            GlStateManager.enableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            //do rendering
            overlay.renderGameOverlay(event.getPartialTicks());
        }
    }

    public static final KeyBinding KEY_TOGGLE_OVERLAY = new KeyBinding("key." + HeroReactionsInfo.MODID + ".toggle", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_H, HeroReactionsInfo.MOD_NAME);

    @SubscribeEvent
    public static void onKey(InputEvent.KeyInputEvent event) {
        if (KEY_TOGGLE_OVERLAY.isPressed())
            ConfigHandler.generalConfigSettings.enableOverlay = !ConfigHandler.generalConfigSettings.enableOverlay; //client side only is fine, no need to send info to the server
    }


}
