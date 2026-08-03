package com.masonsaver.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides textbook search results to the frontend.
 *
 * This first version returns demonstration listings so the full
 * frontend-to-backend search flow can be tested in Azure.
 *
 * Later, these demonstration results can be replaced with live
 * eBay, ThriftBooks, or other textbook API results.
 */
@RestController
@RequestMapping("/api/textbooks")
@CrossOrigin(origins = "*")
public class TextbookController {

    /**
     * Searches for textbook listings.
     *
     * Example request:
     * GET /api/textbooks/search?query=computer%20networks
     *
     * @param query title, course number, or ISBN supplied by the user
     * @return textbook listings containing prices and external search links
     */
    @GetMapping("/search")
    public List<Map<String, Object>> search(
            @RequestParam String query) {

        String cleanedQuery = query == null ? "" : query.trim();

        if (cleanedQuery.isBlank()) {
            return List.of();
        }

        String encodedQuery =
                URLEncoder.encode(cleanedQuery, StandardCharsets.UTF_8);

        return List.of(
            Map.of(
                "seller", "eBay",
                "condition", "Used",
                "title", cleanedQuery,
                "price", 28.99,
                "url", "https://www.ebay.com/sch/i.html?_nkw=" + encodedQuery
            ),
            Map.of(
                "seller", "ThriftBooks",
                "condition", "Good",
                "title", cleanedQuery,
                "price", 34.50,
                "url", "https://www.thriftbooks.com/browse/?b.search="
                        + encodedQuery
            ),
            Map.of(
                "seller", "GMU Bookstore",
                "condition", "New",
                "title", cleanedQuery,
                "price", 79.95,
                "url", "https://gmu.bncollege.com/"
            )
        );
    }
}