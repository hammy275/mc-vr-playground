# MC VR Playground 2.0

Example mod for the [Vivecraft API](https://github.com/Vivecraft/VivecraftMod/tree/Multiloader-1.21.4/common/src/main/java/org/vivecraft/api).

This mod contains several examples of using the Vivecraft APi in a mod. Feel free to be inspired from and/or use the
code in this repository to make your own mods and features with Vivecraft!

## Structure

### Architectury

MC VR Playground is a mod built off of [Architectury](https://docs.architectury.dev/start), allowing for a mod for 
multiple modloaders to be built from one codebase. It uses
[Architectury API](https://modrinth.com/mod/architectury-api) to ease mod creation across modloaders.

In relevance to MC VR Playground, all the VR-specific code is found in the `common` folder within this repository, which
contains the mod code that is used across modloaders.

### Organization

Unlike most mods, this mod has subpackages located within `common/src/main/java/com/hammy275/mcvrplayground` organized
by feature. For example, the rocket hands code is found in the `com.hammy275.mcvrplayground.rocket_hands` package.

Code shared between mutliple features is found in the `com.hammy275.mcvrplayground.shared` package.

## Using this Repository

One should primarily be looking within `common/src/main/java/com/hammy275/mcvrplayground`. From here, any feature you
find interesting can be looked at, and its code browsed. The code is heavily commented to hopefully maximize ones
ability to understand it.

Additionally, this README also contains a small description of each feature MC VR Playground has along with the
underlying features it uses from the Vivecraft API (see below). If there's a feature you're particularly interested in
learning about, this is a great way to see how it's used in practice!

## MC VR Playground Features

### [Data Printer](https://github.com/hammy275/mc-vr-playground/tree/1.21.4-vivecraftapi/common/src/main/java/com/hammy275/mcvrplayground/data_printer)

Simply sends the different versions of pose data from Vivecraft concerning the VR hardware's position and rotation to
chat. This mainly utilizes methods in `VRClientAPI` concerned with getting `VRPose`s.

## [Debug Info](https://github.com/hammy275/mc-vr-playground/tree/1.21.4-vivecraftapi/common/src/main/java/com/hammy275/mcvrplayground/debug_info_item)

Simply sends a lot of data concerning the current state of a player in VR to the chat. Sends both client-available and
server-available data. Mainly utilizes methods that do NOT retrieve `VRPose`s in `VRClientAPI` and the data available
from the player's pose on the server in `VRAPI`.

## [Energy Ball](https://github.com/hammy275/mc-vr-playground/tree/1.21.4-vivecraftapi/common/src/main/java/com/hammy275/mcvrplayground/energy_ball)

Utilizes the `VRPoseHistory` available from `VRClientAPI#getHistoricalVRPoses()` for creating an energy ball that can be
charged via motion, is positioned in a less jitter-y manner, and is shot via motion.

## [History Visualizer](https://github.com/hammy275/mc-vr-playground/tree/1.21.4-vivecraftapi/common/src/main/java/com/hammy275/mcvrplayground/history_visualizer)

Utilizes the `VRPoseHistory` available from `VRClientAPI#getHistoricalVRPoses()` to visualize the history of individual
body parts for the local player. 

## [Keyboardinator](https://github.com/hammy275/mc-vr-playground/tree/1.21.4-vivecraftapi/common/src/main/java/com/hammy275/mcvrplayground/keyboardinator)

Utilizes `VRClientAPI#setKeyboardState()` to open the in-game keyboard provided by Vivecraft.

## [Magic Missile](https://github.com/hammy275/mc-vr-playground/tree/1.21.4-vivecraftapi/common/src/main/java/com/hammy275/mcvrplayground/magic_missile)

Utilizes pose data available on the server from `VRAPI` to allow a VR user to control the movement and rotation of
a projectile.

## [Other VR Player Visualizer](https://github.com/hammy275/mc-vr-playground/tree/1.21.4-vivecraftapi/common/src/main/java/com/hammy275/mcvrplayground/other_player_vr_visualizer)

Utilizes pose data available on the server from `VRAPI` to visualize the pose of a VR user via particles.

## [Rainbow Trail](https://github.com/hammy275/mc-vr-playground/tree/1.21.4-vivecraftapi/common/src/main/java/com/hammy275/mcvrplayground/rainbow_trail)

Utilizes the `VRPoseHistory` available from `VRAPI#getHistoricalVRPoses()` to visualize the position of a player from
a few ticks ago.

## [Shield Look](https://github.com/hammy275/mc-vr-playground/tree/1.21.4-vivecraftapi/common/src/main/java/com/hammy275/mcvrplayground/shield_look)

Utilizes an `ItemInUseTracker` to cause a held shield to be used when pointed in a similar direction to where the player
is looking.