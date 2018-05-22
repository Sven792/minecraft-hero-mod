package gr8pefish.heroreactions.network.hero.websocket;

import gr8pefish.heroreactions.HeroReactions;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.annotation.Nonnull;
import java.net.URI;

/**
 * This WebSocket client attempts to establish a connection to a valid WebSocket server.
 * The {@link Channel} and {@link EventLoopGroup} are provided as public static fields for other classes to access the connection from.
 * Use {@link WebSocketClient#establishConnection()} to initialize the connection.
 */
public final class WebSocketClient {

    // Publicly accessible fields

    /** A {@link Channel} that is connected to the server. */
    @Nonnull
    public static Channel WEBSOCKET_CHANNEL;

    /** A {@link EventLoopGroup}, available so the connection can be fully closed. */
    @Nonnull
    public static EventLoopGroup GROUP;

    // Public methods

    /**
     * Establishes a WebSocket connection to a server.
     *
     * This is a wrapper method that has error handling. The internal class is {@link WebSocketClient#establishWebSocketConnection()}.
     */
    public static void establishConnection() {
        try {
            HeroReactions.LOGGER.info("Starting WebSocket connection...");
            WebSocketClient.establishWebSocketConnection();
            HeroReactions.LOGGER.info("WebSocket connection completed successfully!");
        } catch (Exception e) {
            HeroReactions.LOGGER.error("WebSocket connection failed!");
            e.printStackTrace();
        }
    }

    // Internal code

    /**
     * Establishes a WebSocket connection to a server.
     * Uses the {@link WebSocketClientHandler}.
     *
     * @throws Exception - any error (note it is logged)
     */
    private static void establishWebSocketConnection() throws Exception {

        //Hero -> wss://stream.outpostgames.com/ws/account/<account-id>
        //URL format example: ("url", "ws://127.0.0.1:8080/websocket");
        final String URL = System.getProperty("url", "ws://echo.websocket.org/"); //echo server used for testing

        //setup base data (ws at correct host and URL)
        URI uri = new URI(URL);
        String scheme = uri.getScheme() == null? "ws" : uri.getScheme();
        final String host = uri.getHost() == null? "127.0.0.1" : uri.getHost();

        //determine correct port
        final int port;
        if (uri.getPort() == -1) {
            if ("ws".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("wss".equalsIgnoreCase(scheme)) {
                port = 443;
            } else {
                port = -1;
            }
        } else {
            port = uri.getPort();
        }

        //confirm websocket connections only
        if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
            System.err.println("Only WS(S) is supported.");
            return;
        }

        //utilize wss for secure connection
        final boolean ssl = "wss".equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        //create event group to bind everything together
        EventLoopGroup group = new NioEventLoopGroup();

        //start the connection process (uses the
        try {
            // Connect with V13 (RFC 6455 aka HyBi-17).
            final WebSocketClientHandler handler =
                    new WebSocketClientHandler(
                            WebSocketClientHandshakerFactory.newHandshaker(
                                    uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders())); //.add for more custom headers

            //create a Bootstrap to easily establish the connection via helper methods
            Bootstrap b = new Bootstrap();
            //initialize all the data (through a channel)
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) { //add secure connection if possible
                                p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                            }
                            p.addLast(
                                    new HttpClientCodec(),
                                    new HttpObjectAggregator(8192),
                                    WebSocketClientCompressionHandler.INSTANCE,
                                    handler);
                        }
                    });

            //finally connect to the server via the established channel
            Channel ch = b.connect(uri.getHost(), port).sync().channel();
            handler.handshakeFuture().sync(); //waits and ensures the connection is okay

            //instantiate fields with valid data (for messaging via other classes)
            WEBSOCKET_CHANNEL = ch;
            GROUP = group;

        //catch any errors and propagate them to the main log
        } catch (Exception e) {
            HeroReactions.LOGGER.error("Exception caught during connection: "+e);
            group.shutdownGracefully();
        }
    }

}