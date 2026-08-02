package com.masonsaver.system;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SystemTest {

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

    private BCryptPasswordEncoder encoder;

    private static final String MOCK_EBAY_RESPONSE =
        "{\"itemSummaries\":[" +
        "{\"price\":{\"value\":\"29.99\",\"currency\":\"USD\"}," +
         "\"condition\":\"Good\"," +
         "\"itemWebUrl\":\"https://ebay.com/itm/1\"}," +
        "{\"price\":{\"value\":\"45.00\",\"currency\":\"USD\"}," +
         "\"condition\":\"New\"," +
         "\"itemWebUrl\":\"https://ebay.com/itm/2\"}" +
        "]}";

    @BeforeEach
    void setUp() {
        encoder = new BCryptPasswordEncoder();
    }

    @Test
    void scenario_newStudentRegistersSuccessfully() {
        when(userRepository.findByEmail("jane@gmu.edu")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        assertTrue(ValidationUtils.isValidEmail("jane@gmu.edu"));
        assertTrue(ValidationUtils.isPasswordStrong("securePass123"));
        Map<String, String> body = new HashMap<>();
        body.put("fullName", "Jane Smith");
        body.put("email", "jane@gmu.edu");
        body.put("password", "securePass123");
        ResponseEntity<String> response = authController.register(body);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("User registered successfully", response.getBody());
    }

    @Test
    void scenario_studentRegistersWithInvalidEmail_isRejected() {
        String badEmail = "janeatgmu.edu";
        assertFalse(ValidationUtils.isValidEmail(badEmail));
        when(userRepository.findByEmail(badEmail)).thenReturn(Optional.empty());
        Map<String, String> body = new HashMap<>();
        body.put("fullName", "Jane Smith");
        body.put("email", badEmail);
        body.put("password", "securePass123");
        ResponseEntity<String> response = authController.register(body);
        assertNotNull(response);
    }

    @Test
    void scenario_studentRegistersWithWeakPassword_isRejected() {
        assertFalse(ValidationUtils.isPasswordStrong("abc12"));
    }

    @Test
    void scenario_studentRegistersWithDuplicateEmail_isRejected() {
        User existingUser = new User("Existing Student", "jane@gmu.edu", encoder.encode("password123"));
        when(userRepository.findByEmail("jane@gmu.edu")).thenReturn(Optional.of(existingUser));
        Map<String, String> body = new HashMap<>();
        body.put("fullName", "Jane Smith");
        body.put("email", "jane@gmu.edu");
        body.put("password", "securePass123");
        ResponseEntity<String> response = authController.register(body);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Email already registered", response.getBody());
    }

    @Test
    void scenario_registeredStudentLogsInSuccessfully() {
        User registeredUser = new User("Jane Smith", "jane@gmu.edu", encoder.encode("securePass123"));
        when(userRepository.findByEmail("jane@gmu.edu")).thenReturn(Optional.of(registeredUser));
        Map<String, String> body = new HashMap<>();
        body.put("email", "jane@gmu.edu");
        body.put("password", "securePass123");
        ResponseEntity<String> response = authController.login(body);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Login successful", response.getBody());
    }

    @Test
    void scenario_studentLoginsWithWrongPassword_isRejected() {
        User registeredUser = new User("Jane Smith", "jane@gmu.edu", encoder.encode("securePass123"));
        when(userRepository.findByEmail("jane@gmu.edu")).thenReturn(Optional.of(registeredUser));
        Map<String, String> body = new HashMap<>();
        body.put("email", "jane@gmu.edu");
        body.put("password", "wrongpassword");
        ResponseEntity<String> response = authController.login(body);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid email or password", response.getBody());
    }

    @Test
    void scenario_unregisteredStudentTriesToLogin_isRejected() {
        when(userRepository.findByEmail("unknown@gmu.edu")).thenReturn(Optional.empty());
        Map<String, String> body = new HashMap<>();
        body.put("email", "unknown@gmu.edu");
        body.put("password", "somepassword");
        ResponseEntity<String> response = authController.login(body);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid email or password", response.getBody());
    }

    @Test
    void scenario_studentSearchesForTextbookByISBN_findsResult() {
        Textbook textbook = new Textbook("978-0-13-468599-1", "Computer Networks", "Tanenbaum", "6th", "Pearson");
        when(textbookRepository.findByIsbn("978-0-13-468599-1")).thenReturn(Optional.of(textbook));
        Optional<Textbook> result = textbookService.searchByISBN("978-0-13-468599-1");
        assertTrue(result.isPresent());
        assertEquals("Computer Networks", result.get().getTitle());
        assertEquals("978-0-13-468599-1", result.get().getIsbn());
    }

    @Test
    void scenario_studentSearchesForUnknownISBN_getsEmptyResult() {
        when(textbookRepository.findByIsbn(anyString())).thenReturn(Optional.empty());
        Optional<Textbook> result = textbookService.searchByISBN("000-0-00-000000-0");
        assertFalse(result.isPresent());
    }

    @Test
    void scenario_studentGetsLowestEbayPriceForTextbook() {
        doReturn(MOCK_EBAY_RESPONSE).when(ebayService).searchListingsBySearchQuery(anyString());
        double lowestPrice = ebayService.getLowestEbayPrice("978-0-13-468599-1");
        assertTrue(lowestPrice > 0);
        assertEquals(29.99, lowestPrice, 0.001);
    }

    @Test
    void scenario_studentGetsEbayListingUrl_receivesValidUrl() {
        doReturn(MOCK_EBAY_RESPONSE).when(ebayService).searchListingsBySearchQuery(anyString());
        String url = ebayService.getListingUrl("978-0-13-468599-1");
        assertFalse(url.isEmpty());
        assertTrue(url.startsWith("https://"));
    }

    @Test
    void scenario_studentGetsFullEbayBookInfoInOneCall() {
        doReturn(MOCK_EBAY_RESPONSE).when(ebayService).searchListingsBySearchQuery(anyString());
        EbayBookInfo book = ebayService.getBookInfo("978-0-13-468599-1");
        assertEquals(29.99, book.getLowestEbayPrice(), 0.001);
        assertEquals(37.495, book.getAverageUsedPrice(), 0.001);
        assertEquals("Good", book.getCondition());
        assertEquals("https://ebay.com/itm/1", book.getListingUrl());
    }

    @Test
    void scenario_studentComparesThriftBooksListings_findsBestPricePerCondition() {
        String newCopyJson = "{\"Work\":{\"Title\":\"Computer Networks\",\"Authors\":[{\"AuthorName\":\"Tanenbaum\"}],\"ActiveEdition\":{\"LowPrice\":50.00,\"BuyNowCondition\":\"New\",\"Publisher\":\"Pearson\"}}}";
        String usedCopyCheapJson = "{\"Work\":{\"Title\":\"Computer Networks\",\"Authors\":[{\"AuthorName\":\"Tanenbaum\"}],\"ActiveEdition\":{\"LowPrice\":12.00,\"BuyNowCondition\":\"Acceptable\",\"Publisher\":\"Pearson\"}}}";
        String usedCopyPricierJson = "{\"Work\":{\"Title\":\"Computer Networks\",\"Authors\":[{\"AuthorName\":\"Tanenbaum\"}],\"ActiveEdition\":{\"LowPrice\":18.00,\"BuyNowCondition\":\"Good\",\"Publisher\":\"Pearson\"}}}";

        @SuppressWarnings("unchecked")
        java.net.http.HttpResponse<String> newResponse = mock(java.net.http.HttpResponse.class);
        when(newResponse.body()).thenReturn(newCopyJson);
        @SuppressWarnings("unchecked")
        java.net.http.HttpResponse<String> usedCheapResponse = mock(java.net.http.HttpResponse.class);
        when(usedCheapResponse.body()).thenReturn(usedCopyCheapJson);
        @SuppressWarnings("unchecked")
        java.net.http.HttpResponse<String> usedPricierResponse = mock(java.net.http.HttpResponse.class);
        when(usedPricierResponse.body()).thenReturn(usedCopyPricierJson);

        String newUrl = "https://www.thriftbooks.com/w/cn/1/";
        ThriftTextbook newCopy = ThriftScraper.generate_Entry("978-0-13-468599-1", newUrl, newResponse);
        ThriftTextbook usedCheap = ThriftScraper.generate_Entry("978-0-13-468599-1", "https://www.thriftbooks.com/w/cn/2/", usedCheapResponse);
        ThriftTextbook usedPricier = ThriftScraper.generate_Entry("978-0-13-468599-1", "https://www.thriftbooks.com/w/cn/3/", usedPricierResponse);

        assertEquals("978-0-13-468599-1", newCopy.getIsbn());
        assertEquals("Computer Networks", newCopy.getTitle());
        assertEquals("Tanenbaum", newCopy.getAuthor().get(0));
        assertEquals("Pearson", newCopy.getPublisher());
        assertEquals("New", newCopy.getCondition());
        assertEquals(newUrl, newCopy.getURL());

        ArrayList<ThriftTextbook> scrapedListings = new ArrayList<>();
        scrapedListings.add(newCopy);
        scrapedListings.add(usedCheap);
        scrapedListings.add(usedPricier);

        SearchResults results = new SearchResults(scrapedListings);
        ThriftTextbook cheapestNew = results.lowestCostNew();
        ThriftTextbook cheapestUsed = results.lowestCostGood();

        assertNotNull(cheapestNew);
        assertEquals(50.00, cheapestNew.getPrice(), 0.001);
        assertNotNull(cheapestUsed);
        assertEquals(12.00, cheapestUsed.getPrice(), 0.001);
    }

    @Test
    void scenario_fullFlow_registerLoginSearch_succeeds() {
        String email = "newstudent@gmu.edu";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        Map<String, String> registerBody = new HashMap<>();
        registerBody.put("fullName", "New Student");
        registerBody.put("email", email);
        registerBody.put("password", password);
        ResponseEntity<String> registerResponse = authController.register(registerBody);
        assertEquals(200, registerResponse.getStatusCode().value());

        User savedUser = new User("New Student", email, encoder.encode(password));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(savedUser));

        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("email", email);
        loginBody.put("password", password);
        ResponseEntity<String> loginResponse = authController.login(loginBody);
        assertEquals(200, loginResponse.getStatusCode().value());

        Textbook textbook = new Textbook("978-0-13-468599-1", "Computer Networks", "Tanenbaum", "6th", "Pearson");
        when(textbookRepository.findByIsbn("978-0-13-468599-1")).thenReturn(Optional.of(textbook));
        Optional<Textbook> searchResult = textbookService.searchByISBN("978-0-13-468599-1");
        assertTrue(searchResult.isPresent());
        assertEquals("Computer Networks", searchResult.get().getTitle());
    }
}