package gr8pefish.heroreactions.minecraft.client;

import gr8pefish.heroreactions.hero.client.elements.Bubble;
import gr8pefish.heroreactions.hero.data.FeedbackTypes;
import gr8pefish.heroreactions.minecraft.api.HeroReactionsInfo;
import gr8pefish.heroreactions.minecraft.client.gui.GuiIngameOverlay;
import gr8pefish.heroreactions.minecraft.client.gui.GuiLocations;
import gr8pefish.heroreactions.minecraft.client.gui.GuiReactions;
import gr8pefish.heroreactions.minecraft.config.ConfigHandler;
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

    //The overlay to render (one instance, with internal data changed depending)
    public static final GuiIngameOverlay overlay = new GuiIngameOverlay(Minecraft.getMinecraft());

    private boolean addedBubble = false;

    //ToDo: Data caching locally as small optimization?
    @SubscribeEvent
    public void onRenderOverlayGUI(RenderGameOverlayEvent.Text event) { //can do pre/post also
        if (ConfigHandler.generalConfigSettings.enableOverlay) {

            //Scale the rendering location data to fit current screen size
            GuiLocations.applyPositionScaling(ConfigHandler.overlayConfigSettings.overlayPos.toUpperCase(), event.getResolution());

            if (!addedBubble) {
                overlay.getReactions().addTestBubble();
                addedBubble = true;
            }

            //"reset" GL states (just in case)
            GlStateManager.enableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            //Render the overlay
            overlay.renderOverlay(event.getResolution());
        }
    }

    public static final KeyBinding KEY_TOGGLE_OVERLAY = new KeyBinding("key." + HeroReactionsInfo.MODID + ".toggle", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_H, HeroReactionsInfo.MOD_NAME);

    @SubscribeEvent
    public static void onKey(InputEvent.KeyInputEvent event) {
        if (KEY_TOGGLE_OVERLAY.isPressed())
            ConfigHandler.generalConfigSettings.enableOverlay = !ConfigHandler.generalConfigSettings.enableOverlay; //client side only is fine, no need to send info to the server
    }


}
