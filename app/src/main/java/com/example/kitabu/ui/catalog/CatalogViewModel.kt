package com.example.kitabu.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kitabu.data.LibraryRepository
import com.example.kitabu.data.local.BookEntity
import com.example.kitabu.data.local.BookingEntity
import com.example.kitabu.data.local.BookingStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CatalogViewModel(private val repository: LibraryRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val books: StateFlow<List<BookEntity>> = _searchQuery
        .flatMapLatest { query ->
            repository.searchBooks(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun reserveBook(book: BookEntity, userName: String, rentalDays: Int) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val deadlineTime = currentTime + (rentalDays * 24 * 60 * 60 * 1000L)
            val booking = BookingEntity(
                bookOwnerId = book.bookId,
                userName = userName.ifBlank { "Guest User" },
                bookingDate = currentTime,
                returnDeadline = deadlineTime,
                status = BookingStatus.ACTIVE
            )
            repository.insertBooking(booking)
            repository.updateBookAvailability(book.bookId, false)
        }
    }

    companion object {
        fun provideFactory(repository: LibraryRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CatalogViewModel(repository) as T
                }
            }
    }
}
