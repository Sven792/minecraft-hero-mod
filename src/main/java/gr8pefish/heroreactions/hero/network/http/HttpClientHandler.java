package gr8pefish.heroreactions.hero.network.http;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.hero.data.FileHelper;
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

public class HttpClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject message) {
        if (message instanceof HttpContent) {
            HttpContent content = (HttpContent) message;

            //message contents
            String msg = content.content().toString(CharsetUtil.UTF_8);

            //accountID
            String accountID = "";
            boolean useAccountID = false;

            //token
            String token = "";
            boolean useToken = false;

            //accountID
            if (msg.contains("minecraft-hero")) { //authorized for MC
                accountID = msg.substring(7, 7+36); //{"id":"cba1c1cb-1e41-4bb7-8020-72433c9f7da4"... <- example to show how it is parsed
                useAccountID = true;
                //login via websocket as well -> async at end
            }

            //if msg contains "token", get token
            if (msg.contains("token")) {
                token = msg.substring(10, msg.length() - 2); //cut away beginning, cut out bracket and quotation at end
                FileHelper.storeToken(token); //store token
                useToken = true;
                //login via websocket as well -> async at end
            }

            System.err.print(msg);

            if (content instanceof LastHttpContent) {

                //close connection
                ChannelFuture future = ctx.channel().close();

                //token
                final boolean finalUseToken = useToken;
                final String finalToken = token;
                //accountID
                final boolean finalUseAccountID = useAccountID;
                final String finalAccountID = accountID;

                //Async calls
                future.addListener((ChannelFutureListener) future1 -> {
                    if (finalUseToken) {
                        //get new accountID
                        LoginClient.getOwnerIdFromToken(finalToken);
                    }
                    if (finalUseAccountID) {
                        //login with accountID
                        if (!finalAccountID.equalsIgnoreCase(FileHelper.NONEXISTENT)) {
                            FileHelper.storeAccountID(finalAccountID);
                            Common.LOGGER.info("LOGIN with " + finalAccountID);
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

