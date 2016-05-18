package org.sweatshop.tgz;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TgzCreatorTest {

    public static final String tgzFilename = "./target/test-file.tgz";
    public static final String pathToFiles = "./src/test/resources/dir-to-tgz/";
    
    @BeforeClass public void createTgz() throws FileNotFoundException, IOException {
        File testFileTgz = new File(tgzFilename);
        TgzCreator.create(testFileTgz.toPath(), new File(pathToFiles).toPath());
    }
    
    @AfterClass public void deleteTgz() {
        File testFileTgz = new File(tgzFilename);
        testFileTgz.delete();
    }
    
    @Test
    public void compareFileStructureToTgz() throws FileNotFoundException, IOException {
        testFileEquals("Foo.txt");
        testFileEquals("sub-dir/Bar.txt");
    }
    
    private void testFileEquals(String relativePath) throws FileNotFoundException, IOException {
        try (InputStream is = new TgzInputStream(new File(tgzFilename), relativePath)) {
            assertEquals(
                inputStreamToIntList(is)
                , inputStreamToIntList(new FileInputStream(new File(pathToFiles+relativePath)))
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
        try (InputStream is = new TgzInputStream(new File(tgzFilename), "Foo.txt")) {
            assertEquals(is.available(), 17);
        }
    }
}
