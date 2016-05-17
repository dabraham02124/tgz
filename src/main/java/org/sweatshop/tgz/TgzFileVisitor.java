package org.sweatshop.tgz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper=false)
class TgzFileVisitor extends SimpleFileVisitor<Path> {

    Path basePath;
    @Getter(AccessLevel.PRIVATE) TarArchiveOutputStream taos;
    
    @Override public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        return FileVisitResult.CONTINUE;
    }
    
    @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        return FileVisitResult.CONTINUE;
    }
    
    @Override public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        File file = path.toFile();
        TarArchiveEntry entry = new TarArchiveEntry(file, getSubPath(path));
        entry.setSize(file.length());
        taos.putArchiveEntry(entry);
        copy(file, taos);
        taos.closeArchiveEntry();
        
        return FileVisitResult.CONTINUE;
    }
    
    private String getSubPath(Path path) {
        String s = path.toString().substring(basePath.toString().length());
        return s;
    }

    private static void copy(File file, TarArchiveOutputStream taos) throws FileNotFoundException, IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            for (int n = fis.read(); n != -1; n = fis.read()) {
                taos.write(n);
            }
        }
    }
    
    @Override public FileVisitResult visitFileFailed(Path file, IOException exc) {
        return FileVisitResult.TERMINATE;
    }

}
