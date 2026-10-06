package com.example.kitabu.ui.dashboard

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kitabu.data.local.BookEntity
import com.example.kitabu.data.local.BookingEntity
import com.example.kitabu.data.local.BookingStatus
import com.example.kitabu.data.local.BookingWithBook
import com.example.kitabu.ui.theme.KitabuTheme
import com.example.kitabu.ui.theme.RoseRed
import com.example.kitabu.ui.theme.ThemeMode
import com.example.kitabu.ui.theme.WarmAmber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch

val LightPurpleBar = Color(0xFF6439BD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeBookings by viewModel.activeBookings.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isLightMode = (themeMode == ThemeMode.LIGHT) || ((themeMode == ThemeMode.SYSTEM) && !isSystemInDarkTheme())
    val appBarColor = if (isLightMode) LightPurpleBar else MaterialTheme.colorScheme.primaryContainer

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Reservations Dashboard") },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (themeMode == ThemeMode.DARK) Icons.Default.WbSunny else Icons.Default.NightsStay,
                            contentDescription = "Toggle Theme",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appBarColor,
                    titleContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        if (activeBookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No active reservations", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activeBookings, key = { it.booking.bookingId }) { item ->
                    ReservationCard(
                        bookingWithBook = item,
                        onRenew = { bookingId, deadline ->
                            viewModel.renewRental(bookingId, deadline)
                            scope.launch {
                                snackbarHostState.showSnackbar("Rental renewed successfully!")
                            }
                        },
                        onReturn = { bookingId, bookId ->
                            viewModel.returnBook(bookingId, bookId)
                            scope.launch {
                                snackbarHostState.showSnackbar("Book returned successfully!")
                            }
                        },
                        onCancel = { bookingId, bookId ->
                            viewModel.cancelBooking(bookingId, bookId)
                            scope.launch {
                                snackbarHostState.showSnackbar("Booking cancelled successfully!")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ReservationCard(
    bookingWithBook: BookingWithBook,
    onRenew: (Long, Long) -> Unit,
    onReturn: (Long, Int) -> Unit,
    onCancel: (Long, Int) -> Unit
) {
    val booking = bookingWithBook.booking
    val book = bookingWithBook.book

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val bookingDateStr = dateFormat.format(Date(booking.bookingDate))
    val deadlineStr = dateFormat.format(Date(booking.returnDeadline))

    val currentTime = System.currentTimeMillis()
    val diffMillis = booking.returnDeadline - currentTime
    val daysRemaining = TimeUnit.MILLISECONDS.toDays(diffMillis)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = book.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Reserved by: ${booking.userName}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Booked on: $bookingDateStr", style = MaterialTheme.typography.bodySmall)
            Text(text = "Return Deadline: $deadlineStr", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (daysRemaining >= 0) "Days remaining: $daysRemaining" else "Overdue by ${-daysRemaining} days!",
                color = if (daysRemaining >= 0) WarmAmber else RoseRed,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onRenew(booking.bookingId, booking.returnDeadline) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Renew")
                }
                Button(
                    onClick = { onReturn(booking.bookingId, book.bookId) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Return")
                }
                OutlinedButton(
                    onClick = { onCancel(booking.bookingId, book.bookId) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseRed),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReservationCardPreview() {
    KitabuTheme {
        ReservationCard(
            bookingWithBook = BookingWithBook(
                booking = BookingEntity(
                    bookingId = 1L,
                    bookOwnerId = 1,
                    userName = "John Doe",
                    bookingDate = System.currentTimeMillis(),
                    returnDeadline = System.currentTimeMillis() + 86400000L * 7,
                    status = BookingStatus.ACTIVE
                ),
                book = BookEntity(
                    bookId = 1,
                    title = "Clean Code",
                    author = "Robert C. Martin",
                    category = "Programming",
                    isAvailable = false
                )
            ),
            onRenew = { _, _ -> },
            onReturn = { _, _ -> },
            onCancel = { _, _ -> }
        )
    }
}
