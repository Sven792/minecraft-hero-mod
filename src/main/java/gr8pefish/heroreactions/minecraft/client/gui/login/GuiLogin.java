package gr8pefish.heroreactions.minecraft.client.gui.login;

import gr8pefish.heroreactions.common.Common;
import gr8pefish.heroreactions.hero.data.FileHelper;
import gr8pefish.heroreactions.hero.network.LoginClient;
import gr8pefish.heroreactions.hero.network.http.HttpClient;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.net.URI;

/**
 * Screen for the login UI flow for entering credentials to Hero.tv.
 */
public class GuiLogin extends GuiScreen {

    private GuiScreen parentScreen;
    private GuiTextField tokenField;
    private GuiButton submitToken;
    private GuiButton heroRedirect;
    private GuiButton goBack;
    private GuiButton clearInfo;
    private String token;
    private boolean tokenSuccess;
    private boolean hasTokenAlready;

    public GuiLogin(GuiScreen parent) {
        this.parentScreen = parent;
        this.token = "";
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        if (this.tokenField != null) this.tokenField.updateCursorCounter();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {

        //always present
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        //check for preexisting info
        String accountID = FileHelper.retreiveAccountID();
        String token = FileHelper.retrieveToken();

        if (!token.equals(FileHelper.NONEXISTENT)) { //token exists
            hasTokenAlready = true;
            if (accountID.equals(FileHelper.NONEXISTENT)) { //no owner id, but has token - try to log in again
                LoginClient.login();
            }
            this.goBack = this.addButton(new GuiButton(0, this.width / 2 - 75, 115, 150, 20, I18n.format("gui.heroreactions.login.goBack")));
            this.clearInfo = this.addButton(new GuiButton(5, this.width / 2 - 75, 145, 150, 20, I18n.format("gui.heroreactions.login.clearInfo")));
        } else { //no info, full login flow
            //token text field present but disabled until you redirect
            this.tokenField = new GuiTextField(9, this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
            this.tokenField.setFocused(true);
            this.tokenField.setText(this.token);
            this.tokenField.setVisible(false);

            //submit token button likewise disabled for now
            this.submitToken = this.addButton(new GuiButton(3, this.width / 2 - 75, 115, 150, 20, I18n.format("gui.heroreactions.login.submit")));
            this.submitToken.enabled = false;
            this.submitToken.visible = false;

            //go back button also disabled
            this.goBack = this.addButton(new GuiButton(0, this.width / 2 - 75, 95, 150, 20, I18n.format("gui.heroreactions.login.goBack")));
            this.goBack.enabled = false;
            this.goBack.visible = false;

            //her redirect button enabled
            this.heroRedirect = this.addButton(new GuiButton(4, this.width / 2 - 75, 115, 150, 20, I18n.format("gui.heroreactions.login.heroButton")));
        }

        //help and cancel always present
        this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.heroreactions.login.cancel")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.heroreactions.login.help")));
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format("gui.heroreactions.login.title"), this.width / 2, 20, -1);

        //already logged in
        if (hasTokenAlready) {
            this.drawString(this.fontRenderer, I18n.format("gui.heroreactions.login.tokenAlreadyPresent"), this.width / 2 - 120, 94, -6250336);
        //first screen - instructions
        } else if (heroRedirect.enabled) {
            int yStart = 70;
            this.drawString(this.fontRenderer, I18n.format("gui.heroreactions.login.instructions1"), this.width / 2 - 120, yStart, -6250336);
            this.drawString(this.fontRenderer, I18n.format("gui.heroreactions.login.instructions2"), this.width / 2 - 120, yStart + 12, -6250336);
            this.drawString(this.fontRenderer, I18n.format("gui.heroreactions.login.instructions3"), this.width / 2 - 120, yStart + 24, -6250336);
        //second screen - token info
        } else if (tokenField.getVisible()){
            this.drawString(this.fontRenderer, I18n.format("gui.heroreactions.login.tokenLabel"), this.width / 2 - 100, 47, -6250336);
            this.drawString(this.fontRenderer, I18n.format("gui.heroreactions.login.tokenExample"), this.width / 2 - 100, 85, -6250336);
            this.tokenField.drawTextBox();
        //third screen - results
        } else if (goBack.enabled && !hasTokenAlready) {
            String displayText = tokenSuccess ? I18n.format("gui.heroreactions.login.successToken") : I18n.format("gui.heroreactions.login.failToken");
            this.drawString(this.fontRenderer, displayText, this.width / 2 - 85, 75, -6250336);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 0 || button.id == 1) { //cancel or go back
                this.mc.displayGuiScreen(this.parentScreen); //close GUI, show parent

            } else if (button.id == 2) { //help
                openURL("https://minecraft.curseforge.com/projects/hero-reactions");

            } else if (button.id == 3) { //submit token
                //store token
                try {
                    HttpClient.sendHttpMessage(HttpClient.httpMessageActions.GET_ACCESS_TOKEN_FROM_AUTHCODE, token);
                    tokenSuccess = true;
                } catch (Exception e) {
                    tokenSuccess = false;
                }

                //disable tokenField and submitToken button, and enable goBack button
                this.tokenField.setVisible(false);

                this.submitToken.enabled = false;
                this.submitToken.visible = false;
                this.buttonList.remove(submitToken);

                this.goBack.enabled = true;
                this.goBack.visible = true;

            } else if (button.id == 4) { //hero redirect link
                //open webpage
                openURL("https://www.hero.tv/connect/minecraft-hero");

                //enable token text field and submit token button, and remove hero redirect button
                this.tokenField.setVisible(true);

                this.submitToken.enabled = true;
                this.submitToken.visible = true;

                this.heroRedirect.enabled = false;
                this.heroRedirect.visible = false;
                this.buttonList.remove(heroRedirect);

            } else if (button.id == 5) { //clear info
                FileHelper.clearData();
                this.hasTokenAlready = false;
                this.initGui();
            }
        }
    }

    //Mostly copied from vanilla (private method)
    private void openURL(String url) {
        try {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop").invoke((Object)null);
            oclass.getMethod("browse", URI.class).invoke(object, new URI(url));
        } catch (Throwable throwable1) {
            Throwable throwable = throwable1.getCause();
            Common.LOGGER.error("Couldn't open link: {}", (Object)(throwable == null ? "<UNKNOWN>" : throwable.getMessage()));
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        if (this.tokenField != null) {
            //if focused, add key to field
            if (this.tokenField.isFocused() && this.tokenField.getVisible()) {
                this.tokenField.textboxKeyTyped(typedChar, keyCode);
                this.token = this.tokenField.getText();
            }
        }

        if (this.submitToken != null && this.tokenField != null) {
            // Enter
            if ((keyCode == 28 || keyCode == 156) && this.submitToken.enabled) {
                this.actionPerformed(this.submitToken);
            }

            // enable if non empty
            this.submitToken.enabled = !this.tokenField.getText().isEmpty();
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.tokenField != null) this.tokenField.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
