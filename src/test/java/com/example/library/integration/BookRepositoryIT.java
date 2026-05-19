package com.example.library.integration;

import com.example.library.model.Book;
import com.example.library.model.Genre;
import com.example.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * INTEGRATION TEST - Repository Layer
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    private Book createBook(String isbn, String title, String author, int copies, Genre genre) {
        Book book = new Book(isbn, title, author, copies, genre);
        book.setPublishedDate(LocalDate.of(2020, 1, 1));
        return bookRepository.save(book);
    }

    // =========================================================================
    // EXAMPLE: Basic CRUD and custom query tests — filled in
    // =========================================================================

    @Nested
    @DisplayName("Basic CRUD operations")
    class CrudTests {

        @Test
        @DisplayName("should save and retrieve a book by ID")
        void shouldSaveAndFindById() {
            Book saved = createBook("978-0-13-468599-1", "Clean Code", "Robert C. Martin", 3, Genre.TECHNOLOGY);

            Optional<Book> found = bookRepository.findById(saved.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getTitle()).isEqualTo("Clean Code");
            assertThat(found.get().getIsbn()).isEqualTo("978-0-13-468599-1");
        }

        @Test
        @DisplayName("should find book by ISBN")
        void shouldFindByIsbn() {
            createBook("978-0-13-468599-1", "Clean Code", "Robert C. Martin", 3, Genre.TECHNOLOGY);

            Optional<Book> found = bookRepository.findByIsbn("978-0-13-468599-1");

            assertThat(found).isPresent();
            assertThat(found.get().getTitle()).isEqualTo("Clean Code");
        }

        @Test
        @DisplayName("should return empty when ISBN not found")
        void shouldReturnEmpty_WhenIsbnNotFound() {
            Optional<Book> found = bookRepository.findByIsbn("non-existent");

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("Custom query methods")
    class CustomQueryTests {

        @Test
        @DisplayName("should search books by keyword in title or author (case insensitive)")
        void shouldSearchByKeyword() {
            createBook("978-1", "Clean Code", "Robert C. Martin", 3, Genre.TECHNOLOGY);
            createBook("978-2", "Clean Architecture", "Robert C. Martin", 2, Genre.TECHNOLOGY);
            createBook("978-3", "Design Patterns", "Gang of Four", 5, Genre.TECHNOLOGY);

            List<Book> results = bookRepository.searchBooks("clean");

            assertThat(results).hasSize(2);
            assertThat(results).extracting(Book::getTitle)
                    .containsExactlyInAnyOrder("Clean Code", "Clean Architecture");
        }

        @Test
        @DisplayName("should find available books (copies > 0)")
        void shouldFindAvailableBooks() {
            Book available = createBook("978-1", "Available Book", "Author A", 3, Genre.FICTION);
            Book unavailable = createBook("978-2", "Unavailable Book", "Author B", 1, Genre.FICTION);
            unavailable.setAvailableCopies(0);
            bookRepository.save(unavailable);

            List<Book> results = bookRepository.findAvailableBooks();

            assertThat(results).hasSize(1);
            assertThat(results.get(0).getTitle()).isEqualTo("Available Book");
        }
    }

    // =========================================================================
    // TODO: Students should write these integration tests
    // =========================================================================

    @Nested
    @DisplayName("Genre and author queries")
    class FilterTests {

        @Test
        @DisplayName("should find books by genre")
        void shouldFindByGenre() {
            // TODO: Save books of different genres
            //       Query by Genre.SCIENCE and verify only matching books are returned

            // Arrange
            createBook("978-0-14-045511-3", "The Republic", "Plato", 2, Genre.PHILOSOPHY);
            createBook("978-0-14-044928-0", "Apology", "Plato", 1, Genre.PHILOSOPHY);
            createBook("978-0-19-282890-3", "The Last Day of a Condemned Man", "Victor Hugo", 3, Genre.FICTION);
            createBook("978-1-86197-278-1", "The 48 Laws of Power", "Robert Greene", 4, Genre.NON_FICTION);

            // Act
            List<Book> results = bookRepository.findByGenre(Genre.PHILOSOPHY);

            // Assert
            assertThat(results).hasSize(2);
            assertThat(results).extracting(Book::getTitle)
                    .containsExactlyInAnyOrder("The Republic", "Apology");
        }

        @Test
        @DisplayName("should find books by author (case insensitive, partial match)")
        void shouldFindByAuthor() {
            // TODO: Save books by different authors
            //       Search by partial author name and verify results

            // Arrange
            createBook("978-0-14-045511-3", "The Republic", "Plato", 2, Genre.PHILOSOPHY);
            createBook("978-0-14-044928-0", "Apology", "Plato", 1, Genre.PHILOSOPHY);
            createBook("978-0-19-282890-3", "The Last Day of a Condemned Man", "Victor Hugo", 3, Genre.FICTION);
            createBook("978-1-86197-278-1", "The 48 Laws of Power", "Robert Greene", 4, Genre.NON_FICTION);

            // Act - partial substring + uppercase query against mixed-case author "Plato"
            List<Book> results = bookRepository.findByAuthorContainingIgnoreCase("LATO");

            // Assert
            assertThat(results).hasSize(2);
            assertThat(results).extracting(Book::getAuthor)
                    .containsExactlyInAnyOrder("Plato", "Plato");
        }

        @Test
        @DisplayName("should search by author name using searchBooks()")
        void shouldSearchByAuthorKeyword() {
            // TODO: Use searchBooks() with an author name as keyword
            //       Verify it finds books by that author

            // Arrange
            createBook("978-0-14-045511-3", "The Republic", "Plato", 2, Genre.PHILOSOPHY);
            createBook("978-0-19-282890-3", "The Last Day of a Condemned Man", "Victor Hugo", 3, Genre.FICTION);
            createBook("978-1-86197-278-1", "The 48 Laws of Power", "Robert Greene", 4, Genre.NON_FICTION);

            // Act
            List<Book> results = bookRepository.searchBooks("Greene");

            // Assert
            assertThat(results).hasSize(1);
            Book foundBook = results.get(0);
            assertThat(foundBook.getTitle()).isEqualTo("The 48 Laws of Power");
        }

        @Test
        @DisplayName("should return empty list when no books match search")
        void shouldReturnEmpty_WhenNoMatch() {
            // TODO: Search for a keyword that matches nothing

            // Arrange
            createBook("978-0-14-045511-3", "The Republic", "Plato", 2, Genre.PHILOSOPHY);
            createBook("978-0-19-282890-3", "The Last Day of a Condemned Man", "Victor Hugo", 3, Genre.FICTION);

            // Act
            List<Book> results = bookRepository.searchBooks("nonexistent-keyword");

            // Assert
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("should enforce unique ISBN constraint")
        void shouldEnforceUniqueIsbn() {
            // TODO: Try to save two books with the same ISBN
            //       Verify a DataIntegrityViolationException is thrown
            //       Hint: Use assertThrows() and flush the persistence context

            // Arrange
            String sharedIsbn = "978-0-14-045511-3";
            createBook(sharedIsbn, "The Republic", "Plato", 2, Genre.PHILOSOPHY);
            Book duplicate = new Book(sharedIsbn, "Another Book", "Another Author", 1, Genre.FICTION);

            // Act + Assert - saveAndFlush forces the DB constraint check
            assertThatThrownBy(() -> bookRepository.saveAndFlush(duplicate))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("should handle deleting a book")
        void shouldDeleteBook() {
            // TODO: Save a book, delete it, verify it's gone

            // Arrange
            Book saved = createBook("978-1-86197-278-1", "The 48 Laws of Power", "Robert Greene", 4, Genre.NON_FICTION);
            Long bookId = saved.getId();

            // Act
            bookRepository.deleteById(bookId);

            // Assert
            Optional<Book> found = bookRepository.findById(bookId);
            assertThat(found).isEmpty();
        }
    }
}
