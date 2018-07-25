### Main
See Trello board [here](https://trello.com/b/XwNjGwyC).


##### Notes
* Way to set the interval of subscriptions? Or a 1-time ping? Way to trigger it once, whenever you want?


#### Pre-Beta
- [X] Split hero logic into packages to keep MC-only and hero-only stuff separate
- [X] Testing for logic not bound to minecraft (pure hero messaging)
- [X] Command to clear login data
- [X] Redo sprites to be more "minecraft-y"
- [X] Add correct app-id for Minecraft
- [X] Fix localizations
- [X] Fix (in-game) config reloading
- [X] Add error checking for connections - timeouts
- [X] Add error checking for rendering
- [X] Disable overlay showing with first login
- [X] Refactor code with redundant classes
- [X] Fix command autofill
- [ ] Fix commands to give correct message on feedback
- [X] Add command to retry connection w/ optional ID
- [ ] Improve getCountToRender
- [ ] Test clearOldBubbles method in main logic loop doing anything
- [ ] Officially make it a client side only mod
- [ ] Documentation of features


#### Beta
- [ ] Test from blank install w/ config - does login via authcode have too much delay so it sends message before WS connection valid?
- [ ] Profiling for performance
- [ ] Cleanup Trello board
- [ ] Create Curseforge page
- [ ] License


#### Post-Beta Refinement
- [ ] Switch to Apache commons for networking http://hc.apache.org/
- [ ] Code cleanup v2
- [ ] Make open source
- [ ] Left side screen render location
- [ ] Implement refined bubble lifespan from Hero's code
- [ ] Improve random location composite scoring algorithm
- [ ] Refine glow intensity/spawn box size to be more than just stageSize modifier
- [ ] Main sentiment moment - i.e. big emoji? - uses already written renderTopFeedback field in HeroData
- [ ] Other more advanced things like HeroData.activity that were obtained via rewriting complex JS code - keyword "Utilize" for ctr+f
- [ ] Profiling for performance v2
- [ ] Add more config values where appropriate (e.g. glow intensity)
- [ ] Scaling of entire gui via config value


#### Maybe
- [ ] Jar in jar w/ Maven build (see laptop for details)?
- [ ] Slide animation

