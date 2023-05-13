# HSPS Assistant
Hypixel Server Parkour speedrunning helper mod

## Development
 
If using Eclipse, run `gradlew genEclipseRuns` and `gradlew eclipse`. If using IntelliJ, `gradlew genIntellijRuns` should work but it's untested. Otherwise you can just test with `gradlew runClient` and build with `gradlew build`.

[DevAuth](https://github.com/DJtheRedstoner/DevAuth) is used to simplify testing. You will have a few first-time configuration steps for it:
 * If using Eclipse, add the DevAuth jar to /run/mods. Probably similar for IntelliJ.
 * When running for the first time, it will set up a .devauth/config.toml file in your user home folder. This needs configured with your auth info. See DevAuth page for more info.
 * If using a Microsoft account, there will be a message with the link to set up the auth. It tends to get buried with debug logging on, but it's at the info level (which appears green in my terminal) and is somewhat towards the end of the log. You'll want to keep MC open while you authenticate.