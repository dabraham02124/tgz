package org.sweatshop.tgz;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

public class TGZ {

    public static Optional<InputStream> getInputStream(File tgzFileName, String internalPath) 
            throws FileNotFoundException, IOException 
    {
        try (   FileInputStream fis = new FileInputStream(tgzFileName);
                GZIPInputStream gis = new GZIPInputStream(fis);
                TarArchiveInputStream tais = new TarArchiveInputStream(gis))
        {

            TarArchiveEntry currentEntry = tais.getNextTarEntry();
            while (currentEntry != null) {
                if (equalsEnough(currentEntry.getName(), internalPath)) {
                    return Optional.of(new ByteArrayInputStream(readFullBuffer(tais)));
                }
                currentEntry = tais.getNextTarEntry();
            }
        }
        return Optional.empty();
    }

    private static byte[] readFullBuffer(InputStream is) throws IOException {
        int n = is.available();
        byte[] bytes = new byte[n];
        int copied = 0;
        while (copied < n) {
            int thisTime = is.read(bytes, copied, n-copied);
            copied += thisTime;
        }

        return bytes;
    }

    private static boolean equalsEnough(String absolutePath, String internalPath) {
        return internalPath.equalsIgnoreCase(absolutePath);
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        Optional<InputStream> f = getInputStream(new File("./local/src.tgz"), "src/main/java/org/sweatshop/tgz/TGZ.java");

        InputStream in = f.get();
        int n = in.available();
        byte[] bytes = new byte[n];
        in.read(bytes, 0, n);
        String s = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(s);
    }
}
