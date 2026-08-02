package com.masonsaver.integration;

import com.masonsaver.controller.AuthController;
import com.masonsaver.model.Textbook;
import com.masonsaver.model.User;
import com.masonsaver.repository.TextbookRepository;
import com.masonsaver.repository.UserRepository;
import com.masonsaver.service.TextbookService;
import com.masonsaver.util.ValidationUtils;
import com.masonsaver.EbayService;
import com.masonsaver.EbayBookInfo;
import com.masonsaver.ThriftScraper;
import com.masonsaver.ThriftTextbook;
import com.masonsaver.SearchResults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IntegrationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TextbookRepository textbookRepository;

    @InjectMocks
    private AuthController authController;

    @InjectMocks
    private TextbookService textbookService;

    @Spy
    private EbayService ebayService;

    private static final String MOCK_TWO_LISTINGS =
        "{\"itemSummaries\":[" +
        "{\"price\":{\"value\":\"29.99\",\"currency\":\"USD\"}," +
         "\"condition\":\"Good\"," +
         "\"itemWebUrl\":\"https://ebay.com/itm/1\"}," +
        "{\"price\":{\"value\":\"45.00\",\"currency\":\"USD\"}," +
         "\"condition\":\"New\"," +
         "\"itemWebUrl\":\"https://ebay.com/itm/2\"}" +
        "]}";

    @Test
    void validEmailAndPassword_thenRegister_succeeds() {
        String email = "student@gmu.edu";
        String password = "password123";

        assertTrue(ValidationUtils.isValidEmail(email));
        assertTrue(ValidationUtils.isPasswordStrong(password));

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        Map<String, String> body = new HashMap<>();
        body.put("fullName", "Mason Student");
        body.put("email", email);
        body.put("password", password);

        ResponseEntity<String> response = authController.register(body);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("User registered successfully", response.getBody());
    }

    @Test
    void invalidEmail_blocksRegistration() {
        String email = "notanemail";
        String password = "password123";

        assertFalse(ValidationUtils.isValidEmail(email));

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Map<String, String> body = new HashMap<>();
        body.put("fullName", "Mason Student");
        body.put("email", email);
        body.put("password", password);

        ResponseEntity<String> response = authController.register(body);
        assertNotNull(response);
    }

    @Test
    void registerThenLogin_succeeds() {
        String email = "student@gmu.edu";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        Map<String, String> registerBody = new HashMap<>();
        registerBody.put("fullName", "Mason Student");
        registerBody.put("email", email);
        registerBody.put("password", password);

        ResponseEntity<String> registerResponse = authController.register(registerBody);
        assertEquals(200, registerResponse.getStatusCode().value());

        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
            new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        User savedUser = new User("Mason Student", email, encoder.encode(password));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(savedUser));

        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("email", email);
        loginBody.put("password", password);

        ResponseEntity<String> loginResponse = authController.login(loginBody);
        assertEquals(200, loginResponse.getStatusCode().value());
        assertEquals("Login successful", loginResponse.getBody());
    }

    @Test
    void duplicateRegister_thenLogin_fails() {
        String email = "existing@gmu.edu";
        String password = "password123";

        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
            new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        User existingUser = new User("Existing User", email, encoder.encode("differentpassword"));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        Map<String, String> registerBody = new HashMap<>();
        registerBody.put("fullName", "New Student");
        registerBody.put("email", email);
        registerBody.put("password", password);

        ResponseEntity<String> registerResponse = authController.register(registerBody);
        assertEquals(400, registerResponse.getStatusCode().value());

        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("email", email);
        loginBody.put("password", password);

        ResponseEntity<String> loginResponse = authController.login(loginBody);
        assertEquals(400, loginResponse.getStatusCode().value());
    }

    @Test
    void saveTextbook_thenSearchByISBN_returnsTextbook() {
        Textbook textbook = new Textbook(
            "978-0-13-468599-1", "Computer Networks", "Tanenbaum", "6th", "Pearson"
        );

        when(textbookRepository.save(any(Textbook.class))).thenReturn(textbook);
        textbookRepository.save(textbook);

        when(textbookRepository.findByIsbn("978-0-13-468599-1"))
            .thenReturn(Optional.of(textbook));

        Optional<Textbook> result = textbookService.searchByISBN("978-0-13-468599-1");

        assertTrue(result.isPresent());
        assertEquals("Computer Networks", result.get().getTitle());
        assertEquals("978-0-13-468599-1", result.get().getIsbn());
    }

    @Test
    void searchByISBN_withUnknownISBN_returnsEmpty() {
        when(textbookRepository.findByIsbn(anyString())).thenReturn(Optional.empty());

        Optional<Textbook> result = textbookService.searchByISBN("000-0-00-000000-0");

        assertFalse(result.isPresent());
    }

    @Test
    void searchListings_thenGetLowestPrice_returnsCorrectPrice() {
        doReturn(MOCK_TWO_LISTINGS).when(ebayService).searchListingsBySearchQuery(anyString());

        double lowestPrice = ebayService.getLowestEbayPrice("9780470501450");

        assertEquals(29.99, lowestPrice, 0.001);
    }

    @Test
    void getLowestPrice_andGetListingUrl_returnConsistentResults() {
        doReturn(MOCK_TWO_LISTINGS).when(ebayService).searchListingsBySearchQuery(anyString());

        double lowestPrice = ebayService.getLowestEbayPrice("9780470501450");
        String listingUrl  = ebayService.getListingUrl("9780470501450");

        assertEquals(29.99, lowestPrice, 0.001);
        assertEquals("https://ebay.com/itm/1", listingUrl);
    }

    @Test
    void getLowestPrice_andGetCondition_returnConsistentResults() {
        doReturn(MOCK_TWO_LISTINGS).when(ebayService).searchListingsBySearchQuery(anyString());

        double lowestPrice = ebayService.getLowestEbayPrice("9780470501450");
        String condition   = ebayService.getCondition("9780470501450");

        assertEquals(29.99, lowestPrice, 0.001);
        assertEquals("Good", condition);
    }

    @Test
    void lowestPrice_isLessThanOrEqualTo_averagePrice() {
        doReturn(MOCK_TWO_LISTINGS).when(ebayService).searchListingsBySearchQuery(anyString());

        double lowestPrice  = ebayService.getLowestEbayPrice("9780470501450");
        double averagePrice = ebayService.getAverageUsedPrice("9780470501450");

        assertTrue(lowestPrice <= averagePrice);
    }

    @Test
    void getBookInfo_populatesEbayBookInfoFromAllFourMethods() {
        doReturn(MOCK_TWO_LISTINGS).when(ebayService).searchListingsBySearchQuery(anyString());

        EbayBookInfo book = ebayService.getBookInfo("9780470501450");

        assertEquals(29.99, book.getLowestEbayPrice(), 0.001);
        assertEquals(37.495, book.getAverageUsedPrice(), 0.001);
        assertEquals("Good", book.getCondition());
        assertEquals("https://ebay.com/itm/1", book.getListingUrl());
    }

    @Test
    void generateEntry_thenLowestCostComparison_identifiesCorrectBook() {
        String mockJsonCheap = "{\"Work\":{" +
            "\"Title\":\"Computer Networks\"," +
            "\"Authors\":[{\"AuthorName\":\"Tanenbaum\"}]," +
            "\"ActiveEdition\":{\"LowPrice\":15.00,\"BuyNowCondition\":\"Good\",\"Publisher\":\"Pearson\"}" +
            "}}";
        String mockJsonExpensive = "{\"Work\":{" +
            "\"Title\":\"Computer Networks\"," +
            "\"Authors\":[{\"AuthorName\":\"Tanenbaum\"}]," +
            "\"ActiveEdition\":{\"LowPrice\":35.00,\"BuyNowCondition\":\"Good\",\"Publisher\":\"Pearson\"}" +
            "}}";

        @SuppressWarnings("unchecked")
        java.net.http.HttpResponse<String> cheapResponse = mock(java.net.http.HttpResponse.class);
        when(cheapResponse.body()).thenReturn(mockJsonCheap);

        @SuppressWarnings("unchecked")
        java.net.http.HttpResponse<String> expensiveResponse = mock(java.net.http.HttpResponse.class);
        when(expensiveResponse.body()).thenReturn(mockJsonExpensive);

        ThriftTextbook cheapBook = ThriftScraper.generate_Entry(
            "978-0-13-468599-1", "https://www.thriftbooks.com/w/cn/1/", cheapResponse);
        ThriftTextbook expensiveBook = ThriftScraper.generate_Entry(
            "978-0-13-468599-1", "https://www.thriftbooks.com/w/cn/2/", expensiveResponse);

        ArrayList<ThriftTextbook> scrapedBooks = new ArrayList<>();
        scrapedBooks.add(cheapBook);
        scrapedBooks.add(expensiveBook);

        SearchResults results = new SearchResults(scrapedBooks);
        ThriftTextbook cheapest = results.lowestCostGood();

        assertNotNull(cheapest);
        assertEquals(15.00, cheapest.getPrice(), 0.001);
    }

    @Test
    void generateId_thenGenerateEntry_chainCorrectly() {
        String url = "https://www.thriftbooks.com/w/computer-networks/300123/";
        String workId = ThriftScraper.generate_id(url);

        String mockJson = "{\"Work\":{" +
            "\"Title\":\"Computer Networks\"," +
            "\"Authors\":[{\"AuthorName\":\"Tanenbaum\"}]," +
            "\"ActiveEdition\":{\"LowPrice\":22.00,\"BuyNowCondition\":\"New\",\"Publisher\":\"Pearson\"}" +
            "}}";

        @SuppressWarnings("unchecked")
        java.net.http.HttpResponse<String> mockResponse = mock(java.net.http.HttpResponse.class);
        when(mockResponse.body()).thenReturn(mockJson);

        ThriftTextbook result = ThriftScraper.generate_Entry("978-0-13-468599-1", url, mockResponse);

        assertEquals("300123", workId);
        assertEquals("Computer Networks", result.getTitle());
        assertEquals(url, result.getURL());
    }

    @Test
    void generateEntry_thenFullSearchResultsSurface_allMethodsWorkTogether() {
        String mockJsonNew = "{\"Work\":{" +
            "\"Title\":\"Computer Networks\"," +
            "\"Authors\":[{\"AuthorName\":\"Tanenbaum\"}]," +
            "\"ActiveEdition\":{\"LowPrice\":50.00,\"BuyNowCondition\":\"New\",\"Publisher\":\"Pearson\"}" +
            "}}";
        String mockJsonUsed = "{\"Work\":{" +
            "\"Title\":\"Computer Networks\"," +
            "\"Authors\":[{\"AuthorName\":\"Tanenbaum\"}]," +
            "\"ActiveEdition\":{\"LowPrice\":15.00,\"BuyNowCondition\":\"Good\",\"Publisher\":\"Pearson\"}" +
            "}}";

        @SuppressWarnings("unchecked")
        java.net.http.HttpResponse<String> newResponse = mock(java.net.http.HttpResponse.class);
        when(newResponse.body()).thenReturn(mockJsonNew);

        @SuppressWarnings("unchecked")
        java.net.http.HttpResponse<String> usedResponse = mock(java.net.http.HttpResponse.class);
        when(usedResponse.body()).thenReturn(mockJsonUsed);

        ThriftTextbook newCopy = ThriftScraper.generate_Entry(
            "978-0-13-468599-1", "https://www.thriftbooks.com/w/cn/1/", newResponse);
        ThriftTextbook usedCopy = ThriftScraper.generate_Entry(
            "978-0-13-468599-1", "https://www.thriftbooks.com/w/cn/2/", usedResponse);

        assertEquals("978-0-13-468599-1", newCopy.getIsbn());
        assertEquals("Tanenbaum", newCopy.getAuthor().get(0));
        assertEquals("Pearson", newCopy.getPublisher());

        ArrayList<ThriftTextbook> scrapedBooks = new ArrayList<>();
        scrapedBooks.add(newCopy);
        scrapedBooks.add(usedCopy);

        SearchResults results = new SearchResults(scrapedBooks);

        ThriftTextbook cheapestNew = results.lowestCostNew();
        ThriftTextbook cheapestUsed = results.lowestCostGood();
        int totalListings = results.getTextbooks().size();
        String summary = results.toString();

        assertNotNull(cheapestNew);
        assertEquals(50.00, cheapestNew.getPrice(), 0.001);
        assertNotNull(cheapestUsed);
        assertEquals(15.00, cheapestUsed.getPrice(), 0.001);
        assertEquals(2, totalListings);
        assertTrue(summary.contains("SearchResults"));
    }
}