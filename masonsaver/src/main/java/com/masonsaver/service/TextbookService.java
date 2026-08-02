package com.masonsaver.service;

import com.masonsaver.model.Textbook;
import com.masonsaver.repository.TextbookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * TextbookService
 *
 * Contains the core business logic for textbook search operations.
 *
 * This class sits between the controller layer (which handles HTTP requests)
 * and the repository layer (which talks to the database). It is responsible
 * for validating inputs, calling the repository, and returning results.
 *
 * @Service tells Spring to manage this class as a singleton bean,
 * making it available for injection into controllers and other services.
 */
@Service
public class TextbookService {

    /**
     * TextbookRepository instance injected by Spring automatically.
     * Used to query the PostgreSQL textbooks table.
     */
    @Autowired
    private TextbookRepository textbookRepository;

    /**
     * searchByISBN
     *
     * Searches for a textbook in the local database by its ISBN.
     *
     * Looks up a Textbook entity whose isbn field exactly matches
     * the given value. Returns an Optional so the caller can safely
     * handle the case where no textbook with that ISBN exists in
     * the local catalog.
     *
     * This method is the entry point for the price comparison flow.
     * When a user searches by ISBN on the dashboard, this method
     * retrieves the textbook record which is then used to fetch
     * live prices from external sources.
     *
     * Returns an empty Optional (rather than throwing an exception)
     * when no match is found, forcing callers to explicitly handle
     * the not-found case.
     *
     * @param isbn the ISBN string to search for, in 10 or 13 digit format
     * @return Optional containing the matching Textbook if found,
     *         or an empty Optional if no match exists or isbn is null
     */
    public Optional<Textbook> searchByISBN(String isbn) {
        /* Return empty Optional immediately if input is null or blank */
        if (isbn == null || isbn.trim().isEmpty()) {
            return Optional.empty();
        }
        return textbookRepository.findByIsbn(isbn.trim());
    }

    /**
     * searchByTitle
     *
     * Searches for textbooks in the local database by a title keyword.
     *
     * Performs a case-insensitive partial match against the title field,
     * returning all textbooks whose title contains the given keyword.
     * This supports the main search bar on the dashboard when the user
     * does not know the exact ISBN.
     *
     * Returns an empty list (rather than null or an exception) when no
     * matches are found or when the keyword is null or blank.
     *
     * @param keyword the search term to match against textbook titles
     * @return a List of matching Textbook entities, or an empty list
     *         if no matches are found or keyword is null
     */
    public List<Textbook> searchByTitle(String keyword) {
        /* Return empty list immediately if input is null or blank */
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return textbookRepository.findByTitleContainingIgnoreCase(keyword.trim());
    }
}