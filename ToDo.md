### Main
See Trello board [here](https://trello.com/b/XwNjGwyC).


##### Notes
* Way to set the interval of subscriptions? Or a 1-time ping? Way to trigger it once, whenever you want?
Config toggle connection on/off
* Way to command to connect (not just disconnect) w/ optional ID
* Don't show elements if 0/no message update
* Might break terribly on first run with not enough connection error checking and config disabling mod by default

#### Pre-Beta
- [X] Split hero logic into helper library to keep MC-only and hero-only stuff separate? (Could do pseudo-library for now, as one big package).
- [X] Unit testing for logic not bound to minecraft (pure java messaging)
- [X] Command to clear login data
- [X] Redo sprites to be more "minecraft-y"
- [ ] Fix localizations
- [ ] Fix (in-game) config reloading
- [ ] Add error checking for connections - timeouts
- [ ] Add error checking for rendering

#### Beta
- [ ] Create Curseforge page


#### Post-Beta
- [ ] Switch to Apache commons for networking http://hc.apache.org/


#### Misc
- [ ] Profiling for performance
- [ ] License

#### Maybe
- [ ] Main sentiment moment - i.e. big emoji?
- [ ] Jar in jar w/ Maven build (see laptop for details)?

