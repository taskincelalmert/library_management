package com.example.library.unit;

import com.example.library.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UNIT TEST - Model Layer
 */
class BorrowRecordTest {

    private Book createSampleBook() {
        Book book = new Book("978-0-13-468599-1", "Clean Code", "Robert C. Martin", 3, Genre.TECHNOLOGY);
        book.setId(1L);
        return book;
    }

    private Member createSampleMember() {
        Member member = new Member("Alice", "alice@example.com", MembershipType.STANDARD);
        member.setId(1L);
        return member;
    }

    // =========================================================================
    // EXAMPLE: calculateFine() tests — filled in as reference
    // =========================================================================

    @Nested
    @DisplayName("calculateFine()")
    class CalculateFineTests {

        @Test
        @DisplayName("should return 0 when book is returned on time")
        void shouldReturnZeroFine_WhenReturnedOnTime() {
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            record.setReturnDate(record.getDueDate()); // returned exactly on due date

            assertEquals(0.0, record.calculateFine());
        }

        @Test
        @DisplayName("should return 0 when book is returned before due date")
        void shouldReturnZeroFine_WhenReturnedEarly() {
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            record.setReturnDate(record.getBorrowDate().plusDays(5)); // returned after 5 days

            assertEquals(0.0, record.calculateFine());
        }

        @Test
        @DisplayName("should calculate correct fine when returned 3 days late")
        void shouldCalculateCorrectFine_WhenReturnedLate() {
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            record.setReturnDate(record.getDueDate().plusDays(3)); // 3 days late

            double expectedFine = 3 * BorrowRecord.DAILY_FINE_RATE; // 3 * 1.50 = 4.50
            assertEquals(expectedFine, record.calculateFine());
        }

        @Test
        @DisplayName("should return 0 when book is not yet returned")
        void shouldReturnZeroFine_WhenNotYetReturned() {
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            // returnDate is null

            assertEquals(0.0, record.calculateFine());
        }
    }

    // =========================================================================
    // TODO: Students should write these tests
    // =========================================================================

    @Nested
    @DisplayName("isOverdue()")
    class IsOverdueTests {

        @Test
        @DisplayName("should return true when checked after due date and still borrowed")
        void shouldBeOverdue_WhenPastDueDateAndStillBorrowed() {
            // TODO: Create a BorrowRecord and check isOverdue() with a date after dueDate

            // Arrange
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            LocalDate oneDayLate = record.getDueDate().plusDays(1);

            // Act
            boolean overdue = record.isOverdue(oneDayLate);

            // Assert
            assertTrue(overdue);
        }

        @Test
        @DisplayName("should return false when checked before due date")
        void shouldNotBeOverdue_WhenBeforeDueDate() {
            // TODO: Create a BorrowRecord and check isOverdue() with a date before dueDate

            // Arrange
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            LocalDate oneDayEarly = record.getDueDate().minusDays(1);

            // Act
            boolean overdue = record.isOverdue(oneDayEarly);

            // Assert
            assertFalse(overdue);
        }

        @Test
        @DisplayName("should return false when book is already returned (even if past due)")
        void shouldNotBeOverdue_WhenAlreadyReturned() {
            // TODO: Create a BorrowRecord, set status to RETURNED,
            //       then check isOverdue() — should be false even if past due

            // Arrange
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            record.setStatus(BorrowStatus.RETURNED);
            LocalDate wayPastDue = record.getDueDate().plusDays(10);

            // Act
            boolean overdue = record.isOverdue(wayPastDue);

            // Assert
            assertFalse(overdue);
        }

        @Test
        @DisplayName("should return false on exactly the due date")
        void shouldNotBeOverdue_OnExactDueDate() {
            // TODO: Check isOverdue() when asOfDate == dueDate

            // Arrange
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            LocalDate exactDueDate = record.getDueDate();

            // Act
            boolean overdue = record.isOverdue(exactDueDate);

            // Assert
            assertFalse(overdue);
        }
    }

    @Nested
    @DisplayName("Constructor / default values")
    class ConstructorTests {

        @Test
        @DisplayName("should set borrow date to today")
        void shouldSetBorrowDateToToday() {
            // TODO: Verify that new BorrowRecord sets borrowDate to LocalDate.now()

            // Arrange
            LocalDate today = LocalDate.now();

            // Act
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            LocalDate actualBorrowDate = record.getBorrowDate();

            // Assert
            assertEquals(today, actualBorrowDate);
        }

        @Test
        @DisplayName("should set due date to 14 days from today")
        void shouldSetDueDateTo14DaysFromToday() {
            // TODO: Verify dueDate = borrowDate + STANDARD_BORROW_DAYS

            // Arrange
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());

            // Act
            LocalDate expectedDueDate = record.getBorrowDate().plusDays(BorrowRecord.STANDARD_BORROW_DAYS);
            LocalDate actualDueDate = record.getDueDate();

            // Assert
            assertEquals(expectedDueDate, actualDueDate);
        }

        @Test
        @DisplayName("should set status to BORROWED")
        void shouldSetStatusToBorrowed() {
            // TODO: Verify default status is BORROWED

            // Arrange
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());

            // Act
            BorrowStatus actualStatus = record.getStatus();

            // Assert
            assertEquals(BorrowStatus.BORROWED, actualStatus);
        }
    }
}
