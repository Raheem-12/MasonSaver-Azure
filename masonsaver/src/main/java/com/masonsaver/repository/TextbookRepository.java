package com.masonsaver.repository;

import com.masonsaver.model.Textbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * TextbookRepository
 *
 * Handles all database operations for the Textbook entity.
 *
 * By extending JpaRepository, Spring automatically provides standard
 * operations like save(), findById(), findAll(), and delete() with
 * no extra code needed.
 *
 * The two type parameters are:
 *   Textbook — the entity this repository manages
 *   Long     — the type of the primary key (the id field in Textbook.java)
 *
 * The two custom query methods below are automatically implemented by
 * Spring Data JPA based on their method names. No SQL needs to be written.
 */
@Repository
public interface TextbookRepository extends JpaRepository<Textbook, Long> {

    /**
     * findByIsbn
     *
     * Looks up a single textbook by its exact ISBN.
     *
     * Spring automatically generates the SQL query:
     * SELECT * FROM textbooks WHERE isbn = ?
     *
     * Returns an Optional so the caller can safely handle the case
     * where no textbook with that ISBN exists in the local catalog.
     *
     * Used by TextbookService.searchByISBN() to retrieve the
     * textbook record before fetching external prices.
     *
     * @param isbn the exact ISBN string to search for
     * @return an Optional containing the Textbook if found,
     *         or an empty Optional if no match exists
     */
    Optional<Textbook> findByIsbn(String isbn);

    /**
     * findByTitleContainingIgnoreCase
     *
     * Searches for textbooks whose title contains the given keyword,
     * case-insensitively.
     *
     * Spring automatically generates the SQL query:
     * SELECT * FROM textbooks WHERE LOWER(title) LIKE LOWER('%keyword%')
     *
     * Returns all matching textbooks as a list. Returns an empty list
     * if no textbooks match the keyword.
     *
     * Used by TextbookService.searchByTitle() to support the main
     * search bar on the dashboard when a user enters a book title
     * instead of an ISBN.
     *
     * @param keyword the search term to match against textbook titles
     * @return a list of matching Textbook entities, or an empty list
     *         if no matches are found
     */
    List<Textbook> findByTitleContainingIgnoreCase(String keyword);
}