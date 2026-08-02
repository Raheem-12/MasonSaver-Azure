package com.masonsaver.textbook;

import com.masonsaver.model.Textbook;
import com.masonsaver.repository.TextbookRepository;
import com.masonsaver.service.TextbookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * TextbookServiceTest
 *
 * Unit tests for searchByISBN() and searchByTitle() in TextbookService.
 * Uses Mockito to mock TextbookRepository so no real database
 * connection is needed.
 * Run with: mvn test
 */
@ExtendWith(MockitoExtension.class)
public class TextbookServiceTest {

    @Mock
    private TextbookRepository textbookRepository;

    @InjectMocks
    private TextbookService textbookService;

    private Textbook sampleTextbook;

    /**
     * setUp()
     * Creates a sample Textbook for use across multiple tests.
     */
    @BeforeEach
    void setUp() {
        sampleTextbook = new Textbook(
            "978-0-13-468599-1",
            "Computer Networks",
            "Tanenbaum",
            "6th",
            "Pearson"
        );
    }

    /* ── searchByISBN() tests ── */

    /**
     * A known ISBN that exists in the database should return
     * an Optional containing the matching Textbook.
     */
    @Test
    void searchByISBN_withExistingISBN_returnsTextbook() {
        when(textbookRepository.findByIsbn("978-0-13-468599-1"))
            .thenReturn(Optional.of(sampleTextbook));

        Optional<Textbook> result = textbookService.searchByISBN("978-0-13-468599-1");

        assertTrue(result.isPresent());
        assertEquals("Computer Networks", result.get().getTitle());
    }

    /**
     * An ISBN not in the database should return an empty Optional.
     */
    @Test
    void searchByISBN_withUnknownISBN_returnsEmptyOptional() {
        when(textbookRepository.findByIsbn(anyString())).thenReturn(Optional.empty());

        Optional<Textbook> result = textbookService.searchByISBN("000-0-00-000000-0");

        assertFalse(result.isPresent());
    }

    /**
     * A null ISBN input should return an empty Optional without
     * throwing an exception.
     */
    @Test
    void searchByISBN_withNullISBN_returnsEmptyOptional() {
        Optional<Textbook> result = textbookService.searchByISBN(null);

        assertFalse(result.isPresent());
    }

    /**
     * The returned textbook should have the correct title and edition.
     */
    @Test
    void searchByISBN_returnsCorrectTitleAndEdition() {
        when(textbookRepository.findByIsbn("978-0-13-468599-1"))
            .thenReturn(Optional.of(sampleTextbook));

        Optional<Textbook> result = textbookService.searchByISBN("978-0-13-468599-1");

        assertTrue(result.isPresent());
        assertEquals("Computer Networks", result.get().getTitle());
        assertEquals("6th", result.get().getEdition());
    }

    /**
     * A blank ISBN string with only whitespace should return an
     * empty Optional without calling the repository.
     */
    @Test
    void searchByISBN_withFormattingDifference_handlesGracefully() {
        Optional<Textbook> result = textbookService.searchByISBN("   ");

        assertFalse(result.isPresent());
        verify(textbookRepository, never()).findByIsbn(anyString());
    }

    /* ── searchByTitle() tests ── */

    /**
     * A keyword that matches one or more textbook titles should return
     * a list containing those matching Textbook entities.
     */
    @Test
    void searchByTitle_withMatchingKeyword_returnsMatchingTextbooks() {
        List<Textbook> matches = List.of(sampleTextbook);
        when(textbookRepository.findByTitleContainingIgnoreCase("Networks"))
            .thenReturn(matches);

        List<Textbook> result = textbookService.searchByTitle("Networks");

        assertEquals(1, result.size());
        assertEquals("Computer Networks", result.get(0).getTitle());
    }

    /**
     * A keyword that matches no textbook titles should return an
     * empty list rather than null.
     */
    @Test
    void searchByTitle_withNoMatches_returnsEmptyList() {
        when(textbookRepository.findByTitleContainingIgnoreCase(anyString()))
            .thenReturn(Collections.emptyList());

        List<Textbook> result = textbookService.searchByTitle("Nonexistent");

        assertTrue(result.isEmpty());
    }

    /**
     * A null keyword should return an empty list immediately without
     * calling the repository.
     */
    @Test
    void searchByTitle_withNullKeyword_returnsEmptyListWithoutQuery() {
        List<Textbook> result = textbookService.searchByTitle(null);

        assertTrue(result.isEmpty());
        verify(textbookRepository, never()).findByTitleContainingIgnoreCase(anyString());
    }

    /**
     * A blank/whitespace-only keyword should return an empty list
     * immediately without calling the repository.
     */
    @Test
    void searchByTitle_withBlankKeyword_returnsEmptyListWithoutQuery() {
        List<Textbook> result = textbookService.searchByTitle("   ");

        assertTrue(result.isEmpty());
        verify(textbookRepository, never()).findByTitleContainingIgnoreCase(anyString());
    }

    /**
     * A keyword matching multiple textbooks should return all of them.
     */
    @Test
    void searchByTitle_withMultipleMatches_returnsAllMatches() {
        Textbook second = new Textbook(
            "111-2-22-333333-3", "Computer Architecture", "Patterson", "5th", "Pearson"
        );
        List<Textbook> matches = List.of(sampleTextbook, second);
        when(textbookRepository.findByTitleContainingIgnoreCase("Computer"))
            .thenReturn(matches);

        List<Textbook> result = textbookService.searchByTitle("Computer");

        assertEquals(2, result.size());
    }
}