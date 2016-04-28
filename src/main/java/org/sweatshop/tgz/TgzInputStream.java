package org.sweatshop.tgz;

import static java.lang.String.format;

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

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper=false)
public class TgzInputStream extends InputStream {
    @Getter(AccessLevel.NONE) ByteArrayInputStream bais;
    
    public TgzInputStream(File tgzFileName, String internalPath) 
            throws FileNotFoundException, IOException 
    {
        super();
        Optional<ByteArrayInputStream> ois = getInputStream(tgzFileName, internalPath);
        if (ois.isPresent()) {
            bais = ois.get();
        } else {
            throw new FileNotFoundException(format("%s within %s does not exist", internalPath, tgzFileName.getAbsolutePath()));
        }
    }
    
    private static Optional<ByteArrayInputStream> getInputStream(File tgzFileName, String internalPath) 
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
        try (TgzInputStream tis = new TgzInputStream(
                new File("./local/src.tgz")
                , "src/main/java/org/sweatshop/tgz/TGZ.java")) 
        {
    
            int n = tis.available();
            byte[] bytes = new byte[n];
            tis.read(bytes, 0, n);
            String s = new String(bytes, StandardCharsets.UTF_8);
            System.out.println(s);
        }
    }

    @Override
    public int available() {
        return bais.available();
    }
    
    @Override
    public int read() throws IOException {
        return bais.read();
    }
}
