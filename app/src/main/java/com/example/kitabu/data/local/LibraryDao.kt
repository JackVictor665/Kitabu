package com.example.kitabu.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {

    // ---------- CREATE ----------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    // ---------- READ ----------
    /** Whole catalogue (available AND borrowed) for the catalog screen. */
    @Query("SELECT * FROM books ORDER BY title")
    fun getAllBooks(): Flow<List<BookEntity>>

    /** Books filtered by availability. */
    @Query("SELECT * FROM books WHERE isAvailable = :available ORDER BY title")
    fun getBooksByAvailability(available: Boolean): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isAvailable = 1 ORDER BY title")
    fun getAvailableBooks(): Flow<List<BookEntity>>

    /** Search by title or author across the whole catalogue. */
    @Query(
        "SELECT * FROM books " +
            "WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' " +
            "ORDER BY title"
    )
    fun searchBooks(query: String): Flow<List<BookEntity>>

    /** Active/pending reservations with their book (Room performs the join via @Relation). */
    @Transaction
    @Query("SELECT * FROM bookings WHERE status != 'RETURNED' ORDER BY returnDeadline ASC")
    fun getActiveBookingsWithBookInfo(): Flow<List<BookingWithBook>>

    // ---------- UPDATE ----------
    @Query(
        "UPDATE bookings SET returnDeadline = :newDeadline " +
            "WHERE bookingId = :bookingId AND status != 'RETURNED'"
    )
    suspend fun extendRentalPeriod(bookingId: Long, newDeadline: Long)

    @Query("UPDATE bookings SET status = 'RETURNED' WHERE bookingId = :bookingId")
    suspend fun updateBookingStatusToReturned(bookingId: Long)

    @Query("UPDATE books SET isAvailable = :isAvailable WHERE bookId = :bookId")
    suspend fun updateBookAvailability(bookId: Int, isAvailable: Boolean)

    // ---------- DELETE ----------
    /** Returns the number of rows deleted (0 if already returned / not found). */
    @Query("DELETE FROM bookings WHERE bookingId = :bookingId AND status != 'RETURNED'")
    suspend fun cancelPendingBooking(bookingId: Long): Int

    // ---------- ATOMIC LIFECYCLE OPERATIONS ----------
    /** Record the booking and mark the book unavailable - both succeed or both roll back. */
    @Transaction
    suspend fun reserveBook(booking: BookingEntity) {
        insertBooking(booking)
        updateBookAvailability(booking.bookOwnerId, false)
    }

    @Transaction
    suspend fun processReturn(bookingId: Long, bookId: Int) {
        updateBookingStatusToReturned(bookingId)
        updateBookAvailability(bookId, true)
    }

    @Transaction
    suspend fun cancelBooking(bookingId: Long, bookId: Int) {
        if (cancelPendingBooking(bookingId) > 0) {
            updateBookAvailability(bookId, true)
        }
    }
}
