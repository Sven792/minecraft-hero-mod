package gr8pefish.heroreactions.hero.network.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;

import java.net.URI;

import static io.netty.buffer.Unpooled.wrappedBuffer;

/**
 * A simple HTTP client that prints out the content of the HTTP response to
 * {@link System#out} to test the server.
 */
public final class HttpClient {

    //auth code
    //access token
    //account

    //ws -> account
    //account -> access token
    //store: access token, account info

    public static void sendHttpMessage(httpMessageActions messageAction, String authCodeOrAccessToken) throws Exception {
        URI uri = new URI(System.getProperty("url", messageAction.url));
        String scheme = uri.getScheme() == null? "http" : uri.getScheme();
        String host = uri.getHost() == null? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            System.err.println("Only HTTP(S) is supported.");
            return;
        }

        // Configure SSL context if necessary.
        final boolean ssl = "https".equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HttpClientInitializer(sslCtx)); //initialize handler

            //timeout option
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);

            // Make the connection attempt.
            Channel ch = b.connect(host, port).sync().channel();

            //data
//            String token = "{\"code\":\"FSDM-BL7N-H5XX\"}";
            ByteBuf byteMessage = wrappedBuffer(messageAction.jsonData.concat(authCodeOrAccessToken+"\"}").getBytes(CharsetUtil.UTF_8));

            HttpRequest request;
            if (messageAction.equals(httpMessageActions.GET_ACCOUNT_ID_FROM_ACCESS_TOKEN)) {
                request = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1, messageAction.httpType, uri.getRawPath());
                request.headers().set("Authorization", "Token "+authCodeOrAccessToken);
            } else {
                request = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1, messageAction.httpType, uri.getRawPath(),
                        byteMessage);
                request.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
                request.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteMessage.readableBytes());
            }

            // Prepare the HTTP request with a body dependent on the message type.
            // Note: All CONTENT_* headers here are necessary!
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);


            System.out.println(request);
            System.out.println("Message: " + messageAction.jsonData.concat(authCodeOrAccessToken+"\"}"));

            // Send the HTTP request.
            ch.writeAndFlush(request);

            // Wait for the server to close the connection.
            ch.closeFuture().sync();
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }
    }


    public enum httpMessageActions {

        GET_ACCESS_TOKEN_FROM_AUTHCODE("https://api.outpostgames.com/api/access-token/auth-code", HttpMethod.POST, "{\"code\":\""),
        GET_ACCOUNT_ID_FROM_ACCESS_TOKEN("https://api.outpostgames.com/api/account", HttpMethod.GET, "{\"token\":\"");

        private final String url;
        private final HttpMethod httpType;
        private final String jsonData;

        httpMessageActions(String url, HttpMethod type, String jsonData) {
            this.url = url;
            this.httpType = type;
            this.jsonData = jsonData;
        }

        public String getUrl() {
            return url;
        }

        public HttpMethod getHttpType() {
            return httpType;
        }

        public String getJsonData() {
            return jsonData;
        }
    }

}

