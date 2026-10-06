package com.example.kitabu.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    @Query("SELECT * FROM books WHERE isAvailable = 1")
    fun getAvailableBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun searchBooks(query: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM bookings WHERE status != 'RETURNED'")
    fun getActiveBookingsWithBookInfo(): Flow<List<BookingWithBook>>

    @Query("UPDATE bookings SET returnDeadline = :newDeadline WHERE bookingId = :bookingId")
    suspend fun extendRentalPeriod(bookingId: Long, newDeadline: Long)

    @Query("UPDATE bookings SET status = 'RETURNED' WHERE bookingId = :bookingId")
    suspend fun updateBookingStatusToReturned(bookingId: Long)

    @Query("UPDATE books SET isAvailable = :isAvailable WHERE bookId = :bookId")
    suspend fun updateBookAvailability(bookId: Int, isAvailable: Boolean)

    @Query("DELETE FROM bookings WHERE bookingId = :bookingId")
    suspend fun cancelPendingBooking(bookingId: Long)
}
