package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import com.example.Textbook;

/*
  this class will iterate through each textbook listing
  and find:
        - lowestCostGood
        - lowestCostNew
    will use an ArrayList to store
    parses a JSON response from the thriftbooks API and
    can find cheapest new and used copies from results
*/
public class SearchResults {

    /* Stores all textbook copies parsed from JSON response from thriftbooks API */
    private ArrayList<Textbook> textbooks = new ArrayList<>();

    /* Shared JSON parser used to read the JSON string */
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Constructor that takes a JSON string and parses it to populate the textbooks list.
     * @param json the raw JSON string from API response
     * @throws Exception if JSON parsing fails
     */
    public SearchResults(String json) throws Exception {
        JsonNode root = mapper.readTree(json);
        JsonNode edition = root.path("Work").path("ActiveEdition");
        String isbn = edition.path("ISBN").asText();
        for (JsonNode copy : edition.path("Copies")) {
            double price     = copy.path("Price").asDouble();
            String condition = copy.path("Quality").asText();

            Textbook tb = new Textbook(isbn, price, condition);
            if (tb.validIsbn()) {
                textbooks.add(tb);
            }
        }
    }

    /**
     * Returns the full list of parsed textbooks
     *
     * @return ArrayList of all Textbook copies
     */
    public ArrayList<Textbook> getTextbooks() {
        return textbooks;
    }

    /**
     * Finds the cheapest new condition textbook.
     *
     * @return the lowest priced new Textbook, or null if none exist
     */
    public Textbook lowestCostNew() {
        Textbook lowest = null;
        for (Textbook tb : textbooks) {
            if (tb.getCondition().equalsIgnoreCase("New")) {
                if (lowest == null || tb.getPrice() < lowest.getPrice()) {
                    lowest = tb;
                }
            }
        }
        return lowest;
    }

    /**
     * Finds the cheapest used condition textbook.
     * Includes any condition that is not "New" (Very Good, Good, Acceptable).
     *
     * @return the lowest priced used Textbook, or null if none exist
     */
    public Textbook lowestCostGood() {
        Textbook lowest = null;
        for (Textbook tb : textbooks) {
            if (!tb.getCondition().equalsIgnoreCase("New")) {
                if (lowest == null || tb.getPrice() < lowest.getPrice()) {
                    lowest = tb;
                }
            }
        }
        return lowest;
    }

    /**
     * Returns a readable summary of all textbooks in the list.
     *
     * @return formatted string of all Textbook entries
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SearchResults:\n");
        for (Textbook tb : textbooks) {
            sb.append("  ").append(tb).append("\n");
        }
        return sb.toString();
    }
}