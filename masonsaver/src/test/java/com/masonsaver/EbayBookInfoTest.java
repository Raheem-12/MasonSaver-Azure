package com.masonsaver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EbayBookInfoTest
 *
 * Unit tests for EbayBookInfo, the data model used to pass eBay
 * listing information to the frontend.
 *
 * Since EbayBookInfo is a simple data holder with only getters and
 * setters, these tests verify that values set via setters are
 * correctly retrievable via the matching getters.
 *
 * Run with: mvn clean test
 */
public class EbayBookInfoTest {

    private EbayBookInfo bookInfo;

    @BeforeEach
    void setUp() {
        bookInfo = new EbayBookInfo();
    }

    /**
     * Setting the lowest price should be retrievable via getLowestEbayPrice().
     */
    @Test
    void setLowestPrice_thenGet_returnsCorrectValue() {
        bookInfo.setlowestPrice(29.99);

        assertEquals(29.99, bookInfo.getLowestEbayPrice(), 0.001);
    }

    /**
     * Setting the average used price should be retrievable via getAverageUsedPrice().
     */
    @Test
    void setAverageUsedPrice_thenGet_returnsCorrectValue() {
        bookInfo.setAverageUsedPrice(37.50);

        assertEquals(37.50, bookInfo.getAverageUsedPrice(), 0.001);
    }

    /**
     * Setting the condition should be retrievable via getCondition().
     */
    @Test
    void setCondition_thenGet_returnsCorrectValue() {
        bookInfo.setCondition("Good");

        assertEquals("Good", bookInfo.getCondition());
    }

    /**
     * Setting the listing URL should be retrievable via getListingUrl().
     */
    @Test
    void setListingUrl_thenGet_returnsCorrectValue() {
        bookInfo.setListingUrl("https://ebay.com/itm/12345");

        assertEquals("https://ebay.com/itm/12345", bookInfo.getListingUrl());
    }

    /**
     * A newly constructed EbayBookInfo should have default values
     * (0.0 for doubles, null for Strings) before any setters are called.
     */
    @Test
    void newInstance_hasDefaultValues() {
        EbayBookInfo freshInfo = new EbayBookInfo();

        assertEquals(0.0, freshInfo.getLowestEbayPrice(), 0.001);
        assertEquals(0.0, freshInfo.getAverageUsedPrice(), 0.001);
        assertNull(freshInfo.getCondition());
        assertNull(freshInfo.getListingUrl());
    }

    /**
     * All four fields should be settable independently and retain
     * their values without interfering with one another.
     */
    @Test
    void setAllFields_retainsAllValuesIndependently() {
        bookInfo.setlowestPrice(19.99);
        bookInfo.setAverageUsedPrice(24.99);
        bookInfo.setCondition("Acceptable");
        bookInfo.setListingUrl("https://ebay.com/itm/99999");

        assertEquals(19.99, bookInfo.getLowestEbayPrice(), 0.001);
        assertEquals(24.99, bookInfo.getAverageUsedPrice(), 0.001);
        assertEquals("Acceptable", bookInfo.getCondition());
        assertEquals("https://ebay.com/itm/99999", bookInfo.getListingUrl());
    }
}