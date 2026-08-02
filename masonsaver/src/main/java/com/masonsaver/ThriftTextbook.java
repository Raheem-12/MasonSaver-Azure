package com.masonsaver;

import java.util.ArrayList;

/**
 * ThriftTextbook
 *
 * Represents a textbook entry scraped from ThriftBooks.
 * Stores ISBN, price, condition, title, authors, publisher, and listing URL.
 *
 * Named ThriftTextbook (rather than Textbook) to avoid a naming conflict
 * with com.masonsaver.model.Textbook, which is the JPA entity used by
 * the main application database.
 */
public class ThriftTextbook {

    private String isbn;
    private double price;
    private String condition;
    private String title;
    private ArrayList<String> author;
    private String publisher;
    private String url;

    /**
     * Constructs a new ThriftTextbook with all scraped fields.
     *
     * @param isbn      the textbook's ISBN
     * @param price     the lowest price found for this listing
     * @param condition the condition of the book (e.g. "New", "Good")
     * @param title     the textbook's title
     * @param author    a list of author names
     * @param publisher the textbook's publisher
     * @param url       the URL of the ThriftBooks listing
     */
    public ThriftTextbook(String isbn, double price, String condition, String title,
                           ArrayList<String> author, String publisher, String url) {
        this.isbn = isbn;
        this.price = price;
        this.condition = condition;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.url = url;
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
}