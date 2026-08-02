package com.masonsaver;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Base64;

/**
 * EbayService
 *
 * Handles all communication with the eBay Browse API to retrieve
 * textbook listing data including prices, conditions, and listing URLs.
 *
 * Authentication is handled automatically via OAuth2 client credentials.
 * The access token is cached and reused until it expires.
 */
public class EbayService {

    private static final String CLIENT_ID =
        System.getenv("EBAY_CLIENT_ID");

    private static final String CLIENT_SECRET =
        System.getenv("EBAY_CLIENT_SECRET");

    if (CLIENT_ID == null || CLIENT_SECRET == null) {
        throw new IllegalStateException(
        "EBAY_CLIENT_ID and EBAY_CLIENT_SECRET environment variables must be set."
     );
    }

    private String accessToken = "";
    private long tokenExpirationTime = 0;

    /**
     * Retrieves a valid OAuth2 access token for the eBay API.
     *
     * If a non-expired token is already cached, it is returned immediately.
     * Otherwise a new token is requested from eBay's identity endpoint
     * using the client credentials grant type.
     *
     * @return a valid access token string, or an empty string if retrieval fails
     */
    private String getAccessToken() {

        if (!accessToken.equals("") && System.currentTimeMillis() < tokenExpirationTime) {
            return accessToken;
        }

        try {
            String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
            String encodedCredentials = Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.ebay.com/identity/v1/oauth2/token"))
                    .header("Authorization", "Basic " + encodedCredentials)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "grant_type=client_credentials&scope=https://api.ebay.com/oauth/api_scope"))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            Pattern tokenPattern = Pattern.compile("\"access_token\":\"([^\"]+)\"");
            Matcher tokenMatcher = tokenPattern.matcher(response.body());

            if (tokenMatcher.find()) {
                accessToken = tokenMatcher.group(1);
                tokenExpirationTime = System.currentTimeMillis() + (100 * 60 * 1000);
                return accessToken;
            }
        } catch (Exception e) {
            return "";
        }

        return "";
    }

    /**
     * Searches eBay for textbook listings matching the specified search query.
     *
     * This method serves as the primary communication point between the application
     * and the eBay Browse API. A request is sent using the provided search query
     * and the raw JSON response containing matching textbook listings is returned.
     *
     * Other methods within EbayService use the returned listing data to retrieve
     * pricing information, listing URLs, and textbook conditions.
     *
     * If the request fails or no matching listings are found, an empty string
     * is returned.
     *
     * @param searchQuery the ISBN, textbook title, or keyword to search for
     * @return the raw JSON response string from the eBay API, or empty string on failure
     */
    public String searchListingsBySearchQuery(String searchQuery) {

        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return "";
        }

        try {
            String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
            String url = "https://api.ebay.com/buy/browse/v1/item_summary/search?q=" + encodedQuery + "&limit=10";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + getAccessToken())
                    .header("X-EBAY-C-MARKETPLACE-ID", "EBAY_US")
                    .GET()
                    .build();

            HttpResponse<byte[]> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofByteArray());

            byte[] responseBytes = response.body();

            if ("gzip".equalsIgnoreCase(response.headers()
                    .firstValue("Content-Encoding").orElse(""))) {
                return new String(new GZIPInputStream(
                        new ByteArrayInputStream(responseBytes)).readAllBytes());
            }
            return new String(responseBytes);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Searches eBay for textbook listings matching the specified search query
     * and returns the lowest available price found.
     *
     * All matching textbook listings returned by the API are examined and their
     * prices are compared. The method determines the lowest valid price among
     * the returned listings and returns that value.
     *
     * If no matching listings are found, no valid price exists, the input
     * is invalid, or the API request fails, the method returns 0.0.
     *
     * @param searchQuery the ISBN, textbook title, or keyword used to search
     *                    for matching textbook listings on eBay
     * @return the lowest textbook price found on eBay, or 0.0 if no
     *         valid price can be determined
     */
    public double getLowestEbayPrice(String searchQuery) {

        String response = searchListingsBySearchQuery(searchQuery);

        if (response == null || response.isEmpty()) {
            return 0.0;
        }

        Pattern pattern = Pattern.compile(
                "\"price\"\\s*:\\s*\\{\\s*\"value\"\\s*:\\s*\"(\\d+\\.\\d+)\"");
        Matcher matcher = pattern.matcher(response);

        double lowestPrice = Double.MAX_VALUE;

        while (matcher.find()) {
            double price = Double.parseDouble(matcher.group(1));
            if (price < lowestPrice) {
                lowestPrice = price;
            }
        }

        return lowestPrice == Double.MAX_VALUE ? 0.0 : lowestPrice;
    }

    /**
     * Calculates the average price of textbook listings returned by eBay
     * for the specified search query.
     *
     * The prices of all qualifying listings are averaged and the resulting
     * value is returned. This can be used to estimate the typical market
     * value of a used textbook and help students make informed decisions.
     *
     * If no listings are found, the input is invalid, or the API request
     * fails, the method returns 0.0.
     *
     * @param searchQuery the ISBN, textbook title, or keyword used to search
     *                    for matching textbook listings on eBay
     * @return the average price of matching textbook listings,
     *         or 0.0 if no listings are available
     */
    public double getAverageUsedPrice(String searchQuery) {

        String response = searchListingsBySearchQuery(searchQuery);

        if (response == null || response.isEmpty()) {
            return 0.0;
        }

        Pattern pattern = Pattern.compile(
                "\"price\"\\s*:\\s*\\{\\s*\"value\"\\s*:\\s*\"(\\d+\\.\\d+)\"");
        Matcher matcher = pattern.matcher(response);

        double total = 0.0;
        int count = 0;

        while (matcher.find()) {
            total += Double.parseDouble(matcher.group(1));
            count++;
        }

        return count == 0 ? 0.0 : total / count;
    }

    /**
     * Retrieves the URL of the lowest-priced eBay listing matching the
     * specified search query.
     *
     * This method searches eBay for listings matching the provided input,
     * compares prices across all returned listings, and returns the URL
     * of the listing with the lowest price.
     *
     * The returned URL can be used to direct users to the corresponding
     * eBay product page where they can view details and purchase the textbook.
     *
     * If no matching listing is found, the input is invalid, or the API
     * request fails, an empty string is returned.
     *
     * @param searchQuery the ISBN, textbook title, or keyword used to search
     *                    for matching textbook listings on eBay
     * @return the URL of the lowest-priced eBay listing, or an empty string
     *         if no listing is available
     */
    public String getListingUrl(String searchQuery) {

        if (searchQuery == null || searchQuery.equals("")) {
            return "";
        }

        String response = searchListingsBySearchQuery(searchQuery);

        if (response == null || response.equals("")) {
            return "";
        }

        Pattern pattern = Pattern.compile(
                "\"price\"\\s*:\\s*\\{\\s*\"value\"\\s*:\\s*\"(\\d+\\.\\d+)\".*?\"itemWebUrl\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(response);

        double lowestPrice = Double.MAX_VALUE;
        String lowestPriceURL = "";

        while (matcher.find()) {
            double price = Double.parseDouble(matcher.group(1));
            String url = matcher.group(2);

            if (price < lowestPrice) {
                lowestPrice = price;
                lowestPriceURL = url;
            }
        }

        return lowestPriceURL;
    }

    /**
     * Retrieves the condition of the first textbook listing returned by
     * the eBay API for the specified search query.
     *
     * Examples of conditions include "New" and "Good".
     * This information helps users understand the physical condition of
     * the textbook before purchasing it.
     *
     * If no condition information is available, the input is invalid, or
     * the API request fails, an empty string is returned.
     *
     * @param searchQuery the ISBN, textbook title, or keyword used to search
     *                    for matching textbook listings on eBay
     * @return the condition of the textbook listing, or an empty string
     *         if no condition information is available
     */
    public String getCondition(String searchQuery) {

        if (searchQuery == null || searchQuery.equals("")) {
            return "";
        }

        String response = searchListingsBySearchQuery(searchQuery);

        if (response == null || response.equals("")) {
            return "";
        }

        Pattern pattern = Pattern.compile("\"condition\":\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    /**
     * Represents information about a textbook listing retrieved from eBay. 
     * This class serves as a data container for storing textbook details, 
     * including pricing information, listing condition, and listing URL. 
     * It allows the frontend and other applications components to access eBay textbook data through a single object. 
     * 
     * @param searchQuery searchQuery
     * @return book info
     */
    public EbayBookInfo getBookInfo(String searchQuery){
        EbayBookInfo book = new EbayBookInfo(); 

        book.setlowestPrice(getLowestEbayPrice(searchQuery));
        book.setAverageUsedPrice(getAverageUsedPrice(searchQuery));
        book.setCondition(getCondition(searchQuery));
        book.setListingUrl(getListingUrl(searchQuery));

        return book;
    }

    /**
     * Main method for manual testing purposes only.
     * Demonstrates all public methods using a sample ISBN.
     */
    public static void main(String[] args) {

        EbayService service = new EbayService();

        EbayBookInfo book = service.getBookInfo("9781285741550");

        System.out.println("Lowest price: " + book.getLowestEbayPrice());
        System.out.println();

        System.out.println("Condition: " + book.getCondition());
        System.out.println();

        System.out.println("Textbook URL: " + book.getListingUrl());
        System.out.println();

        System.out.println("Average price: " + book.getAverageUsedPrice());
        System.out.println();

        // System.out.println("Lowest price: " + service.getLowestEbayPrice("9780470501450"));
        // System.out.println();

        // System.out.println("Condition: " + service.getCondition("9780470501450"));
        // System.out.println();

        // System.out.println("Textbook URL: " + service.getListingUrl("9780470501450"));
        // System.out.println();

        // System.out.println("Average price: " + service.getAverageUsedPrice("9780470501450"));
        // System.out.println();
    }
}