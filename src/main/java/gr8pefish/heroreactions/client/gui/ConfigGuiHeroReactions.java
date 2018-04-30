package gr8pefish.heroreactions.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

import static gr8pefish.heroreactions.api.HeroReactionsInfo.MODID;
import static gr8pefish.heroreactions.api.HeroReactionsInfo.MOD_NAME;

public class ConfigGuiHeroReactions extends GuiConfig {

    public ConfigGuiHeroReactions(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(parentScreen), MODID, false, false, MOD_NAME);
    }

    private static List<IConfigElement> getConfigElements(GuiScreen parentScreen) {
        List<IConfigElement> list = new ArrayList<>();

        //TODO
//        for (String category : ConfigHandler.categories)
//            list.add(new ConfigElement(ConfigHandler.config.getCategory(category)));

        return list;
    }
}
