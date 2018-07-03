package gr8pefish.heroreactions.hero.data;

import gr8pefish.heroreactions.common.Common;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public class FileHelper {

    /** Base dir */
    private static Path loginFilePath = Common.loginPathFromConfig;
    /** Token file dir */
    private static final String tokenName = File.separatorChar+"token.txt";
    private static Path tokenFilePath = Paths.get(loginFilePath.toString(), tokenName);
    /** Account ID file dir*/
    private static final String accountName = File.separatorChar+"accountID.txt";
    private static Path accountIDFilePath = Paths.get(loginFilePath.toString(), accountName);

    /** For files that don't exist/have data*/
    public static final String NONEXISTENT = "DOES NOT EXIST";

    public static String storeToken(String token) {

        //set location
        setFilepathLocations();

        //write token to file
        try {
            Files.write(tokenFilePath, Collections.singletonList(token));
            return "Token stored successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failure!";
        }
    }

    public static String retrieveToken() {
        setFilepathLocations();
        try {
            if (new File(tokenFilePath.toString()).length() == 0) return NONEXISTENT; //empty file
            byte[] bytes = Files.readAllBytes(tokenFilePath);
            return Arrays.toString(bytes).isEmpty() ? NONEXISTENT : Arrays.toString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return NONEXISTENT;
    }

    public static String storeAccountID(String accountID) {
        //set location
        setFilepathLocations();

        //write account id to file
        try {
            Files.write(accountIDFilePath, Collections.singletonList(accountID));
            return "Account ID stored successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failure!";
        }
    }

    public static String retreiveAccountID() {
        setFilepathLocations();
        try {
            if (new File(accountIDFilePath.toString()).length() == 0) return NONEXISTENT; //empty file
            byte[] bytes = Files.readAllBytes(accountIDFilePath);
            return Arrays.toString(bytes).isEmpty() ? NONEXISTENT : Arrays.toString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return NONEXISTENT;
    }

    private static void setFilepathLocations() {
        //if unchanged file from base app data dir, make subdir /minecraft/hero
        if (loginFilePath.toString().equalsIgnoreCase(System.getenv("APPDATA"))) {
            //get path to hero directory

            String heroDir = File.separatorChar + "minecraft" + File.separatorChar + "hero";
            Path dirPath = Paths.get(loginFilePath.toString(), heroDir);
            //if no minecraft/hero dir
            if (Files.notExists(dirPath)) {
                //make filepath
                try {
                    Files.createDirectories(dirPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            loginFilePath = dirPath;
        }

        //create files
        Path filepath;

        //token
        filepath = Paths.get(loginFilePath.toString(), tokenName); //append file to dir path
        tokenFilePath = filepath;

        try {
            Files.createFile(filepath);
        } catch (FileAlreadyExistsException ex) {
            Common.LOGGER.error("You already have a token stored, you're good to go!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //account ID
        filepath = Paths.get(loginFilePath.toString(), accountName); //append file to dir path
        accountIDFilePath = filepath;

        try {
            Files.createFile(filepath);
        } catch (FileAlreadyExistsException ex) {
            Common.LOGGER.error("You already have a account ID stored, you're good to go!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
