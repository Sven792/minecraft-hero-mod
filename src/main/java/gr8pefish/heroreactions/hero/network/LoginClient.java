package gr8pefish.heroreactions.hero.network;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.hero.data.FileHelper;
import gr8pefish.heroreactions.hero.network.http.HttpClient;
import gr8pefish.heroreactions.hero.network.websocket.WebSocketClient;

public class LoginClient {

    public static void login() {
        //try with account ID first
        String accountID = FileHelper.retreiveAccountID();
        //if account ID exists
        if (!accountID.equals(FileHelper.NONEXISTENT)) {
            //login with correct user
            Common.LOGGER.info("Logging in with user: "+accountID);
            WebSocketClient.establishConnection(accountID);
            return;
        }

        //try with token
        String token = FileHelper.retrieveToken();
        //if token exists
        if (!token.equals(FileHelper.NONEXISTENT)) {
            //get accountID from token
            accountID = getOwnerIdFromToken(token);
            //if valid token
            if (!accountID.equals(FileHelper.NONEXISTENT)) {
                //login with correct user
                Common.LOGGER.info("Logging in with user: "+accountID);
                WebSocketClient.establishConnection(accountID);
                return;
            } else {
              //invalid token
            }
        }

        //default action
        //login with default user
        Common.LOGGER.error("Logging in with DEFAULT user: " + WebSocketClient.DEFAULT_ACCOUNT_ID);
        WebSocketClient.establishConnection(WebSocketClient.DEFAULT_ACCOUNT_ID);
    }

    private static String getOwnerIdFromToken(String token) {
        //send REST request with JSON token
        //TODO
        try {
            HttpClient.main(token); //send request
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "todo";
    }

}
