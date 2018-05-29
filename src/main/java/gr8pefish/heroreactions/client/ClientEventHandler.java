package gr8pefish.heroreactions.client;

import com.google.common.collect.ArrayListMultimap;
import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.api.HeroReactionsInfo;
import gr8pefish.heroreactions.config.ConfigHandler;
import gr8pefish.heroreactions.network.hero.HeroConnectionData;
import gr8pefish.heroreactions.network.hero.message.HeroMessages;
import gr8pefish.heroreactions.network.hero.message.MessageHelper;
import gr8pefish.heroreactions.network.hero.message.data.StreamData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientEventHandler {

    //=============================================================Rendering In-World for 1st Person Perspective==================================================

    public static final ResourceLocation WIDGETS_TEX_PATH = new ResourceLocation("textures/gui/widgets.png");

    //extend GuiScreen (see 1.7.10 src)


    @SubscribeEvent
    public void onRenderOverlayGUI(RenderGameOverlayEvent.Text event) { //can do pre/post also
        if (ConfigHandler.generalConfigSettings.enableOverlay) {

            Minecraft mc = Minecraft.getMinecraft();
            FontRenderer fontRenderer = mc.fontRenderer;

            ArrayList<String> msgArray = MessageHelper.getStreamData();
            int top = 2;
            for (String msg : msgArray) {
                if (msg == null || msg.isEmpty()) continue;
                Gui.drawRect(1, top - 1, 2 + fontRenderer.getStringWidth(msg) + 1, top + fontRenderer.FONT_HEIGHT - 1, -1873784752);
                fontRenderer.drawString(msg, 2, top, 14737632);
                top += fontRenderer.FONT_HEIGHT;
            }
        }
    }

    //reference
//    if (this.mc.getRenderViewEntity() instanceof EntityPlayer)
//    {
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        this.mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
//        EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
//        ItemStack itemstack = entityplayer.getHeldItemOffhand();
//        EnumHandSide enumhandside = entityplayer.getPrimaryHand().opposite();
//        int i = sr.getScaledWidth() / 2;
//        float f = this.zLevel;
//        int j = 182;
//        int k = 91;
//        this.zLevel = -90.0F;
//        this.drawTexturedModalRect(i - 91, sr.getScaledHeight() - 22, 0, 0, 182, 22);
//        this.drawTexturedModalRect(i - 91 - 1 + entityplayer.inventory.currentItem * 20, sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
//
//        if (!itemstack.isEmpty())
//        {
//            if (enumhandside == EnumHandSide.LEFT)
//            {
//                this.drawTexturedModalRect(i - 91 - 29, sr.getScaledHeight() - 23, 24, 22, 29, 24);
//            }
//            else
//            {
//                this.drawTexturedModalRect(i + 91, sr.getScaledHeight() - 23, 53, 22, 29, 24);
//            }
//        }
//
//        this.zLevel = f;
//        GlStateManager.enableRescaleNormal();
//        GlStateManager.enableBlend();
//        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//        RenderHelper.enableGUIStandardItemLighting();
//
//        for (int l = 0; l < 9; ++l)
//        {
//            int i1 = i - 90 + l * 20 + 2;
//            int j1 = sr.getScaledHeight() - 16 - 3;
//            this.renderHotbarItem(i1, j1, partialTicks, entityplayer, entityplayer.inventory.mainInventory.get(l));
//        }
//
//        if (!itemstack.isEmpty())
//        {
//            int l1 = sr.getScaledHeight() - 16 - 3;
//
//            if (enumhandside == EnumHandSide.LEFT)
//            {
//                this.renderHotbarItem(i - 91 - 26, l1, partialTicks, entityplayer, itemstack);
//            }
//            else
//            {
//                this.renderHotbarItem(i + 91 + 10, l1, partialTicks, entityplayer, itemstack);
//            }
//        }
//
//        if (this.mc.gameSettings.attackIndicator == 2)
//        {
//            float f1 = this.mc.player.getCooledAttackStrength(0.0F);
//
//            if (f1 < 1.0F)
//            {
//                int i2 = sr.getScaledHeight() - 20;
//                int j2 = i + 91 + 6;
//
//                if (enumhandside == EnumHandSide.RIGHT)
//                {
//                    j2 = i - 91 - 22;
//                }
//
//                this.mc.getTextureManager().bindTexture(Gui.ICONS);
//                int k1 = (int)(f1 * 19.0F);
//                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//                this.drawTexturedModalRect(j2, i2, 0, 94, 18, 18);
//                this.drawTexturedModalRect(j2, i2 + 18 - k1, 18, 112 - k1, 18, k1);
//            }
//        }
//
//        RenderHelper.disableStandardItemLighting();
//        GlStateManager.disableRescaleNormal();
//        GlStateManager.disableBlend();
//    }



    public static final KeyBinding KEY_TOGGLE_OVERLAY = new KeyBinding("key." + HeroReactionsInfo.MODID + ".toggle", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_H, HeroReactionsInfo.MOD_NAME);

    @SubscribeEvent
    public static void onKey(InputEvent.KeyInputEvent event) {
        if (KEY_TOGGLE_OVERLAY.isPressed())
            ConfigHandler.generalConfigSettings.enableOverlay = !ConfigHandler.generalConfigSettings.enableOverlay; //client side only is fine, no need to send info to the server
    }


}
