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
            record.setReturnDate(record.getDueDate());

            assertEquals(0.0, record.calculateFine());
        }

        @Test
        @DisplayName("should return 0 when book is returned before due date")
        void shouldReturnZeroFine_WhenReturnedEarly() {
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            record.setReturnDate(record.getBorrowDate().plusDays(5));

            assertEquals(0.0, record.calculateFine());
        }

        @Test
        @DisplayName("should calculate correct fine when returned 3 days late")
        void shouldCalculateCorrectFine_WhenReturnedLate() {
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            record.setReturnDate(record.getDueDate().plusDays(3));

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
    // isOverdue() tests
    // =========================================================================

    @Nested
    @DisplayName("isOverdue()")
    class IsOverdueTests {

        @Test
        @DisplayName("should return true when checked after due date and still borrowed")
        void shouldBeOverdue_WhenPastDueDateAndStillBorrowed() {
            // Arrange
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            // Status defaults to BORROWED; check one day after the due date
            LocalDate oneDayLate = record.getDueDate().plusDays(1);

            // Act & Assert
            assertTrue(record.isOverdue(oneDayLate));
        }

        @Test
        @DisplayName("should return false when checked before due date")
        void shouldNotBeOverdue_WhenBeforeDueDate() {
            // Arrange
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            LocalDate beforeDue = record.getDueDate().minusDays(1);

            // Act & Assert
            assertFalse(record.isOverdue(beforeDue));
        }

        @Test
        @DisplayName("should return false when book is already returned (even if past due)")
        void shouldNotBeOverdue_WhenAlreadyReturned() {
            // Arrange
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            record.setStatus(BorrowStatus.RETURNED);
            LocalDate wellPastDue = record.getDueDate().plusDays(10);

            // Act & Assert
            // isOverdue() requires status == BORROWED, so a returned book is never overdue
            assertFalse(record.isOverdue(wellPastDue));
        }

        @Test
        @DisplayName("should return false on exactly the due date")
        void shouldNotBeOverdue_OnExactDueDate() {
            // Arrange
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            // isOverdue uses isAfter(dueDate), so the due date itself is NOT overdue
            LocalDate exactDueDate = record.getDueDate();

            // Act & Assert
            assertFalse(record.isOverdue(exactDueDate));
        }
    }

    // =========================================================================
    // Constructor / default values tests
    // =========================================================================

    @Nested
    @DisplayName("Constructor / default values")
    class ConstructorTests {

        @Test
        @DisplayName("should set borrow date to today")
        void shouldSetBorrowDateToToday() {
            // Arrange & Act
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());

            // Assert
            assertEquals(LocalDate.now(), record.getBorrowDate());
        }

        @Test
        @DisplayName("should set due date to 14 days from today")
        void shouldSetDueDateTo14DaysFromToday() {
            // Arrange & Act
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());

            // Assert: dueDate must be exactly STANDARD_BORROW_DAYS after borrowDate
            LocalDate expectedDueDate = LocalDate.now().plusDays(BorrowRecord.STANDARD_BORROW_DAYS);
            assertEquals(expectedDueDate, record.getDueDate());
        }

        @Test
        @DisplayName("should set status to BORROWED")
        void shouldSetStatusToBorrowed() {
            // Arrange & Act
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());

            // Assert
            assertEquals(BorrowStatus.BORROWED, record.getStatus());
        }
    }
}
