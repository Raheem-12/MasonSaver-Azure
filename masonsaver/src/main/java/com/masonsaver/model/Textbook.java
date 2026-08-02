package com.masonsaver.model;

import jakarta.persistence.*;

/**
 * Textbook
 *
 * Represents a textbook entry in the MasonSaver catalog.
 *
 * This class is a JPA Entity, meaning Spring will automatically
 * create a corresponding "textbooks" table in PostgreSQL based on
 * the fields defined here when the application starts.
 *
 * A Textbook record stores the core identifying information about
 * a book: its ISBN, title, author, edition, and publisher.
 * Price data is NOT stored here — prices are fetched live from
 * external sources (Amazon, Chegg, GMU Bookstore) at search time.
 *
 * Used by TextbookRepository and TextbookService to support
 * the ISBN and title search features on the dashboard.
 */
@Entity
@Table(name = "textbooks")
public class Textbook {

    /**
     * Primary key for the textbooks table.
     * IDENTITY strategy means PostgreSQL auto-increments this value
     * for each new textbook inserted.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The textbook's ISBN (International Standard Book Number).
     * Must be unique across all textbook records.
     * Supports both 10-digit and 13-digit ISBN formats.
     * Cannot be null in the database.
     */
    @Column(nullable = false, unique = true)
    private String isbn;

    /**
     * The full title of the textbook.
     * Cannot be null in the database.
     * Used for keyword-based title searches.
     */
    @Column(nullable = false)
    private String title;

    /**
     * The author or authors of the textbook.
     * Cannot be null in the database.
     */
    @Column(nullable = false)
    private String author;

    /**
     * The edition of the textbook (e.g. "6th", "3rd").
     * Important for ensuring students buy the correct required edition.
     * Cannot be null in the database.
     */
    @Column(nullable = false)
    private String edition;

    /**
     * The publisher of the textbook (e.g. "Pearson", "McGraw Hill").
     * Nullable since publisher info may not always be available.
     */
    @Column
    private String publisher;

    /* -------------------------
     * Constructors
     * ------------------------- */

    /** Default no-argument constructor required by JPA */
    public Textbook() {}

    /**
     * Constructor for creating a new Textbook entry.
     *
     * @param isbn      the textbook's ISBN
     * @param title     the full title of the textbook
     * @param author    the author(s) of the textbook
     * @param edition   the edition of the textbook
     * @param publisher the publisher of the textbook
     */
    public Textbook(String isbn, String title, String author,
                    String edition, String publisher) {
        this.isbn      = isbn;
        this.title     = title;
        this.author    = author;
        this.edition   = edition;
        this.publisher = publisher;
    }

    /* -------------------------
     * Getters and Setters
     * ------------------------- */

    /** @return the textbook's database ID */
    public Long getId() { return id; }

    /** @return the textbook's ISBN */
    public String getIsbn() { return isbn; }

    /** @param isbn the ISBN to set */
    public void setIsbn(String isbn) { this.isbn = isbn; }

    /** @return the textbook's title */
    public String getTitle() { return title; }

    /** @param title the title to set */
    public void setTitle(String title) { this.title = title; }

    /** @return the textbook's author */
    public String getAuthor() { return author; }

    /** @param author the author to set */
    public void setAuthor(String author) { this.author = author; }

    /** @return the textbook's edition */
    public String getEdition() { return edition; }

    /** @param edition the edition to set */
    public void setEdition(String edition) { this.edition = edition; }

    /** @return the textbook's publisher */
    public String getPublisher() { return publisher; }

    /** @param publisher the publisher to set */
    public void setPublisher(String publisher) { this.publisher = publisher; }
}