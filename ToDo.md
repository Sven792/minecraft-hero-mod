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
- [ ] Fix localizations
- [ ] Fix (in-game) config reloading
- [ ] Add error checking for connections - timeouts
- [ ] Add error checking for rendering
- [ ] Improve getCountToRender
- [ ] Refactor code with slide animation/redundant classes
- [ ] Documentation of features
- [ ] Add more config values where appropriate (e.g. glow intensity)
- [ ] Scaling of entire gui via config value
- [ ] Fix commands to give correct message on feedback
- [ ] Test clearOldBubbles method in main logic loop doing anything
- [ ] Add command to retry connection w/ optional ID
- [ ] Officially make it a client side only mod


#### Beta
- [ ] Test from blank install w/ config
- [ ] Profiling for performance
- [ ] Cleanup Trello board
- [ ] Create Curseforge page
- [ ] License


#### Post-Beta Refinement
- [ ] Switch to Apache commons for networking http://hc.apache.org/
- [ ] Code cleanup
- [ ] Make open source
- [ ] Left side screen render location
- [ ] Implement refined bubble lifespan from Hero's code
- [ ] Improve random location composite scoring algorithm
- [ ] Refine glow intensity/spawn box size to be more than just stageSize modifier
- [ ] Main sentiment moment - i.e. big emoji? - uses already written renderTopFeedback field in HeroData
- [ ] Other more advanced things like HeroData.activity that were obtained via rewriting complex JS code - keyword "Utilize" for ctr+f
- [ ] Profiling for performance


#### Maybe
- [ ] Jar in jar w/ Maven build (see laptop for details)?

