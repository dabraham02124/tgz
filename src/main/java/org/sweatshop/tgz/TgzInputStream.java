package org.sweatshop.tgz;

import static java.lang.String.format;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
    
    public TgzInputStream(Path tgzPath, String internalPath) 
            throws FileNotFoundException, IOException 
    {
        super();
        Optional<ByteArrayInputStream> ois = getInputStream(tgzPath, internalPath);
        if (ois.isPresent()) {
            bais = ois.get();
        } else {
            throw new FileNotFoundException(format("%s within %s does not exist", internalPath, tgzPath.toUri()));
        }
    }
    
    private static Optional<ByteArrayInputStream> getInputStream(Path tgzPath, String internalPath) 
            throws FileNotFoundException, IOException 
    {
        try (   InputStream fis = Files.newInputStream(tgzPath);
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

    @Override public int available() {
        return bais.available();
    }
    
    @Override public int read() throws IOException {
        return bais.read();
    }
    
    @Override public void close() throws IOException {
        bais.close();
    }

}
