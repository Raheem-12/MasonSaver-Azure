package com.masonsaver;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SearchResultsTest
 *
 * Unit tests for SearchResults: the JSON-parsing constructor,
 * lowestCostNew(), lowestCostGood(), getTextbooks(), and toString().
 *
 * The JSON-parsing constructor is tested directly with hardcoded JSON
 * strings matching the ThriftBooks work-info response format, requiring
 * no network access. The comparison-logic methods are tested using a
 * package-private test constructor that accepts a pre-built list of
 * ThriftTextbook objects directly.
 *
 * Run with: mvn clean test
 */
public class SearchResultsTest {

    private ThriftTextbook book(String isbn, double price, String condition) {
        return new ThriftTextbook(isbn, price, condition, null, null, null, null);
    }

    /* ── JSON-parsing constructor tests ── */

    /**
     * The JSON-parsing constructor should correctly parse a well-formed
     * work info response with multiple copies into ThriftTextbook entries.
     */
    @Test
    void jsonConstructor_withValidResponse_parsesAllCopies() throws Exception {
        String json = "{\"Work\":{\"ActiveEdition\":{" +
            "\"ISBN\":\"9780470501450\"," +
            "\"Copies\":[" +
                "{\"Price\":25.00,\"Quality\":\"Good\"}," +
                "{\"Price\":40.00,\"Quality\":\"New\"}" +
            "]" +
        "}}}";

        SearchResults results = new SearchResults(json);

        assertEquals(2, results.getTextbooks().size());
    }

    /**
     * Copies with a missing or blank ISBN should be filtered out by
     * the constructor's internal validIsbn() check.
     */
    @Test
    void jsonConstructor_withMissingIsbn_filtersOutInvalidCopy() throws Exception {
        String json = "{\"Work\":{\"ActiveEdition\":{" +
            "\"ISBN\":\"\"," +
            "\"Copies\":[" +
                "{\"Price\":25.00,\"Quality\":\"Good\"}" +
            "]" +
        "}}}";

        SearchResults results = new SearchResults(json);

        assertTrue(results.getTextbooks().isEmpty());
    }

    /**
     * The JSON-parsing constructor should correctly extract price and
     * condition fields into the resulting ThriftTextbook objects.
     */
    @Test
    void jsonConstructor_extractsCorrectPriceAndCondition() throws Exception {
        String json = "{\"Work\":{\"ActiveEdition\":{" +
            "\"ISBN\":\"9780470501450\"," +
            "\"Copies\":[" +
                "{\"Price\":15.50,\"Quality\":\"Acceptable\"}" +
            "]" +
        "}}}";

        SearchResults results = new SearchResults(json);

        ThriftTextbook tb = results.getTextbooks().get(0);
        assertEquals(15.50, tb.getPrice(), 0.001);
        assertEquals("Acceptable", tb.getCondition());
    }

    /**
     * A JSON response with no copies at all should produce an empty
     * textbooks list rather than throwing an exception.
     */
    @Test
    void jsonConstructor_withNoCopies_returnsEmptyList() throws Exception {
        String json = "{\"Work\":{\"ActiveEdition\":{" +
            "\"ISBN\":\"9780470501450\"," +
            "\"Copies\":[]" +
        "}}}";

        SearchResults results = new SearchResults(json);

        assertTrue(results.getTextbooks().isEmpty());
    }

    /* ── lowestCostNew() tests ── */

    /**
     * When multiple "New" condition listings exist, the method should
     * return the one with the lowest price.
     */
    @Test
    void lowestCostNew_withMultipleNewListings_returnsCheapest() {
        ArrayList<ThriftTextbook> books = new ArrayList<>();
        books.add(book("111", 45.00, "New"));
        books.add(book("111", 32.50, "New"));
        books.add(book("111", 60.00, "New"));

        SearchResults results = new SearchResults(books);

        ThriftTextbook lowest = results.lowestCostNew();

        assertNotNull(lowest);
        assertEquals(32.50, lowest.getPrice(), 0.001);
    }

    /**
     * When no "New" condition listings exist, the method should return null.
     */
    @Test
    void lowestCostNew_withNoNewListings_returnsNull() {
        ArrayList<ThriftTextbook> books = new ArrayList<>();
        books.add(book("111", 20.00, "Good"));
        books.add(book("111", 15.00, "Acceptable"));

        SearchResults results = new SearchResults(books);

        assertNull(results.lowestCostNew());
    }

    /**
     * The condition check should be case-insensitive, so "new" lowercase
     * should still be recognized as a New condition listing.
     */
    @Test
    void lowestCostNew_withLowercaseCondition_isCaseInsensitive() {
        ArrayList<ThriftTextbook> books = new ArrayList<>();
        books.add(book("111", 25.00, "new"));

        SearchResults results = new SearchResults(books);

        ThriftTextbook lowest = results.lowestCostNew();

        assertNotNull(lowest);
        assertEquals(25.00, lowest.getPrice(), 0.001);
    }

    /**
     * When only one "New" listing exists, that listing should be returned.
     */
    @Test
    void lowestCostNew_withSingleNewListing_returnsThatListing() {
        ArrayList<ThriftTextbook> books = new ArrayList<>();
        books.add(book("111", 40.00, "New"));
        books.add(book("111", 18.00, "Good"));

        SearchResults results = new SearchResults(books);

        ThriftTextbook lowest = results.lowestCostNew();

        assertNotNull(lowest);
        assertEquals(40.00, lowest.getPrice(), 0.001);
    }

    /* ── lowestCostGood() tests ── */

    /**
     * When multiple used condition listings exist (Good, Acceptable, etc.),
     * the method should return the one with the lowest price.
     */
    @Test
    void lowestCostGood_withMultipleUsedListings_returnsCheapest() {
        ArrayList<ThriftTextbook> books = new ArrayList<>();
        books.add(book("111", 18.00, "Good"));
        books.add(book("111", 12.50, "Acceptable"));
        books.add(book("111", 22.00, "Very Good"));

        SearchResults results = new SearchResults(books);

        ThriftTextbook lowest = results.lowestCostGood();

        assertNotNull(lowest);
        assertEquals(12.50, lowest.getPrice(), 0.001);
    }

    /**
     * "New" condition listings should be excluded from lowestCostGood(),
     * even if they have the lowest price overall.
     */
    @Test
    void lowestCostGood_excludesNewCondition() {
        ArrayList<ThriftTextbook> books = new ArrayList<>();
        books.add(book("111", 5.00, "New"));
        books.add(book("111", 15.00, "Good"));

        SearchResults results = new SearchResults(books);

        ThriftTextbook lowest = results.lowestCostGood();

        assertNotNull(lowest);
        assertEquals(15.00, lowest.getPrice(), 0.001);
        assertEquals("Good", lowest.getCondition());
    }

    /**
     * When no used condition listings exist (only New), the method
     * should return null.
     */
    @Test
    void lowestCostGood_withOnlyNewListings_returnsNull() {
        ArrayList<ThriftTextbook> books = new ArrayList<>();
        books.add(book("111", 30.00, "New"));
        books.add(book("111", 35.00, "New"));

        SearchResults results = new SearchResults(books);

        assertNull(results.lowestCostGood());
    }

    /* ── getTextbooks() tests ── */

    /**
     * getTextbooks() should return the complete list of all textbooks
     * regardless of condition.
     */
    @Test
    void getTextbooks_returnsAllListings() {
        ArrayList<ThriftTextbook> books = new ArrayList<>();
        books.add(book("111", 10.00, "Good"));
        books.add(book("111", 20.00, "New"));

        SearchResults results = new SearchResults(books);

        assertEquals(2, results.getTextbooks().size());
    }

    /**
     * An empty list of textbooks should result in both lowestCostNew()
     * and lowestCostGood() returning null without throwing exceptions.
     */
    @Test
    void emptyResults_returnsNullForBothLowestCostMethods() {
        SearchResults results = new SearchResults(new ArrayList<>());

        assertNull(results.lowestCostNew());
        assertNull(results.lowestCostGood());
        assertTrue(results.getTextbooks().isEmpty());
    }

    /**
     * A JSON response with no ISBN field at all (parsing to a null
     * String rather than an empty one) should also be filtered out,
     * exercising the null-check branch of validIsbn() independently
     * from the empty-string branch.
     */
    @Test
    void jsonConstructor_withNullIsbn_filtersOutInvalidCopy() throws Exception {
        String json = "{\"Work\":{\"ActiveEdition\":{" +
            "\"Copies\":[" +
                "{\"Price\":25.00,\"Quality\":\"Good\"}" +
            "]" +
        "}}}";

        SearchResults results = new SearchResults(json);

        assertTrue(results.getTextbooks().isEmpty());
    }

    /* ── toString() test ── */

    /**
     * toString() should produce a non-empty summary string containing
     * the ISBN of each textbook in the results.
     */
    @Test
    void toString_includesTextbookSummary() {
        ArrayList<ThriftTextbook> books = new ArrayList<>();
        books.add(book("9780470501450", 20.00, "Good"));

        SearchResults results = new SearchResults(books);

        String summary = results.toString();

        assertTrue(summary.contains("SearchResults"));
        assertTrue(summary.contains("9780470501450"));
    }
}