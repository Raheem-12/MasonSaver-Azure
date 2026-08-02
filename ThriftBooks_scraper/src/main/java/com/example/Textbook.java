package com.example;

import java.util.ArrayList;

/*
    this class will represent the following methods:
        - isbn
        - price
        - condition (good, new)
 */
public class Textbook {
    private String isbn;
    private double price;
    private String condition;
    private String title;
    private ArrayList<String> author;
    private String publisher;
    private String url;

    /**
     * Full constructor with all textbook fields, used by ThriftScraper
     * once a complete listing has been scraped and parsed.
     */
    public Textbook(String isbn, double price, String condition, String title,
                     ArrayList<String> author, String publisher, String url) {
        this.isbn = isbn;
        this.price = price;
        this.condition = condition;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.url = url;
    }

    /**
     * Simplified constructor used by SearchResults when parsing individual
     * copy listings from a search response, where only isbn, price, and
     * condition are available per copy (title/author/publisher/url are
     * shared at the work level, not per copy).
     */
    public Textbook(String isbn, double price, String condition) {
        this(isbn, price, condition, null, null, null, null);
    }

    /**
     * Checks whether this Textbook has a non-null, non-empty ISBN.
     * Used by SearchResults to filter out malformed entries before
     * adding them to the results list.
     *
     * @return true if isbn is present and not blank, false otherwise
     */
    public boolean validIsbn() {
        return isbn != null && !isbn.trim().isEmpty();
    }

    public String getIsbn() {
        return isbn;
    }

    public double getPrice() {
        return price;
    }

    public String getCondition() {
        return condition;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getURL() {
        return url;
    }

    @Override
    public String toString() {
        return String.format("%s | $%.2f | %s", isbn, price, condition);
    }
}