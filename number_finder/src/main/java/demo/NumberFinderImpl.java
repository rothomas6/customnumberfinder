package demo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class NumberFinderImpl implements NumberFinder interface
 * Implements following apis
 *   1. readFromFile - reads and load CustomNumberEntity objects from the file
 *   2. contains     - checks if valueToFind is contained in the list
 */
public class NumberFinderImpl implements NumberFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberFinderImpl.class);

    private FastestComparator fastestComparator = new FastestComparator();

    /**
     * Read a list of CustommNumberEntity from a file
     * In the file the list is as a JSON  object
     * @param filePath
     * @return a list of CustomNumberEntity objects read from the file.
     *
     * @exception - an empty list will be return in case of a filenotfoundexception.
     */
    @Override
    public List<CustomNumberEntity> readFromFile(final String filePath) {
        final File file = new File(filePath);
        final JSONParser parser = new JSONParser();

        try (final Reader reader = new FileReader(file)) {
            final JSONArray customNumberJsonArray = (JSONArray) parser.parse(reader);
            return extractAndCreateCustomNumberEntity(customNumberJsonArray);
        } catch (final Exception exception) {
            LOGGER.warn("Error Reading File: " + exception.getMessage() + "; Returning empty list.");
            return new ArrayList<>();
        }
    }

    /**
     * Checks if valueToFind is contained in the list
     * @param valueToFind
     * @param list
     * @return true if valueToFind exist in the list
     *          false if not
     */
    @Override
    public boolean contains(final int valueToFind, final List<CustomNumberEntity> list) {
        Optional<Integer> numberPresent =
                list.stream().parallel()
                        .map(customNumberEntity -> {
                            return checkIfExistUsingFastestComparator(valueToFind, customNumberEntity);
                        })
                        .filter(a -> a.equals(0))
                        .findAny();  // used to allow for maximal performance in parallel operations;
        return numberPresent.isPresent();
    }

    private int checkIfExistUsingFastestComparator(final int valueToFind, final CustomNumberEntity customNumberEntity) {
        try {
            return fastestComparator.compare(valueToFind, customNumberEntity);
        } catch (final NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Using java streams extract json value from "number" key, filter not null check and creates CustomNumberEntity
     * @param customNumberJsonArray
     * @return List<CustomNumberEntity>
     */
    private List<CustomNumberEntity> extractAndCreateCustomNumberEntity(final JSONArray customNumberJsonArray) {

        return (List<CustomNumberEntity>) customNumberJsonArray.stream()
                .map(numberData -> ((JSONObject) numberData).get("number"))
                .filter(numberJson -> (numberJson != null))
                .map(numberJson -> {
                            return createCustomNumberEntity(numberJson.toString());
                        }
                )
                .collect(Collectors.toList());
    }

    /**
     * Creates a CustomNumberEntity object with the provided number as value
     * @param number
     * @return CustomNumberEntity
     */
    private CustomNumberEntity createCustomNumberEntity(final String number) {
        try {
            //return new CustomNumberEntity(number);  // CustomNumberEntity is private and final class.
            final Constructor<CustomNumberEntity> constructor = CustomNumberEntity.class.getDeclaredConstructor(String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(number);

        } catch (SecurityException | IllegalArgumentException | NoSuchMethodException | InstantiationException |
                IllegalAccessException | InvocationTargetException ex) {
            return null;
        }
    }
}
