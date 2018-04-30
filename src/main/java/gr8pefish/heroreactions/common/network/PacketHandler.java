package gr8pefish.heroreactions.common.network;

import gr8pefish.heroreactions.common.lib.ModInfo;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class PacketHandler {

    //ToDo: Copy IronBackpacks' implementation

    public static final SimpleNetworkWrapper HANDLER = new SimpleNetworkWrapper(ModInfo.NETWORK_CHANNEL);

    public static void init() {
        int id = 0;
//        HANDLER.registerMessage(PacketClientGliding.Handler.class, PacketClientGliding.class, id++, Side.CLIENT);
    }

}

