package com.example.kitabu.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val bookId: Int = 0,
    val title: String,
    val author: String,
    val category: String,
    val edition: String = "1st Edition",
    val synopsis: String = "A compelling and educational read that provides deep insights into its subject matter.",
    val coverImageUrl: String = "https://covers.openlibrary.org/b/id/7222246-L.jpg",
    val isAvailable: Boolean = true
)
