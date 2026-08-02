package com.masonsaver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * EbayServiceTest
 *
 * Unit tests for EbayService methods: getLowestEbayPrice(), getAverageUsedPrice(),
 * getListingUrl(), and getCondition().
 *
 * Uses Mockito Spy to intercept searchListingsBySearchQuery() so no real
 * eBay API calls are made during testing. All HTTP responses are simulated
 * using hardcoded JSON strings that match the eBay Browse API response format.
 *
 * Run with: mvn clean test
 */
@ExtendWith(MockitoExtension.class)
public class EbayServiceTest {

    /* Spy wraps the real EbayService so we can stub searchListingsBySearchQuery()
       while keeping all other method logic real */
    @Spy
    private EbayService ebayService;

    /**
     * A simulated eBay API response with two listings at different prices.
     * Listing 1: $29.99, Good condition, URL: https://ebay.com/itm/1
     * Listing 2: $45.00, New condition, URL: https://ebay.com/itm/2
     */
    private static final String MOCK_TWO_LISTINGS =
        "{\"itemSummaries\":[" +
        "{\"price\":{\"value\":\"29.99\",\"currency\":\"USD\"}," +
         "\"condition\":\"Good\"," +
         "\"itemWebUrl\":\"https://ebay.com/itm/1\"}," +
        "{\"price\":{\"value\":\"45.00\",\"currency\":\"USD\"}," +
         "\"condition\":\"New\"," +
         "\"itemWebUrl\":\"https://ebay.com/itm/2\"}" +
        "]}";

    /**
     * A simulated eBay API response with a single listing.
     * Listing: $19.99, Acceptable condition, URL: https://ebay.com/itm/3
     */
    private static final String MOCK_SINGLE_LISTING =
        "{\"itemSummaries\":[" +
        "{\"price\":{\"value\":\"19.99\",\"currency\":\"USD\"}," +
         "\"condition\":\"Acceptable\"," +
         "\"itemWebUrl\":\"https://ebay.com/itm/3\"}" +
        "]}";

    /**
     * A simulated eBay API response with no listings (empty result set).
     */
    private static final String MOCK_EMPTY_RESPONSE =
        "{\"itemSummaries\":[]}";

    /* ── getLowestEbayPrice() tests ── */

    /**
     * When multiple listings exist, the method should return the lowest price.
     * Listings: $29.99 and $45.00 → expected lowest: 29.99
     */
    @Test
    void getLowestEbayPrice_withMultipleListings_returnsLowestPrice() {
        doReturn(MOCK_TWO_LISTINGS).when(ebayService).searchListingsBySearchQuery(anyString());

        double result = ebayService.getLowestEbayPrice("9780470501450");

        assertEquals(29.99, result, 0.001);
    }

    /**
     * When a single listing exists, the method should return that listing's price.
     */
    @Test
    void getLowestEbayPrice_withSingleListing_returnsListingPrice() {
        doReturn(MOCK_SINGLE_LISTING).when(ebayService).searchListingsBySearchQuery(anyString());

        double result = ebayService.getLowestEbayPrice("9780470501450");

        assertEquals(19.99, result, 0.001);
    }

    /**
     * When no listings are found, the method should return 0.0.
     */
    @Test
    void getLowestEbayPrice_withNoListings_returnsZero() {
        doReturn(MOCK_EMPTY_RESPONSE).when(ebayService).searchListingsBySearchQuery(anyString());

        double result = ebayService.getLowestEbayPrice("9780470501450");

        assertEquals(0.0, result, 0.001);
    }

    /**
     * When the search query is null, the method should return 0.0
     * without throwing an exception.
     */
    @Test
    void getLowestEbayPrice_withNullQuery_returnsZero() {
        doReturn("").when(ebayService).searchListingsBySearchQuery(null);

        double result = ebayService.getLowestEbayPrice(null);

        assertEquals(0.0, result, 0.001);
    }

    /**
     * When the API returns an empty string, the method should return 0.0.
     */
    @Test
    void getLowestEbayPrice_withEmptyResponse_returnsZero() {
        doReturn("").when(ebayService).searchListingsBySearchQuery(anyString());

        double result = ebayService.getLowestEbayPrice("9780470501450");

        assertEquals(0.0, result, 0.001);
    }

    /**
     * When the response from searchListingsBySearchQuery is literally
     * null, getLowestEbayPrice() should still return 0.0 rather than
     * throwing a NullPointerException.
     */
    @Test
    void getLowestEbayPrice_withNullResponse_returnsZero() {
        doReturn(null).when(ebayService).searchListingsBySearchQuery(anyString());

        double result = ebayService.getLowestEbayPrice("9780470501450");

        assertEquals(0.0, result, 0.001);
    }

    /**
     * With three listings where the lowest price appears first, the
     * comparison loop must correctly skip updating the minimum on both
     * subsequent (higher-priced) iterations, exercising the false side
     * of the price-comparison branch more than once.
     */
    @Test
    void getLowestEbayPrice_withThreeListingsLowestFirst_skipsHigherPrices() {
        String mockThreeListings =
            "{\"itemSummaries\":[" +
            "{\"price\":{\"value\":\"10.00\",\"currency\":\"USD\"}," +
             "\"condition\":\"Good\",\"itemWebUrl\":\"https://ebay.com/itm/a\"}," +
            "{\"price\":{\"value\":\"20.00\",\"currency\":\"USD\"}," +
             "\"condition\":\"New\",\"itemWebUrl\":\"https://ebay.com/itm/b\"}," +
            "{\"price\":{\"value\":\"30.00\",\"currency\":\"USD\"}," +
             "\"condition\":\"New\",\"itemWebUrl\":\"https://ebay.com/itm/c\"}" +
            "]}";

        doReturn(mockThreeListings).when(ebayService).searchListingsBySearchQuery(anyString());

        double result = ebayService.getLowestEbayPrice("9780470501450");

        assertEquals(10.00, result, 0.001);
    }

    /* ── getAverageUsedPrice() tests ── */

    /**
     * When multiple listings exist, the method should return the correct average.
     * Listings: $29.99 and $45.00 → expected average: (29.99 + 45.00) / 2 = 37.495
     */
    @Test
    void getAverageUsedPrice_withMultipleListings_returnsCorrectAverage() {
        doReturn(MOCK_TWO_LISTINGS).when(ebayService).searchListingsBySearchQuery(anyString());

        double result = ebayService.getAverageUsedPrice("9780470501450");

        assertEquals(37.495, result, 0.001);
    }

    /**
     * When a single listing exists, the method should return that listing's price
     * as the average.
     */
    @Test
    void getAverageUsedPrice_withSingleListing_returnsListingPrice() {
        doReturn(MOCK_SINGLE_LISTING).when(ebayService).searchListingsBySearchQuery(anyString());

        double result = ebayService.getAverageUsedPrice("9780470501450");

        assertEquals(19.99, result, 0.001);
    }

    /**
     * When no listings are found, the method should return 0.0.
     */
    @Test
    void getAverageUsedPrice_withNoListings_returnsZero() {
        doReturn(MOCK_EMPTY_RESPONSE).when(ebayService).searchListingsBySearchQuery(anyString());

        double result = ebayService.getAverageUsedPrice("9780470501450");

        assertEquals(0.0, result, 0.001);
    }

    /**
     * When the search query is null, the method should return 0.0.
     */
    @Test
    void getAverageUsedPrice_withNullQuery_returnsZero() {
        doReturn("").when(ebayService).searchListingsBySearchQuery(null);

        double result = ebayService.getAverageUsedPrice(null);

        assertEquals(0.0, result, 0.001);
    }

    /**
     * When the response from searchListingsBySearchQuery is literally
     * null, getAverageUsedPrice() should still return 0.0 rather than
     * throwing a NullPointerException.
     */
    @Test
    void getAverageUsedPrice_withNullResponse_returnsZero() {
        doReturn(null).when(ebayService).searchListingsBySearchQuery(anyString());

        double result = ebayService.getAverageUsedPrice("9780470501450");

        assertEquals(0.0, result, 0.001);
    }

    /* ── getListingUrl() tests ── */

    /**
     * When multiple listings exist, the method should return the URL
     * of the lowest-priced listing.
     * Lowest price is $29.99 at https://ebay.com/itm/1
     */
    @Test
    void getListingUrl_withMultipleListings_returnsLowestPriceUrl() {
        doReturn(MOCK_TWO_LISTINGS).when(ebayService).searchListingsBySearchQuery(anyString());

        String result = ebayService.getListingUrl("9780470501450");

        assertEquals("https://ebay.com/itm/1", result);
    }

    /**
     * When a single listing exists, the method should return that listing's URL.
     */
    @Test
    void getListingUrl_withSingleListing_returnsListingUrl() {
        doReturn(MOCK_SINGLE_LISTING).when(ebayService).searchListingsBySearchQuery(anyString());

        String result = ebayService.getListingUrl("9780470501450");

        assertEquals("https://ebay.com/itm/3", result);
    }

    /**
     * When the search query is null, the method should return an empty string
     * without throwing an exception.
     */
    @Test
    void getListingUrl_withNullQuery_returnsEmptyString() {
        String result = ebayService.getListingUrl(null);

        assertEquals("", result);
    }

    /**
     * When the search query is empty, the method should return an empty string.
     */
    @Test
    void getListingUrl_withEmptyQuery_returnsEmptyString() {
        String result = ebayService.getListingUrl("");

        assertEquals("", result);
    }

    /**
     * When no listings are found, the method should return an empty string.
     */
    @Test
    void getListingUrl_withNoListings_returnsEmptyString() {
        doReturn(MOCK_EMPTY_RESPONSE).when(ebayService).searchListingsBySearchQuery(anyString());

        String result = ebayService.getListingUrl("9780470501450");

        assertEquals("", result);
    }

    /**
     * When the response from searchListingsBySearchQuery is literally
     * null (not just empty), getListingUrl() should still return an
     * empty string rather than throwing a NullPointerException. This
     * exercises the response == null branch independently of the
     * response.equals("") branch.
     */
    @Test
    void getListingUrl_withNullResponse_returnsEmptyString() {
        doReturn(null).when(ebayService).searchListingsBySearchQuery(anyString());

        String result = ebayService.getListingUrl("9780470501450");

        assertEquals("", result);
    }

    /* ── getCondition() tests ── */

    /**
     * When listings exist, the method should return the condition of the first listing.
     * First listing in MOCK_TWO_LISTINGS has condition "Good".
     */
    @Test
    void getCondition_withValidQuery_returnsCondition() {
        doReturn(MOCK_TWO_LISTINGS).when(ebayService).searchListingsBySearchQuery(anyString());

        String result = ebayService.getCondition("9780470501450");

        assertEquals("Good", result);
    }

    /**
     * When a single listing exists, the method should return its condition.
     */
    @Test
    void getCondition_withSingleListing_returnsCondition() {
        doReturn(MOCK_SINGLE_LISTING).when(ebayService).searchListingsBySearchQuery(anyString());

        String result = ebayService.getCondition("9780470501450");

        assertEquals("Acceptable", result);
    }

    /**
     * When the search query is null, the method should return an empty string
     * without throwing an exception.
     */
    @Test
    void getCondition_withNullQuery_returnsEmptyString() {
        String result = ebayService.getCondition(null);

        assertEquals("", result);
    }

    /**
     * When the search query is empty, the method should return an empty string.
     */
    @Test
    void getCondition_withEmptyQuery_returnsEmptyString() {
        String result = ebayService.getCondition("");

        assertEquals("", result);
    }

    /**
     * When no listings are found, the method should return an empty string.
     */
    @Test
    void getCondition_withNoListings_returnsEmptyString() {
        doReturn(MOCK_EMPTY_RESPONSE).when(ebayService).searchListingsBySearchQuery(anyString());

        String result = ebayService.getCondition("9780470501450");

        assertEquals("", result);
    }

    /* ── getBookInfo() tests ── */

    /**
     * getBookInfo() should aggregate the results of getLowestEbayPrice(),
     * getAverageUsedPrice(), getCondition(), and getListingUrl() into a
     * single EbayBookInfo object, all derived from the same underlying
     * search response.
     */
    @Test
    void getBookInfo_withListings_returnsFullyPopulatedBookInfo() {
        doReturn(MOCK_TWO_LISTINGS).when(ebayService).searchListingsBySearchQuery(anyString());

        EbayBookInfo book = ebayService.getBookInfo("9780470501450");

        assertEquals(29.99, book.getLowestEbayPrice(), 0.001);
        assertEquals(37.495, book.getAverageUsedPrice(), 0.001);
        assertEquals("Good", book.getCondition());
        assertEquals("https://ebay.com/itm/1", book.getListingUrl());
    }

    /**
     * getBookInfo() called with no listings available should return an
     * EbayBookInfo with default/empty values rather than throwing.
     */
    @Test
    void getBookInfo_withNoListings_returnsEmptyBookInfo() {
        doReturn(MOCK_EMPTY_RESPONSE).when(ebayService).searchListingsBySearchQuery(anyString());

        EbayBookInfo book = ebayService.getBookInfo("9780470501450");

        assertEquals(0.0, book.getLowestEbayPrice(), 0.001);
        assertEquals(0.0, book.getAverageUsedPrice(), 0.001);
        assertEquals("", book.getCondition());
        assertEquals("", book.getListingUrl());
    }

    /**
     * When the response from searchListingsBySearchQuery is literally
     * null, getCondition() should still return an empty string rather
     * than throwing a NullPointerException.
     */
    @Test
    void getCondition_withNullResponse_returnsEmptyString() {
        doReturn(null).when(ebayService).searchListingsBySearchQuery(anyString());

        String result = ebayService.getCondition("9780470501450");

        assertEquals("", result);
    }

    /* ── searchListingsBySearchQuery() tests ── */

    /**
     * When the search query is null, the method should return an empty string.
     */
    @Test
    void searchListingsBySearchQuery_withNullQuery_returnsEmptyString() {
        String result = ebayService.searchListingsBySearchQuery(null);

        assertEquals("", result);
    }

    /**
     * When the search query is blank whitespace, the method should return
     * an empty string.
     */
    @Test
    void searchListingsBySearchQuery_withBlankQuery_returnsEmptyString() {
        String result = ebayService.searchListingsBySearchQuery("   ");

        assertEquals("", result);
    }
}