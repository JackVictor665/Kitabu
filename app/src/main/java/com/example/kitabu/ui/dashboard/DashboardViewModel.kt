package com.example.kitabu.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kitabu.data.LibraryRepository
import com.example.kitabu.data.local.BookingWithBook
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: LibraryRepository) : ViewModel() {

    val activeBookings: StateFlow<List<BookingWithBook>> = repository.activeBookings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun renewRental(bookingId: Long, currentDeadline: Long, extraDays: Int = 7) {
        viewModelScope.launch {
            val newDeadline = currentDeadline + (extraDays * 24 * 60 * 60 * 1000L)
            repository.extendRentalPeriod(bookingId, newDeadline)
        }
    }

    fun returnBook(bookingId: Long, bookId: Int) {
        viewModelScope.launch {
            repository.processReturn(bookingId, bookId)
        }
    }

    fun cancelBooking(bookingId: Long, bookId: Int) {
        viewModelScope.launch {
            repository.cancelPendingBooking(bookingId)
            repository.updateBookAvailability(bookId, true)
        }
    }

    companion object {
        fun provideFactory(repository: LibraryRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DashboardViewModel(repository) as T
                }
            }
    }
}
