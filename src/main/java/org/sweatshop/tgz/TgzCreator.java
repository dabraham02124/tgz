package org.sweatshop.tgz;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

public class TgzCreator {

    /**
     * Takes a path at which to place the tgz, and a path from which to assemble the tgz, and creates it.
     * 
     * @param tgzPath the path at which to create the tgz file
     * @param rootPath the path at which to find the files to make into a tgz
     * @throws FileNotFoundException if the tgz file exists but is a directory rather than a regular file, 
     * does not exist but cannot be created, or cannot be opened for any other reason
     * @throws IOException lot of possible reasons
     */
    public static void create(Path tgzPath, Path rootPath) throws FileNotFoundException, IOException {
        try (   FileOutputStream fos = new FileOutputStream(tgzPath.toFile());
                GZIPOutputStream gos = new GZIPOutputStream(fos);
                TarArchiveOutputStream taos = new TarArchiveOutputStream(gos))
        {
            Files.walkFileTree(rootPath, new TgzFileVisitor(rootPath, taos));
        }
    }

}
