<!-- name-start -->
# OpenLoader [![CurseForge Project](https://img.shields.io/curseforge/dt/354339?logo=curseforge&label=CurseForge&style=flat-square&labelColor=2D2D2D&color=555555)](https://www.curseforge.com/minecraft/mc-mods/open-loader) [![Modrinth Project](https://img.shields.io/modrinth/dt/KwWsINvD?logo=modrinth&label=Modrinth&style=flat-square&labelColor=2D2D2D&color=555555)](https://modrinth.com/mod/open-loader) [![Maven Project](https://img.shields.io/maven-metadata/v?style=flat-square&logoColor=D31A38&labelColor=2D2D2D&color=555555&label=Latest&logo=gradle&metadataUrl=https%3A%2F%2Fmaven.blamejared.com%2Fnet%2Fdarkhax%2Fopenloader%2Fopenloader-common-26.2%2Fmaven-metadata.xml)](https://maven.blamejared.com/net/darkhax/openloader)
<!-- name-end -->
<!-- description-start -->
An open source loader for custom resources and data! The documentation for this mod can be found [here](https://docs.darkhax.net/mods/open-loader).
<!-- description-end -->

## Pack Order

OpenLoader scans each directory recursively in case-insensitive filename order. Packs with names later in that order have higher default priority, so zero-padded numeric prefixes can make the intended order explicit, for example `00-base.zip`, `10-addon.zip`, and `90-overrides.zip`.

<!-- maven-start -->
## Maven Dependency

If you are using [Gradle](https://gradle.org) to manage your dependencies, add the following into your `build.gradle` file. Make sure to replace the version with the correct one. All versions can be viewed [here](https://maven.blamejared.com/net/darkhax/openloader).

```gradle
repositories {
    maven { 
        url 'https://maven.blamejared.com'
    }
}

dependencies {
    // NeoForge
    implementation group: 'net.darkhax.openloader', name: 'openloader-neoforge-26.2', version: '26.2.0.1'

    // Fabric & Quilt
    implementation group: 'net.darkhax.openloader', name: 'openloader-fabric-26.2', version: '26.2.0.1'

    // Common / MultiLoader / Vanilla
    compileOnly group: 'net.darkhax.openloader', name: 'openloader-common-26.2', version: '26.2.0.1'
}
```
<!-- maven-end -->

<!-- sponsor-start -->
## Sponsors

[![](https://assets.blamejared.com/nodecraft/darkhax.jpg)](https://nodecraft.com/r/darkhax)    
OpenLoader is sponsored by Nodecraft. Use code **[DARKHAX](https://nodecraft.com/r/darkhax)** for 30% of your first month of service!
<!-- sponsor-end -->
