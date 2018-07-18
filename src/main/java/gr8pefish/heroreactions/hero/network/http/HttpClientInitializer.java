package gr8pefish.heroreactions.hero.network.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.ssl.SslContext;

/**
 * Helper class to initialize the {@link HttpClient}'s data
 */
public class HttpClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public HttpClientInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();

        // Enable HTTPS if necessary.
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }

        // Add an inbuilt codec
        p.addLast(new HttpClientCodec());

        // Automatic content decompression.
        p.addLast(new HttpContentDecompressor());

        // If you don't want to handle HttpContents, uncomment this
//        p.addLast(new HttpObjectAggregator(1048576));

        // Our custom handler (for receiving messages)
        p.addLast(new HttpClientHandler());
    }
}
