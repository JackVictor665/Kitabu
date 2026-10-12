package com.example.kitabu.data

import com.example.kitabu.data.local.BookEntity
import com.example.kitabu.data.local.BookingEntity
import com.example.kitabu.data.local.BookingStatus
import com.example.kitabu.data.local.BookingWithBook
import com.example.kitabu.data.local.LibraryDao
import kotlinx.coroutines.flow.Flow

class LibraryRepository(private val libraryDao: LibraryDao) {

    companion object {
        const val MAX_RENTAL_DAYS = 30
        private const val MAX_NAME_LENGTH = 50
        private const val DAY_MS = 24 * 60 * 60 * 1000L
    }

    /** Whole catalogue: available and borrowed books. */
    val allBooks: Flow<List<BookEntity>> = libraryDao.getAllBooks()
    val availableBooks: Flow<List<BookEntity>> = libraryDao.getAvailableBooks()
    val activeBookings: Flow<List<BookingWithBook>> = libraryDao.getActiveBookingsWithBookInfo()

    fun searchBooks(query: String): Flow<List<BookEntity>> {
        val clean = sanitize(query)
        return if (clean.isBlank()) libraryDao.getAllBooks() else libraryDao.searchBooks(clean)
    }

    suspend fun insertBook(book: BookEntity) {
        libraryDao.insertBook(book)
    }

    /**
     * Validates input, then reserves the book atomically.
     * @return false if the input was invalid (nothing is written).
     */
    suspend fun reserveBook(book: BookEntity, userName: String, rentalDays: Int): Boolean {
        if (!book.isAvailable || rentalDays !in 1..MAX_RENTAL_DAYS) return false
        val now = System.currentTimeMillis()
        val deadline = now + rentalDays * DAY_MS
        if (deadline <= now) return false

        libraryDao.reserveBook(
            BookingEntity(
                bookOwnerId = book.bookId,
                userName = sanitize(userName).take(MAX_NAME_LENGTH).ifBlank { "Guest User" },
                bookingDate = now,
                returnDeadline = deadline,
                status = BookingStatus.ACTIVE
            )
        )
        return true
    }

    /** Extends the deadline; the new deadline must be later than the current one. */
    suspend fun extendRentalPeriod(bookingId: Long, currentDeadline: Long, newDeadline: Long): Boolean {
        if (newDeadline <= currentDeadline) return false
        libraryDao.extendRentalPeriod(bookingId, newDeadline)
        return true
    }

    suspend fun processReturn(bookingId: Long, bookId: Int) {
        libraryDao.processReturn(bookingId, bookId)
    }

    suspend fun cancelPendingBooking(bookingId: Long, bookId: Int) {
        libraryDao.cancelBooking(bookingId, bookId)
    }

    /** Trim, collapse whitespace and strip control characters. */
    private fun sanitize(text: String): String =
        text.replace(Regex("\\p{Cntrl}"), " ").replace(Regex("\\s+"), " ").trim()
}
