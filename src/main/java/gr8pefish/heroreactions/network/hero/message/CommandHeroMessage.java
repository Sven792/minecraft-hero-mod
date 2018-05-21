package gr8pefish.heroreactions.network.hero.message;

import gr8pefish.heroreactions.HeroReactions;
import gr8pefish.heroreactions.network.hero.WebSocketClient;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

//Registered on the client, in the main init phase
public class CommandHeroMessage extends CommandBase {

    // The strings used in the command in one place
    private final String HERO = "hero";
    private final String SEND = "send";
    private final String MESSAGE = "[message]";
    private final String MESSAGE_COMMAND = "/"+HERO+" "+SEND+" "+MESSAGE;

    @Override
    public String getName() {
        return HERO;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return MESSAGE_COMMAND;
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 2; //Best guess, not sure on the perfect level
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        if (params.length > 0 && params.length <= 2) {
            if (params[0].equalsIgnoreCase(SEND)) { //send command
                if (params.length == 2) { //message supplied
                    try {
                        if (!WebSocketClient.WEBSOCKET_CHANNEL.isOpen()) { //closed channel, can'ty do anything
                            sender.sendMessage(new TextComponentString("Can't send message, connection not open!"));
                            HeroReactions.LOGGER.warn("Couldn't process command to send a message (closed channel)!");
                        } else if ("bye".equals(params[1].toLowerCase())) {
                            sender.sendMessage(new TextComponentString("Closing connection."));
                            //close connection
                            WebSocketClient.WEBSOCKET_CHANNEL.writeAndFlush(new CloseWebSocketFrame());
                            WebSocketClient.WEBSOCKET_CHANNEL.closeFuture().sync();
                            //shutdown group
                            WebSocketClient.GROUP.shutdownGracefully();
                        } else if ("ping".equals(params[1].toLowerCase())) {
                            sender.sendMessage(new TextComponentString("Sending ping message."));
                            WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[]{8, 1, 8, 1}));
                            WebSocketClient.WEBSOCKET_CHANNEL.writeAndFlush(frame);
                        } else {
                            sender.sendMessage(new TextComponentString("Sending text message."));
                            WebSocketFrame frame = new TextWebSocketFrame(params[1]);
                            WebSocketClient.WEBSOCKET_CHANNEL.writeAndFlush(frame);
                        }
                    } catch (Exception e) {
                        sender.sendMessage(new TextComponentString("Invalid message!"));
                        HeroReactions.LOGGER.warn("Couldn't process command to send a message!");
                    }
                }
            }
        } else {
            throw new CommandException(getUsage(sender));
        }
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        List<String> tabCompletion = new ArrayList<String>();
        if (args.length <= 1) //no name, match string
            tabCompletion.addAll(getListOfStringsMatchingLastWord(args, SEND));
        else //match name
            tabCompletion.addAll(getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()));
        return tabCompletion;
    }

}
