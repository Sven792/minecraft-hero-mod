package gr8pefish.heroreactions.network.hero.message;

import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.network.hero.json.HeroJSONMessageHelper;
import gr8pefish.heroreactions.network.hero.message.types.EnumHeroMessage;
import gr8pefish.heroreactions.network.hero.websocket.WebSocketClient;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class MessageHelper {

    //Check if a ping is received by parsing the data
    public static boolean gotPing(TextWebSocketFrame frame) {
        return HeroJSONMessageHelper.getMessageTypeFromJson(frame.text()).equals(EnumHeroMessage.PING);
    }

    //Send data to server over websocket
    public static void send(String jsonMessage) {
        HeroReactions.LOGGER.info("sending: "+jsonMessage);
        WebSocketFrame frame = new TextWebSocketFrame(jsonMessage);
        WebSocketClient.WEBSOCKET_CHANNEL.writeAndFlush(frame);
    }

    //test helper class (unused currently)
    private static void sendPing(){
        WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[]{8, 1, 8, 1}));
        WebSocketClient.WEBSOCKET_CHANNEL.writeAndFlush(frame);
    }

}
