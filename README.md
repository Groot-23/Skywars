# Skywars
Skywars Plugin for Minecraft 1.14.4

## Installation:
- Simply drag the jar into your plugin folder
- The plugin will automatically install the datapack for the loot_tables when used the first time
- **IMPORTANT** You have to reload the server after loading it the first time to update the loot_tables

## Setup and basic usage:
### 1. register a map
```
/swmaps register <map> <numPlayers> <weight>
```
- `numPlayers` is the number of players for this map
- maps are chosen randomly. The higher the `weight of map`, the higher is the probability that it will be chosen

### 2. edit a map
```
/swedit <map>
```
- this will teleport you to the map and sets you in creative mode
- this takes care of loading the world as a void world and will unload it after leaving
- It is **NOT** recommended to use multiverse to travel to those worlds because this may 
  keep it loaded or load it normally if you do not use multiverse properly
  
### 3. Add chests
```
/swchest <loot_table>
```
- This command gives you a skywars chest, which will be automatically refilled when a game is running

### 4. Add spawns

Use this command to add a new spawn to a map while editing it:
```
/swspawns add
```
You can set the visibility of the spawns to true/false by using:
```
/swspawns show [true|false]
```
- Simply make them visible and destroy the armorstand to remove a spawn
- **IMPORTANT** Set the visibility to false after editing or the players will see them

Use the normal minecraft command to set the world spawn
```
/setworldspawn
```

### 5. Apply changes
```
/swupdate
```
- This command has to be called after every change to apply the changes
- Note that this will **end all active games**.
- **IMPORTANT**: It is recommended to restart the server instead because
Minecraft may lock the world files, which can't be replaced because of that.

### 6. Join a game
```
/swjoin
```
This is everything you have to do :)

### 7. Leave
```
/swleave
```
This command teleports you back to your default world

## Settings

### 1. refillTime

The command `/swset` can be used to change some settings like the refill time of chests. Simply use
```
/swset refillTime <time>
```
*time* can be given in seconds and the format *minutes:seconds*. E.g. 145 and 2:25 are the same

### 2. dynamic and persistent game worlds

The worlds you actually play in are either created dynamically (if they are needed) or they are persistent. Persistent game worlds are always stored on disk while dynamic ones will be deleted (if Minecraft does not lock the world files. So there is no guarantee that they will actually be deleted) after the game is over. Therefore, persistent worlds have the advantage that they don't need to be copied and deleted when starting a new game. However they may need unused disk space. Let's say you have 10 persistent lobbies and only 2 of them are used currently. So there are 8 unused worlds which may need 10 MB each. 

You can set the amount of persistent worlds (which will always be stored on disk) with the command
```
/swset persistentLobbies <count>
```
The number of instances per map will be calculated with the *weight* you gave them with `/swmaps register`.

If you don't want the dynamic creation of worlds, you can disable it:
```
/swset allowDynamic false
```

## Permissions

All of them are in the format `skywars.<command>.<mode>` where `mode` refers to the first argument if the command supports different modes; e.g. `/swmaps` supports the modes `register`, `remove` and `list`. So one permission is `skywars.maps.register`. Note that `command` is the name of the command **WITHOUT sw** at the beginning.

If you use a command without having the needed permission the plugin will show you the permission you need. So you should be able to figure out the permissions ;)
