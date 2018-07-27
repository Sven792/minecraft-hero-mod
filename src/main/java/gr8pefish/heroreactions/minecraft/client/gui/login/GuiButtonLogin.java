package gr8pefish.heroreactions.minecraft.client.gui.login;

import gr8pefish.heroreactions.minecraft.client.gui.overlay.GuiReactions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Simple button of a given texture
 * Used on main screen to link to login flow UI
 */
@SideOnly(Side.CLIENT)
public class GuiButtonLogin extends GuiButton {

    public GuiButtonLogin(int buttonID, int xPos, int yPos) {
        super(buttonID, xPos, yPos, 20, 20, "");
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(GuiReactions.REACTION_ICONS_TEX_PATH);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            //if hovering, change to blue hover texture
            boolean hovering = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = 16; //start at 16 pixels down
            if (hovering) i += this.height;

            this.drawTexturedModalRect(this.x, this.y, 0, i, 20, 20);
        }
    }
}