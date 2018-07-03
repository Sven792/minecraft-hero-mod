package gr8pefish.heroreactions.hero.network;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.hero.data.FileHelper;
import gr8pefish.heroreactions.hero.network.http.HttpClient;
import gr8pefish.heroreactions.hero.network.websocket.WebSocketClient;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class LoginClient {

    public static String accountID;

    public static void login() {
        //try with account ID first
        Common.LOGGER.info("Checking for account ID...");
        accountID = FileHelper.retreiveAccountID();
        //if account ID exists
        if (!accountID.equals(FileHelper.NONEXISTENT)) {
            //login with correct user
            Common.LOGGER.info("Logging in with user: "+accountID);
            WebSocketClient.establishConnection(accountID);
            return;
        }

        //try with token
        String token = FileHelper.retrieveToken();
        Common.LOGGER.info("Checking for access token...");
        //if token exists
        if (!token.equals(FileHelper.NONEXISTENT)) {
            //get accountID from token
            accountID = getOwnerIdFromToken(token);
            //automatically logs in via async calls if possible
        }

        //default action
        //login with default user
        Common.LOGGER.error("Logging in with DEFAULT user: " + WebSocketClient.DEFAULT_ACCOUNT_ID);
        WebSocketClient.establishConnection(WebSocketClient.DEFAULT_ACCOUNT_ID);
    }

    public static String getOwnerIdFromToken(String token) {
        //send REST request with JSON token
        try {
            HttpClient.sendHttpMessage(HttpClient.httpMessageActions.GET_ACCOUNT_ID_FROM_ACCESS_TOKEN, token); //send request
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FileHelper.NONEXISTENT;
    }

}
