package com.example.kitabu.ui.catalog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.kitabu.data.local.BookEntity
import com.example.kitabu.ui.theme.EmeraldGreen
import com.example.kitabu.ui.theme.KitabuTheme
import com.example.kitabu.ui.theme.ThemeMode
import com.example.kitabu.ui.theme.WarmAmber
import kotlinx.coroutines.launch

val LightPurpleBar = Color(0xFF6439BD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val books by viewModel.books.collectAsStateWithLifecycle()

    var selectedBookForDetails by remember { mutableStateOf<BookEntity?>(null) }
    var selectedBookForReserve by remember { mutableStateOf<BookEntity?>(null) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showReserveDialog by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }
    var rentalDays by remember { mutableStateOf("7") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isLightMode = themeMode == ThemeMode.LIGHT || (themeMode == ThemeMode.SYSTEM && !isSystemInDarkTheme())
    val isDarkTheme = themeMode == ThemeMode.DARK || (themeMode == ThemeMode.SYSTEM && isSystemInDarkTheme())
    val appBarColor = if (isLightMode) LightPurpleBar else MaterialTheme.colorScheme.primaryContainer

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Bookshelf") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text("Search by title or author") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Featured Books Horizontal Row ("Your Bookshelf")
            if (books.isNotEmpty()) {
                Text(
                    text = "Featured Books",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(books.take(4), key = { "featured_${it.bookId}" }) { book ->
                        FeaturedBookCard(book = book) {
                            selectedBookForDetails = book
                            showDetailsDialog = true
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Categorized Book Listings (LazyColumn)
            if (books.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No books found", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                val booksByCategory = books.groupBy { it.category }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    booksByCategory.forEach { (category, categoryBooks) ->
                        item(key = "header_$category") {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 4.dp)
                            ) {
                                Text(
                                    text = category.uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                        items(categoryBooks, key = { it.bookId }) { book ->
                            BookCard(book = book) {
                                selectedBookForDetails = book
                                showDetailsDialog = true
                            }
                        }
                    }
                }
            }
        }
    }

    // Book Details Description Dialog
    if (showDetailsDialog && selectedBookForDetails != null) {
        BookDetailsDialog(
            book = selectedBookForDetails!!,
            isDark = isDarkTheme,
            onDismiss = { showDetailsDialog = false },
            onReserveClick = {
                showDetailsDialog = false
                selectedBookForReserve = selectedBookForDetails
                showReserveDialog = true
            }
        )
    }

    // Reservation Input Dialog
    if (showReserveDialog && selectedBookForReserve != null) {
        AlertDialog(
            onDismissRequest = { showReserveDialog = false },
            title = { Text("Take Out Book") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Book: ${selectedBookForReserve?.title}")
                    Text("Author: ${selectedBookForReserve?.author}")
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("Your Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        )
                    )
                    OutlinedTextField(
                        value = rentalDays,
                        onValueChange = { rentalDays = it },
                        label = { Text("Rental Duration (Days)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val days = rentalDays.toIntOrNull() ?: 7
                        selectedBookForReserve?.let { book ->
                            viewModel.reserveBook(book, userName, days)
                            scope.launch {
                                snackbarHostState.showSnackbar("Successfully took out '${book.title}' for $days days!")
                            }
                        }
                        showReserveDialog = false
                        userName = ""
                        rentalDays = "7"
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showReserveDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FeaturedBookCard(
    book: BookEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(200.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = book.coverImageUrl,
                contentDescription = book.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(MaterialTheme.shapes.small)
            )
            Text(
                text = book.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun BookCard(
    book: BookEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = book.coverImageUrl,
                contentDescription = book.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(56.dp, 72.dp)
                    .clip(MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = book.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Author: ${book.author}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Category: ${book.category}", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = if (book.isAvailable) EmeraldGreen else WarmAmber,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = if (book.isAvailable) "Available" else "Borrowed",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun BookDetailsDialog(
    book: BookEntity,
    isDark: Boolean,
    onDismiss: () -> Unit,
    onReserveClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(book.title, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = book.coverImageUrl,
                    contentDescription = book.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(MaterialTheme.shapes.medium)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Author: ${book.author}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Edition: ${book.edition}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Category: ${book.category}", style = MaterialTheme.typography.bodySmall)
                    }
                    Surface(
                        color = if (book.isAvailable) EmeraldGreen else WarmAmber,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = if (book.isAvailable) "Available" else "Borrowed",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                HorizontalDivider()

                Text(text = "Synopsis", style = MaterialTheme.typography.titleSmall)
                Text(text = book.synopsis, style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = {
            if (book.isAvailable) {
                Button(onClick = onReserveClick) {
                    Text("Take Out Book")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isDark) Color.White else MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Close")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BookCardPreview() {
    KitabuTheme {
        BookCard(
            book = BookEntity(
                bookId = 1,
                title = "Clean Code",
                author = "Robert C. Martin",
                category = "Programming",
                edition = "1st Edition",
                synopsis = "A guide to writing clean code.",
                coverImageUrl = "https://covers.openlibrary.org/b/id/7222246-L.jpg",
                isAvailable = true
            ),
            onClick = {}
        )
    }
}
