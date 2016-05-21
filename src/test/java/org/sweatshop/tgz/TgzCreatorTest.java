package org.sweatshop.tgz;

import static org.testng.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TgzCreatorTest {

    private static final String tgzFilename = "./target/test-file.tgz";
    private static final String pathToFiles = "./src/test/resources/dir-to-tgz/";
    private static final Path testFileTgz = Paths.get(tgzFilename);
    
    
    @BeforeClass public void createTgz() throws FileNotFoundException, IOException {
        
        TgzCreator.create(testFileTgz, Paths.get(pathToFiles));
    }
    
    @AfterClass public void deleteTgz() throws IOException {
        Files.delete(testFileTgz);
    }
    
    @Test
    public void compareFileStructureToTgz() throws FileNotFoundException, IOException {
        testFileEquals("Foo.txt");
        testFileEquals("sub-dir/Bar.txt");
    }
    
    private void testFileEquals(String relativePath) throws FileNotFoundException, IOException {
        try (InputStream is = new TgzInputStream(testFileTgz, relativePath)) {
            assertEquals(
                inputStreamToIntList(is)
                , inputStreamToIntList(Files.newInputStream(Paths.get(pathToFiles+relativePath)))
                );
        }
    }
    
    public static List<Integer> inputStreamToIntList(InputStream is) throws IOException {
        List<Integer> list = new LinkedList<>();
        for (int i = is.read(); i != -1; i = is.read()) {
            list.add(i);
        }
        return list;
    }
    
    @Test
    public void testAvailable() throws FileNotFoundException, IOException {
        try (InputStream is = new TgzInputStream(testFileTgz, "Foo.txt")) {
            assertEquals(is.available(), 17);
        }
    }
    
    @Test(expectedExceptions=FileNotFoundException.class)
    public void testCantFindInternalFile() throws FileNotFoundException, IOException {
        System.out.println(new TgzInputStream(testFileTgz, "does-not-exist"));
    }

    @Test(expectedExceptions=NoSuchFileException.class)
    public void testCantTgzFile() throws FileNotFoundException, IOException {
        System.out.println(new TgzInputStream(Paths.get("does-not-exist"), "does-not-exist"));
    }

}