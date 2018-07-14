package gr8pefish.heroreactions.hero.network.websocket;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.hero.network.message.MessageHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
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
 * Use {@link WebSocketClient#establishConnection)} to initialize the connection.
 */
public final class WebSocketClient {

    // Fields

    /** A {@link Channel} that is connected to the server. */
    @Nonnull
    private static Channel WEBSOCKET_CHANNEL;

    /** A {@link EventLoopGroup}, available so the connection can be fully closed. */
    @Nonnull
    private static EventLoopGroup GROUP;

    /** A owner-id to connect to. */
    public static final String DEFAULT_ACCOUNT_ID = "a20e92f3-e27e-4ac0-a1f3-0cd81a18850c"; //default one (random)

    // Public helper methods

    /**
     * Establishes a WebSocket connection to a server.
     *
     * This is a wrapper method that has error handling. The internal class is {@link WebSocketClient#establishWebSocketConnection(String)}.
     */
    public static void establishConnection(String accountID) {
        try {
            Common.LOGGER.info("Starting WebSocket connection...");
            WebSocketClient.establishWebSocketConnection(accountID);
            Common.LOGGER.info("WebSocket connection completed successfully!");
        } catch (Exception e) {
            Common.LOGGER.error("WebSocket connection failed!");
            e.printStackTrace();
        }
    }

    /**
     * Safely checks if the connection has been established.
     *
     * @return - boolean
     */
    public static boolean isConnected() {
        return (WEBSOCKET_CHANNEL != null && WEBSOCKET_CHANNEL.isOpen());
    }

    /**
     * Send a message (if possible)
     *
     * @param message - the message to send
     */
    public static void sendMessage(Object message) {
        if (WEBSOCKET_CHANNEL != null && WEBSOCKET_CHANNEL.isOpen()) {
            WEBSOCKET_CHANNEL.writeAndFlush(message);
        }
    }

    /**
     * Shuts down the connection fully
     *
     * @throws InterruptedException - thread interruption
     */
    public static void closeConnection() throws InterruptedException {
        //close connection
        sendMessage(new CloseWebSocketFrame());
        if (isConnected()) WEBSOCKET_CHANNEL.closeFuture().sync();
        //shutdown group
        if (GROUP != null) GROUP.shutdownGracefully();
    }

    // Internal code

    /**
     * Establishes a WebSocket connection to a server.
     * Uses the {@link WebSocketClientHandler}.
     *
     * @throws Exception - any error (note it is logged)
     * @param accountID - the account to connect with
     */
    private static void establishWebSocketConnection(String accountID) throws Exception {


        //URL format example: ("url", "ws://127.0.0.1:8080/websocket");
        //final String URL = System.getProperty("url", "ws://echo.websocket.org/"); //echo server used for testing

        //Hero -> wss://stream.outpostgames.com/ws/account/<account-id>
        final String URL = System.getProperty("url", "wss://stream.outpostgames.com/ws/account/"+accountID);

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
        final SslContext sslContext;
        if (ssl) {
            sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslContext = null;
        }

        //create event group to bind everything together
        EventLoopGroup group = new NioEventLoopGroup();

        //start the connection process (delegates to the WebSocketClientHandler for handling channel traffic)
        try {
            // Connect with V13 (RFC 6455 aka HyBi-17).
            final WebSocketClientHandler handler =
                    new WebSocketClientHandler(
                            WebSocketClientHandshakerFactory.newHandshaker(
                                    uri, WebSocketVersion.V13, null, false,
                                    new DefaultHttpHeaders()
                                            .add("app-id", "2"))); //adds minecraft app id (required) - 2 is random TODO: Correct app id for minecraft

            //create a Bootstrap to easily establish the connection via helper methods
            Bootstrap bootstrap = new Bootstrap();
            //initialize all the data (through a channel)
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            if (sslContext != null) { //add secure connection if possible
                                pipeline.addLast(sslContext.newHandler(socketChannel.alloc(), host, port));
                            }
                            pipeline.addLast(
                                    new HttpClientCodec(),
                                    new HttpObjectAggregator(8192),
                                    WebSocketClientCompressionHandler.INSTANCE,
                                    handler);
                        }
                    });

            //finally connect to the server via the established channel
            Channel channel = bootstrap.connect(uri.getHost(), port).sync().channel();
            handler.handshakeFuture().sync(); //waits and ensures the connection is okay

            //instantiate fields with valid data (for messaging via other classes)
            WEBSOCKET_CHANNEL = channel;
            GROUP = group;

            //Subscribe to all relevant events
            MessageHelper.subscribeAll();

        //catch any errors and propagate them to the main log
        } catch (Exception e) {
            Common.LOGGER.error("Exception caught during connection: "+e);
            group.shutdownGracefully();
        }
    }

}