package gr8pefish.heroreactions.hero.network.http;

import gr8pefish.heroreactions.hero.data.FileHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

public class HttpClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject message) {
        if (message instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) message;

            System.err.println("STATUS: " + response.status());
            System.err.println("VERSION: " + response.protocolVersion());
            System.err.println();

            boolean json = false;

            if (!response.headers().isEmpty()) {
                for (CharSequence name: response.headers().names()) {
                    for (CharSequence value: response.headers().getAll(name)) {
                        if (value.toString().contains("Content-Type' = application/json;")) {
                            json = true;
                        }
                        System.err.println("HEADER: " + name + " = " + value);
                    }
                }
                System.err.println();
            }

            if (HttpUtil.isTransferEncodingChunked(response)) {
                System.err.println("CHUNKED CONTENT {");
            } else {
                System.err.println("CONTENT {");
            }
        }
        if (message instanceof HttpContent) {
            HttpContent content = (HttpContent) message;

            String msg = content.content().toString(CharsetUtil.UTF_8);
            String token;
            //if msg contains "token", get token
            if (msg.contains("token")) {
                token = msg.substring(10, msg.length() - 2); //cut away beginning, cut out bracket and quotation at end
                FileHelper.storeToken(token); //store token
            }
            System.err.print(content.content().toString(CharsetUtil.UTF_8));
            System.err.flush();

            if (content instanceof LastHttpContent) {
                System.err.println("} END OF CONTENT");
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

