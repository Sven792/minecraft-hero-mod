package gr8pefish.heroreactions.minecraft.client;

import gr8pefish.heroreactions.hero.network.websocket.WebSocketClient;
import gr8pefish.heroreactions.minecraft.client.gui.GuiIngameOverlay;
import gr8pefish.heroreactions.minecraft.client.gui.GuiLocations;
import gr8pefish.heroreactions.minecraft.config.ConfigHandler;
import gr8pefish.heroreactions.minecraft.lib.ModInfo;
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

/**
 * Deals with client code, specifically the main logic loop of rendering the overlay and the keybinding handler to toggle the overlay.
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientEventHandler {

    //Main overlay rendering code

    //The overlay to render (one instance, with internal data changed depending)
    public static final GuiIngameOverlay overlay = new GuiIngameOverlay(Minecraft.getMinecraft());

    @SubscribeEvent
    public void onRenderOverlayGUI(RenderGameOverlayEvent.Text event) { //can do pre/post also //TODO: Ensure correct event
        if (ConfigHandler.generalConfigSettings.enableOverlay && WebSocketClient.isConnected()) {

            //Scale the rendering location data to fit current screen size
            GuiLocations.applyPositionScaling(overlay.getGuiLocation(), event.getResolution());

            //"reset" GL states (just in case)
            GlStateManager.enableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            //Render the overlay
            overlay.renderOverlay(event.getResolution());
        }
    }

    //Keybinding for toggling the overlay

    public static final KeyBinding KEY_TOGGLE_OVERLAY = new KeyBinding("key." + ModInfo.MODID + ".toggle", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_H, ModInfo.MOD_NAME);

    @SubscribeEvent
    public static void onKey(InputEvent.KeyInputEvent event) {
        if (KEY_TOGGLE_OVERLAY.isPressed())
            ConfigHandler.generalConfigSettings.enableOverlay = !ConfigHandler.generalConfigSettings.enableOverlay; //client side only is fine, no need to send info to the server
    }


}
