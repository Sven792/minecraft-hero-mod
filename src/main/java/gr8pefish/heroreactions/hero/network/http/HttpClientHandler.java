package gr8pefish.heroreactions.hero.network.http;

import com.google.common.base.Strings;
import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.hero.data.UserData;
import gr8pefish.heroreactions.hero.network.LoginClient;
import gr8pefish.heroreactions.hero.network.websocket.WebSocketClient;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

/**
 * Class to receive a message (instantiated from {@link HttpClient} and act on what is obtained
 */
public class HttpClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    /**
     * Called whenever a HTTP message is received on the client
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject message) {
        if (message instanceof HttpContent) {
            HttpContent content = (HttpContent) message;

            //message contents formatted
            String msg = content.content().toString(CharsetUtil.UTF_8);

            //accountID data
            String accountID = "";
            boolean useAccountID = false;

            //token data
            String token = "";
            boolean useToken = false;

            //accountID
            if (msg.contains("minecraft-hero")) { //authorized for MC
                accountID = msg.substring(7, 7+36); //{"id":"cba1c1cb-1e41-4bb7-8020-72433c9f7da4"... <- example: it would get the string of chars 'c...4'
                useAccountID = true;
                //login via websocket as well -> async at end
            }

            //if msg contains "token", get token
            if (msg.contains("token")) {
                token = msg.substring(10, msg.length() - 2); //cut away beginning, cut out bracket and quotation at end -> {"token": "12t63-hg2e7-yd9u3-ha4dg"} results in '1..g'
                if (!UserData.TOKEN.store(token)) //store token in file
                    Common.LOGGER.warn("Failed to store the token");
                useToken = true;
                //login via websocket as well -> async at end
            }

            //Debug printing
            Common.LOGGER.debug("Obtained message: "+msg);

            //End of message
            if (content instanceof LastHttpContent) {

                //close connection
                ChannelFuture future = ctx.channel().close();

                //token data
                final boolean finalUseToken = useToken;
                final String finalToken = token;
                //accountID data
                final boolean finalUseAccountID = useAccountID;
                final String finalAccountID = accountID;

                //Async calls - for after the message is obtained
                future.addListener((ChannelFutureListener) future1 -> {
                    //if useToken -> get the owner id from that via a new HTTP message
                    if (finalUseToken) {
                        //get new accountID
                        LoginClient.getOwnerIdFromToken(finalToken);
                    }
                    //if useAccountID -> log in with that data after storing it in a file
                    if (finalUseAccountID) {
                        //login with accountID
                        if (!Strings.isNullOrEmpty(finalAccountID)) {
                            //store in file
                            if (!UserData.ACCOUNT_ID.store(finalAccountID))
                                Common.LOGGER.warn("Failed to store the account ID");
                            //Debug printing
                            Common.LOGGER.debug("Logging in with account ID: " + finalAccountID);
                            //close old connection
                            WebSocketClient.closeConnection();
                            //start new one
                            WebSocketClient.establishConnection(finalAccountID);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

