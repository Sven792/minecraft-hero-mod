package gr8pefish.heroreactions.hero.network;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.hero.network.websocket.WebSocketClient;
import gr8pefish.heroreactions.minecraft.command.CommandHeroLogin;

public class LoginClient {

    public static void login() {
        //get token
        String token = CommandHeroLogin.getToken();
        //if got valid token
        if (!token.equals("NO TOKEN")) {
            //use token to get owner-id
            String ownerID = getOwnerIdFromToken();
            //login with correct user
            Common.LOGGER.info("Logging in with user: "+ownerID);
            WebSocketClient.establishConnection(ownerID);
        }
        //no valid token found
        else {
            //login with default user
            Common.LOGGER.error("Logging in with DEFAULT user: " + WebSocketClient.DEFAULT_ACCOUNT_ID);
            WebSocketClient.establishConnection(WebSocketClient.DEFAULT_ACCOUNT_ID);
        }
    }

    private static String getOwnerIdFromToken() {
        //send REST request with JSON token
        //TODO
        return "todo";
    }

}
