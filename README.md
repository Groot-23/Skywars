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
- numPlayers is the number of players for this map
- maps are chosen randomly. The higher the weight of map, the higher is the probability that it will be chosen

### 2. edit a map
```
/swedit <map>
```
- this will teleport you to the map and sets you in creative mode
- you can only edit **registered** maps
- this takes care of loading the world as a void world and will unload it after leaving
- It is recommended **NOT** to use multiverse to travel to those worlds because this may 
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

### 5. Apply changes
```
/swupdate
```
- This command has to be called after every change to apply the changes
- Note that this will **end all active games**.

### 6. Join a game
```
/swjoin
```
This is everything you have to do :)

**NOT FINISHED YET**
