package gr8pefish.heroreactions.minecraft.command;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.minecraft.config.ConfigHandler;
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
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
                        String outputMessage = storeToken(params[1]);
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

    private String storeToken(String token) {
        //get token if it exists already
        if (ConfigHandler.authConfigSettings.keepToken) {
            Path dirPath = Paths.get(ConfigHandler.authConfigSettings.tokenFilePath);

            //if unchanged file from home dir, make subdir /minecraft/hero
            if (ConfigHandler.authConfigSettings.tokenFilePath.equalsIgnoreCase(System.getProperty("user.home"))) {
                //get path to hero directory
                String heroDir = File.separatorChar + "minecraft" + File.separatorChar + "hero";
                dirPath = Paths.get(ConfigHandler.authConfigSettings.tokenFilePath.concat(heroDir));
                //if no hero dir
                if (Files.notExists(dirPath)) {
                    //make filepath
                    try {
                        Files.createDirectories(dirPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            //create file
            String filename = File.separatorChar + "token.txt";
            Path filepath = Paths.get(dirPath.toString(), filename); //append file to dir path

            try {
                Files.createFile(filepath);
            } catch (FileAlreadyExistsException ex) {
                return "You already have a token stored, you're good to go!";
            } catch (IOException e) {
                e.printStackTrace();
            }

            //write token to file
            try {
                Files.write(filepath, Collections.singletonList(token));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Token stored successfully!";

        } else {
            return "Your config options are set to not store a token. Change that to enable this command.";
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
