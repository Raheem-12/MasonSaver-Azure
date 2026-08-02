package com.masonsaver.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ModelTest
 *
 * Unit tests for the User and Textbook JPA entity classes.
 *
 * These are simple data holder classes, but every getter and setter
 * is independently verified here to ensure full statement and branch
 * coverage of the model package, since no method in either class
 * is unreachable or untestable with hardcoded input.
 *
 * Run with: mvn clean test
 */
public class ModelTest {

    /* ── User tests ── */

    /**
     * The no-argument constructor required by JPA should produce
     * a User with all fields null/unset.
     */
    @Test
    void user_noArgConstructor_createsEmptyUser() {
        User user = new User();

        assertNull(user.getId());
        assertNull(user.getFullName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
    }

    /**
     * The full constructor should correctly populate fullName,
     * email, and password.
     */
    @Test
    void user_fullConstructor_populatesAllFields() {
        User user = new User("Jane Smith", "jane@gmu.edu", "hashedPassword123");

        assertEquals("Jane Smith", user.getFullName());
        assertEquals("jane@gmu.edu", user.getEmail());
        assertEquals("hashedPassword123", user.getPassword());
    }

    /**
     * setFullName() should update the value returned by getFullName().
     */
    @Test
    void user_setFullName_updatesValue() {
        User user = new User();
        user.setFullName("New Name");

        assertEquals("New Name", user.getFullName());
    }

    /**
     * setEmail() should update the value returned by getEmail().
     */
    @Test
    void user_setEmail_updatesValue() {
        User user = new User();
        user.setEmail("updated@gmu.edu");

        assertEquals("updated@gmu.edu", user.getEmail());
    }

    /**
     * setPassword() should update the value returned by getPassword().
     */
    @Test
    void user_setPassword_updatesValue() {
        User user = new User();
        user.setPassword("newHashedPassword");

        assertEquals("newHashedPassword", user.getPassword());
    }

    /**
     * getId() should return null before the entity has been persisted,
     * since the ID is database-generated.
     */
    @Test
    void user_getId_isNullBeforePersistence() {
        User user = new User("Jane Smith", "jane@gmu.edu", "hash");

        assertNull(user.getId());
    }

    /* ── Textbook tests ── */

    /**
     * The no-argument constructor required by JPA should produce
     * a Textbook with all fields null/unset.
     */
    @Test
    void textbook_noArgConstructor_createsEmptyTextbook() {
        Textbook textbook = new Textbook();

        assertNull(textbook.getId());
        assertNull(textbook.getIsbn());
        assertNull(textbook.getTitle());
        assertNull(textbook.getAuthor());
        assertNull(textbook.getEdition());
        assertNull(textbook.getPublisher());
    }

    /**
     * The full constructor should correctly populate all five fields.
     */
    @Test
    void textbook_fullConstructor_populatesAllFields() {
        Textbook textbook = new Textbook(
            "978-0-13-468599-1", "Computer Networks", "Tanenbaum", "6th", "Pearson"
        );

        assertEquals("978-0-13-468599-1", textbook.getIsbn());
        assertEquals("Computer Networks", textbook.getTitle());
        assertEquals("Tanenbaum", textbook.getAuthor());
        assertEquals("6th", textbook.getEdition());
        assertEquals("Pearson", textbook.getPublisher());
    }

    /**
     * setIsbn() should update the value returned by getIsbn().
     */
    @Test
    void textbook_setIsbn_updatesValue() {
        Textbook textbook = new Textbook();
        textbook.setIsbn("111-2-22-333333-3");

        assertEquals("111-2-22-333333-3", textbook.getIsbn());
    }

    /**
     * setTitle() should update the value returned by getTitle().
     */
    @Test
    void textbook_setTitle_updatesValue() {
        Textbook textbook = new Textbook();
        textbook.setTitle("New Title");

        assertEquals("New Title", textbook.getTitle());
    }

    /**
     * setAuthor() should update the value returned by getAuthor().
     */
    @Test
    void textbook_setAuthor_updatesValue() {
        Textbook textbook = new Textbook();
        textbook.setAuthor("New Author");

        assertEquals("New Author", textbook.getAuthor());
    }

    /**
     * setEdition() should update the value returned by getEdition().
     */
    @Test
    void textbook_setEdition_updatesValue() {
        Textbook textbook = new Textbook();
        textbook.setEdition("3rd");

        assertEquals("3rd", textbook.getEdition());
    }

    /**
     * setPublisher() should update the value returned by getPublisher().
     */
    @Test
    void textbook_setPublisher_updatesValue() {
        Textbook textbook = new Textbook();
        textbook.setPublisher("McGraw Hill");

        assertEquals("McGraw Hill", textbook.getPublisher());
    }

    /**
     * getId() should return null before the entity has been persisted,
     * since the ID is database-generated.
     */
    @Test
    void textbook_getId_isNullBeforePersistence() {
        Textbook textbook = new Textbook(
            "978-0-13-468599-1", "Computer Networks", "Tanenbaum", "6th", "Pearson"
        );

        assertNull(textbook.getId());
    }

    /**
     * A Textbook constructed with a null publisher (allowed since the
     * field is nullable in the database) should correctly return null
     * from getPublisher() without throwing an exception.
     */
    @Test
    void textbook_withNullPublisher_isAllowed() {
        Textbook textbook = new Textbook(
            "978-0-13-468599-1", "Computer Networks", "Tanenbaum", "6th", null
        );

        assertNull(textbook.getPublisher());
    }
}