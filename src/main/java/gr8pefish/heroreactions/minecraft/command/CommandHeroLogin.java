package gr8pefish.heroreactions.minecraft.command;

import gr8pefish.heroreactions.hero.data.FileHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

//Registered on the client, in the main init phase

/**
 * Command to log a player in
 *
 * Format: `hero login [token]`
 *
 * The default (no token) gives you the url, the token logs you in
 */
public class CommandHeroLogin extends CommandBase {

    // The strings used for the command, all in one place
    private final String HERO = "hero";
    private final String LOGIN = "login";
    private final String MESSAGE = "[help|token]";
    private final String MESSAGE_COMMAND = "/"+HERO+" "+LOGIN+" "+MESSAGE;

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
        return 1; //Anyone
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        if (params.length > 0 && params.length <= 2) {
            //Send message
            if (params[0].equalsIgnoreCase(LOGIN)) {

                //create help message with formatted link
                TextComponentString preLink = new TextComponentString("Login at ");
                TextComponentString link = new TextComponentString("hero.tv/minecraft");
                link.setStyle(link.getStyle().setColor(TextFormatting.BLUE).setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://hero.tv/minecraft")));
                TextComponentString postLink = new TextComponentString(", then paste the token you get here by running '/hero login [token_here]'. You only have to do this once, every time after it will remember you!"); //TODO: localize;
                TextComponentString helpMsg = (TextComponentString) preLink.appendSibling(link).appendSibling(postLink);

                if (params.length == 2) { //message supplied (contained in params[1])
                    if (params[1].equalsIgnoreCase("help")) {
                        sender.sendMessage(helpMsg);
                    } else {
                        //token login
                        String outputMessage = FileHelper.storeToken(params[1]);
                        sender.sendMessage(new TextComponentString(outputMessage));
                    }
                } else { //no message
                    sender.sendMessage(helpMsg);
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
            tabCompletion.addAll(getListOfStringsMatchingLastWord(args, "login"));
        else //match name
            tabCompletion.addAll(getListOfStringsMatchingLastWord(args, "help", "token"));
        return tabCompletion;
    }

}
