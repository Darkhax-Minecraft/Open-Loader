package net.darkhax.openloader.common.impl.unused;

import net.darkhax.openloader.common.impl.packs.PackContentType;
import net.darkhax.openloader.common.impl.packs.PackFileType;

import java.io.File;

public record PackContainer(File packFile, PackFileType fileType, PackContentType contentType) {

    public static PackContainer from(File packFile) {
        return new PackContainer(packFile, PackFileType.from(packFile), PackContentType.from(packFile.toPath()));
    }
}