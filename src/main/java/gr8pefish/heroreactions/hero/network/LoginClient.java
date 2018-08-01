package gr8pefish.heroreactions.hero.network;

import com.google.common.base.Strings;
import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.hero.data.UserData;
import gr8pefish.heroreactions.hero.network.http.HttpClient;
import gr8pefish.heroreactions.hero.network.websocket.WebSocketClient;

/**
 * Main class to handle the core login flow.
 *
 * Process:
 * 1) Try to connect (via websocket) with account ID saved in a file
 * 2) Try to see if an access token is saved in a file
 *  2a) If so, get the account ID from the token and login that way (i.e. #1)
 * 3) If none of those work, do nothing - no way to log in
 */
public class LoginClient {

    //Stored in a field for other classes to access easily
    public static String accountID;

    public static void login() {
        //try with account ID first
        Common.LOGGER.debug("Checking for account ID...");
        accountID = UserData.ACCOUNT_ID.retrieve();
        //if account ID exists
        if (!Strings.isNullOrEmpty(accountID)) {
            //login with correct user
            Common.LOGGER.debug("Logging in with user with account ID: "+ accountID);
            WebSocketClient.establishConnection(accountID);
            return;
        }

        //try with token
        String token = UserData.TOKEN.retrieve();
        Common.LOGGER.debug("Checking for access token...");
        //if token exists
        if (!Strings.isNullOrEmpty(token)) {
            //get accountID from token
            accountID = getOwnerIdFromToken(token);
            //automatically logs in via async calls if possible
        }

        //Couldn't login, tell user
        Common.LOGGER.info("Couldn't log in, no information found. Run '/hero login help' to amend this.");
    }

    public static String getOwnerIdFromToken(String token) {
        //send REST request with JSON token
        try {
            HttpClient.sendHttpMessage(HttpClient.httpMessageActions.GET_ACCOUNT_ID_FROM_ACCESS_TOKEN, token); //send request
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
