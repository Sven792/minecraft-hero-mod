package gr8pefish.heroreactions.minecraft.command;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.hero.data.UserData;
import gr8pefish.heroreactions.hero.network.json.variants.SubscribeJsonMessage;
import gr8pefish.heroreactions.hero.network.message.MessageHelper;
import gr8pefish.heroreactions.hero.network.websocket.WebSocketClient;
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

/**
 * Command to test the {@link WebSocketClient} connection in-game easily.
 *
 * Format: `hero send [ping/close/{message}]`
 * Where:
 *  ping - sends a ping to the server
 *  close - closes the connection
 *  {message} - sends a text message to the server containing whatever {message} is
 *
 *  TODO: Localize messages
 */
public class CommandHeroMessage extends CommandBase {

    // The strings used for the command, all in one place
    private final String HERO = "heromsg";
    private final String SEND = "[send|sub]";
    private final String MESSAGE = "[ping|open|close|message|subscriptionType|help]";
    private final String OTHER = "[accountID]";
    private final String MESSAGE_COMMAND = "/"+HERO+" "+SEND+" "+MESSAGE+" "+OTHER;

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
        return 1; //Has to be op-ed
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        if (params.length > 0 && params.length <= 3) {
            //Send message
            if (params[0].equalsIgnoreCase("send")) {
                if (params.length == 2) { //message supplied (contained in params[1])
                    try {
                        //check if connection is open first
                        if (!WebSocketClient.isConnected()) { //closed channel, can't do anything
                            sender.sendMessage(new TextComponentString("Can't send message, connection not open!"));
                            Common.LOGGER.warn("Couldn't process command to send a message (closed channel)!");
                        //open - open connection
                        if ("open".equals(params[1].toLowerCase())) {
                            String accountID = UserData.ACCOUNT_ID.retrieve();
                            sender.sendMessage(new TextComponentString("Opening connection to account: "+accountID));
                            WebSocketClient.establishConnection(accountID);
                        }
                        //close - close connection
                        } else if ("close".equals(params[1].toLowerCase())) {
                            sender.sendMessage(new TextComponentString("Closing connection."));
                            WebSocketClient.closeConnection();
                        //ping - send ping
                        } else if ("ping".equals(params[1].toLowerCase())) {
                            sender.sendMessage(new TextComponentString("Sending ping message."));
                            MessageHelper.sendPing();
                        //pong - send pong
                        } else if ("pong".equals(params[1].toLowerCase())) {
                            sender.sendMessage(new TextComponentString("Sending pong message."));
                            //MessageHelper.sendPong(new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[]{8, 1, 8, 1}))); //causes loop, no need to send a pong from client realistically
                        //message - send text (whatever was contained in the message)
                        } else {
                            sender.sendMessage(new TextComponentString("Sending text message."));
                            MessageHelper.sendText(params[1]);
                        }
                    } catch (Exception e) {
                        sender.sendMessage(new TextComponentString("Invalid message!"));
                        Common.LOGGER.warn("Couldn't process command to send a message!");
                    }
                } else if (params.length  == 3) {
                    //open connection with given ID
                    if ("open".equals(params[1].toLowerCase())) {
                        sender.sendMessage(new TextComponentString("Opening connection to account: "+params[2]));
                        WebSocketClient.establishConnection(params[2].toLowerCase());
                    }
                }
            //Subscribe message
            } else if (params[0].equalsIgnoreCase("sub")) {
                if (params.length == 2) { //message supplied (contained in params[1])
                    try {
                        //check if connection is open first
                        if (!WebSocketClient.isConnected()) { //closed channel, can't do anything
                            sender.sendMessage(new TextComponentString("Can't send message, connection not open!"));
                            Common.LOGGER.warn("Couldn't process command to send a message (closed channel)!");
                        }
                        //loop through subscription options
                        for (SubscribeJsonMessage.SubscribeTopics topic : SubscribeJsonMessage.SubscribeTopics.values()) {
                            if (topic.stringRepresentation.equalsIgnoreCase(params[1])) {
                                sender.sendMessage(new TextComponentString("Sending subscribe to " + topic + " message."));
                                MessageHelper.subscribeToEvent(topic);
                                return;
                            }
                        }

                        sender.sendMessage(new TextComponentString("Invalid option. Try one of: "));
                        for (SubscribeJsonMessage.SubscribeTopics topic : SubscribeJsonMessage.SubscribeTopics.values()) {
                            sender.sendMessage(new TextComponentString(topic.stringRepresentation));
                        }

                    } catch (Exception e) {
                        sender.sendMessage(new TextComponentString("Invalid message!"));
                        Common.LOGGER.warn("Couldn't process command to send a message!");
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
            tabCompletion.addAll(getListOfStringsMatchingLastWord(args, "send", "sub"));
        else if (args.length <= 2) //match name
            tabCompletion.addAll(getListOfStringsMatchingLastWord(args, "ping", "open", "close", "textMessage", "subscriptionType", "help"));
        else
            tabCompletion.addAll(getListOfStringsMatchingLastWord(args, "accountID"));
        return tabCompletion;
    }

}
