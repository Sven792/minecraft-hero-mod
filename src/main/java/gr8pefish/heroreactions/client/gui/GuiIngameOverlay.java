package gr8pefish.heroreactions.client.gui;

import net.minecraft.client.Minecraft;

public class GuiIngameOverlay {

    private final Minecraft mc;
    private int viewCount;
    private GuiReactions reactions;

    public GuiIngameOverlay(Minecraft minecraft) {
        //client minecraft instance (for rendering)
        this.mc = minecraft;
        //view count
        this.viewCount = 0;
        //reactions
        this.reactions = new GuiReactions(minecraft);
    }



}
