package org.sweatshop.tgz;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

public class TgzCreator {

    public static void create(Path tgzPath, Path rootPath) throws FileNotFoundException, IOException {
        try (   FileOutputStream fos = new FileOutputStream(tgzPath.toFile());
                GZIPOutputStream gos = new GZIPOutputStream(fos);
                TarArchiveOutputStream taos = new TarArchiveOutputStream(gos))
        {
            Files.walkFileTree(rootPath, new TgzFileVisitor(rootPath, taos));
        }
    }

}
