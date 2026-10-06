package com.example.kitabu.data

import com.example.kitabu.data.local.BookEntity
import com.example.kitabu.data.local.BookingEntity
import com.example.kitabu.data.local.BookingWithBook
import com.example.kitabu.data.local.LibraryDao
import kotlinx.coroutines.flow.Flow

class LibraryRepository(private val libraryDao: LibraryDao) {

    val availableBooks: Flow<List<BookEntity>> = libraryDao.getAvailableBooks()

    fun searchBooks(query: String): Flow<List<BookEntity>> {
        return if (query.isBlank()) {
            libraryDao.getAvailableBooks()
        } else {
            libraryDao.searchBooks(query)
        }
    }

    val activeBookings: Flow<List<BookingWithBook>> = libraryDao.getActiveBookingsWithBookInfo()

    suspend fun insertBook(book: BookEntity) {
        libraryDao.insertBook(book)
    }

    suspend fun insertBooking(booking: BookingEntity) {
        libraryDao.insertBooking(booking)
    }

    suspend fun updateBookAvailability(bookId: Int, isAvailable: Boolean) {
        libraryDao.updateBookAvailability(bookId, isAvailable)
    }

    suspend fun extendRentalPeriod(bookingId: Long, newDeadline: Long) {
        libraryDao.extendRentalPeriod(bookingId, newDeadline)
    }

    suspend fun processReturn(bookingId: Long, bookId: Int) {
        libraryDao.updateBookingStatusToReturned(bookingId)
        libraryDao.updateBookAvailability(bookId, true)
    }

    suspend fun cancelPendingBooking(bookingId: Long) {
        libraryDao.cancelPendingBooking(bookingId)
    }
}
