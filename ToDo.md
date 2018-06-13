### Main
See Trello board [here](https://trello.com/b/XwNjGwyC).

#### Misc
- [ ] License

##### Notes
* Way to set the interval of subscriptions? Or a 1-time ping? Way to trigger it once, whenever you want?
Config toggle connection on/off
* Way to command to connect (not just disconnect) w/ optional ID
* Localizations/resources correct?
* Don't show elements if 0/no message update
* Fix resources/in-game config not showing (and possibly not reloading/onChanged)
* Might break terribly on first run with not enough connection error checking and config disabling mod by default
- [X] Split hero logic into helper library to keep MC-only and hero-only stuff separate? (Could do pseudo-library for now, as one big package).
- [X] Unit testing for logic not bound to minecraft (pure java messaging)
