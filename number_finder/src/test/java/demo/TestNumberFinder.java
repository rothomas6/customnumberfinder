package demo;

import static org.junit.Assert.assertEquals;

import java.util.List;


import org.junit.Test;

public class TestNumberFinder {

    final NumberFinderImpl objectUnderTest = new NumberFinderImpl();

    private final static String HOME=  System.getProperty("user.dir");

    @Test
    public void testReadAllFromFile() {

        List<CustomNumberEntity> customNumberEntityList = objectUnderTest.readFromFile(HOME + "\\" +"GoodJsonFile.json");
        assertEquals("["+"CustomNumberEntity [number=67]"+", "
                        +"CustomNumberEntity [number=45]"+", "
                        +"CustomNumberEntity [number=45]"+", "
                        +"CustomNumberEntity [number=s]"+", "
                        +"CustomNumberEntity [number=-3]"+", "
                        +"CustomNumberEntity [number=12]"+", "
                        +"CustomNumberEntity [number=100]"+", "
                        +"CustomNumberEntity [number=3]"+"]",
                customNumberEntityList.toString());
    }

    @Test
    public void testReadFileThatDoesNotExist() {
        List<CustomNumberEntity> customNumberEntityList = objectUnderTest.readFromFile(HOME + "\\" +"FileDoesnotexist.json");
        assertEquals(0, customNumberEntityList.size());
    }

    @Test
    public void testReadFileIncorrectJson() {
        List<CustomNumberEntity> customNumberEntityList = objectUnderTest.readFromFile(HOME + "\\" +"IncorrectJsonFile.json");
        assertEquals(0, customNumberEntityList.size());
    }

    @Test
    public void testValueFoundSuccessfully() {
        List<CustomNumberEntity> customNumberEntityList = objectUnderTest
                .readFromFile(HOME + "\\" +"GoodJsonFile.json");
        assertEquals(true, objectUnderTest.contains(100, customNumberEntityList));
    }

    @Test
    public void testValueNotFound1() {
        List<CustomNumberEntity> customNumberEntityList = objectUnderTest
                .readFromFile(HOME + "\\" +"GoodJsonFile.json");
        assertEquals(false, objectUnderTest.contains( -22222222, customNumberEntityList));
    }

    @Test
    public void testValueNotFound2() {
        List<CustomNumberEntity> customNumberEntityList = objectUnderTest
                .readFromFile(HOME + "\\" +"GoodJsonFile.json");
        assertEquals(false, objectUnderTest.contains(400, customNumberEntityList));
    }
}