# Session Timer

A client-side Fabric mod for Minecraft 1.21.8 and newer. It displays the time elapsed since joining the current server, making it easier to compare session age when investigating item pickup priority. The timer automatically restarts after every successful server connection and disappears after disconnecting.

Session Timer does not need to be installed on the server.

## Features

- Millisecond timer in `HH:MM:SS.mmm` format
- Automatic restart on server/world switch
- Draggable HUD position that adapts to resolution changes
- Scale from 0.5x to 3.0x
- Background opacity and RGB text color controls
- Persistent settings in `config/sessiontimer.json`
- In-game editor opened with `O` by default; the key can be changed in Controls

## Usage

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) and the [Fabric API](https://modrinth.com/mod/fabric-api) matching your Minecraft version.
2. Put the appropriate Session Timer JAR in your instance's `mods` folder.
3. Join a server. The timer starts automatically after the connection completes.
4. Press `O` to open the HUD editor. Drag the timer and adjust its scale, background opacity, and text color.
5. Change the editor key under **Options → Controls → Key Binds → Session Timer** if desired.

Settings are stored in `.minecraft/config/sessiontimer.json`.

## Supported versions

| Minecraft | Artifact |
| --- | --- |
| 1.21.8+ | `SessionTimer1.0.1-1.21.8+.jar` |

## Build

Install Java 21, then clone the repository and run:

```powershell
.\gradlew.bat build
```

The root build compiles and tests the mod. It writes the distributable JAR to `build/libs`:

- `SessionTimer1.0.1-1.21.8+.jar` for Minecraft 1.21.8 and newer, including the 1.21.11 keybinding/input API changes

Install the JAR together with Fabric Loader and the matching Fabric API release.

## Project structure

- `src/main` contains shared timer/configuration code and resources.
- `versions/modern` contains the HUD renderer and configures the Minecraft 1.21.8+ artifact.
- The root `build` task compiles, tests, remaps, and collects the artifact.

## Accuracy note

This mod measures local elapsed connection time. It does not read or modify the server's internal player/entity tick order, so treat it as a convenient comparison tool rather than proof of pickup priority on every server implementation.

## License

Session Timer is licensed under the [GNU General Public License v3.0](LICENSE).
