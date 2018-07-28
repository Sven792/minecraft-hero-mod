### Main
See Trello board [here](https://trello.com/b/XwNjGwyC).


##### Notes
* Way to set the interval of subscriptions? Or a 1-time ping? Way to trigger it once, whenever you want?


#### Completed
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
- [X] Fix commands to give correct message on feedback
- [X] Add command to retry connection w/ optional ID
- [X] Improve getCountToRender
- [X] Officially make it a client side only mod
- [X] Test clearOldBubbles method in main logic loop doing anything
- [X] Documentation of features
- [X] Profiling for performance
- [X] Create Curseforge page
- [X] License
- [X] Test with server
- [X] Change config names to be more readable
- [X] Test from blank install w/ config
- [X] Create UI for login
- [X] Ensure fade out time is configurable (fade out amount, and bubble render time is)


#### Release

Logistics
- [ ] Fill out Curseforge page (Link to API docs - describe potential with mod and that it is OS)

Testing
- [ ] Code review with Ryan
- [ ] Remove debug log spam

Additions
- [ ] Add in Hero branding url every x seconds
- [?] Alter bubbling as per Wright's request


#### Potential future additions
- [ ] Further improve getCountToRender (specifically with small values)
- [ ] Switch to Apache commons for networking http://hc.apache.org/
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
- [ ] Jar in jar w/ Maven build (see laptop for details)?
- [ ] Slide animation

