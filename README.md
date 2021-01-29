# [Open Loader](https://www.curseforge.com/minecraft/mc-mods/open-loader)
The Open Loader mod allows modpacks and servers to load [Data Packs](https://minecraft.gamepedia.com/Data_Pack) and [Resource Packs](https://minecraft.gamepedia.com/Resource_Pack) from a global location. You can load packs from zips or folders, just make sure they comply with the vanilla Minecraft file structure.

## Global Folders
This mod will load global packs from the `openloader` folder. This folder and it's sub folders will be automatically generated the first time you run the game with this mod. `.minecraft` refers to the install location of the game instance.
| Type                                                           | Location                          |
|----------------------------------------------------------------|-----------------------------------|
| [Data Pack](https://minecraft.gamepedia.com/Data_Pack)         | `.minecraft/openloader/data/`     |
| [Resource Pack](https://minecraft.gamepedia.com/Resource_Pack) | `.minecraft/openloader/resources` |

## FaQ

### My pack is not loading?
Make sure your pack has all the required files in the right space. If you're unsure you can compare against the [Example Data Pack](https://darkhax.net/assets/openloader/datapack-1.0.0.zip) and [Example Resource Pack](https://darkhax.net/assets/openloader/resourcepack-1.0.0.zip). You should also check your game log for any errors related to your pack. 

### How to set the priority or load order?
All packs are loaded with the highest level of priority, meaning they should be able to override files in vanilla and modded data packs. In some cases you may need further control over load order. With Resource Packs this can be done by naming them alphabetically. With datapacks this is done on a per-world basis and can be edited using the `/datapack` command.

### The mod isn't copying the pack to my world folder?
Yes, this mod does not copy files around. It adds in the openloader folders as new global sources and will load from there instead. This is far more performant than copying files over and greatly reduces the complexity of the mod.

## Maven Dependency
If you are using [Gradle](https://gradle.org) to manage your dependencies, add the following into your `build.gradle` file. Make sure to replace the version with the correct one. All versions can be viewed [here](https://maven.mcmoddev.com/net/darkhax/openloader/).
```groovy
repositories {

    maven { url 'https://maven.blamejared.com' }
}

dependencies {

    // Example: compile "net.darkhax.openloader:OpenLoader-1.16.4:8.0.2"
    compile "net.darkhax.openloader:OpenLoader-MCVERSION:PUT_BOOKSHELH_VERSION_HERE"
}
```

## Jar Signing

As of January 11th 2021 officially published builds will be signed. You can validate the integrity of these builds by comparing their signatures with the public fingerprints.

| Hash   | Fingerprint                                                        |
|--------|--------------------------------------------------------------------|
| MD5    | `12F89108EF8DCC223D6723275E87208F`                                 |
| SHA1   | `46D93AD2DC8ADED38A606D3C36A80CB33EFA69D1`                         |
| SHA256 | `EBC4B1678BF90CDBDC4F01B18E6164394C10850BA6C4C748F0FA95F2CB083AE5` |

## Sponsors
<img src="https://nodecraft.com/assets/images/logo-dark.png" width="384" height="90">    
This project is sponsored by Nodecraft. Use code [Darkhax](https://nodecraft.com/r/darkhax) for 30% off your first month of service!