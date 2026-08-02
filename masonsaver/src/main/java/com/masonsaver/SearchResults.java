package com.masonsaver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;

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
    private ArrayList<ThriftTextbook> textbooks = new ArrayList<>();

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

            ThriftTextbook tb = new ThriftTextbook(isbn, price, condition, null, null, null, null);
            if (validIsbn(tb)) {
                textbooks.add(tb);
            }
        }
    }

    /**
     * Constructor for testing and for integration/system test classes that
     * need to build a SearchResults directly from a pre-built list of
     * ThriftTextbook objects (e.g. produced via ThriftScraper.generate_Entry())
     * without needing a real JSON response.
     *
     * Declared public (rather than package-private) so it can be used from
     * integration and system test classes that live in subpackages
     * (com.masonsaver.integration, com.masonsaver.system).
     *
     * @param textbooks a pre-built list of ThriftTextbook objects
     */
    public SearchResults(ArrayList<ThriftTextbook> textbooks) {
        this.textbooks = textbooks;
    }

    /**
     * Checks whether a given ThriftTextbook has a non-null, non-empty ISBN.
     * Used to filter out malformed entries before adding them to the results list.
     *
     * @param tb the ThriftTextbook to validate
     * @return true if isbn is present and not blank, false otherwise
     */
    private boolean validIsbn(ThriftTextbook tb) {
        String isbn = tb.getIsbn();
        return isbn != null && !isbn.trim().isEmpty();
    }

    /**
     * Returns the full list of parsed textbooks
     *
     * @return ArrayList of all ThriftTextbook copies
     */
    public ArrayList<ThriftTextbook> getTextbooks() {
        return textbooks;
    }

    /**
     * Finds the cheapest new condition textbook.
     *
     * @return the lowest priced new ThriftTextbook, or null if none exist
     */
    public ThriftTextbook lowestCostNew() {
        ThriftTextbook lowest = null;
        for (ThriftTextbook tb : textbooks) {
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
     * @return the lowest priced used ThriftTextbook, or null if none exist
     */
    public ThriftTextbook lowestCostGood() {
        ThriftTextbook lowest = null;
        for (ThriftTextbook tb : textbooks) {
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
     * @return formatted string of all ThriftTextbook entries
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SearchResults:\n");
        for (ThriftTextbook tb : textbooks) {
            sb.append("  ").append(tb.getIsbn())
              .append(" | $").append(tb.getPrice())
              .append(" | ").append(tb.getCondition())
              .append("\n");
        }
        return sb.toString();
    }
}