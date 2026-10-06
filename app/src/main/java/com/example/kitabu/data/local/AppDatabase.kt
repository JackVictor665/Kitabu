package com.example.kitabu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [BookEntity::class, BookingEntity::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun libraryDao(): LibraryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kitabu_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateSampleData(database.libraryDao())
                    }
                }
            }

            private suspend fun populateSampleData(dao: LibraryDao) {
                dao.insertBook(BookEntity(
                    title = "Clean Code",
                    author = "Robert C. Martin",
                    category = "Programming",
                    edition = "1st Edition (2008)",
                    synopsis = "Even bad code can function. But if code isn't clean, it can bring a development organization to its knees. Every year, countless hours and significant resources are lost because of poorly written code. But it doesn't have to be that way.",
                    coverImageUrl = "https://covers.openlibrary.org/b/id/15126503-L.jpg",
                    isAvailable = true
                ))
                dao.insertBook(BookEntity(
                    title = "The Pragmatic Programmer",
                    author = "Andrew Hunt & David Thomas",
                    category = "Programming",
                    edition = "20th Anniversary Edition",
                    synopsis = "Straight from the programming trenches, The Pragmatic Programmer cuts through the increasing specialty and technicalism of modern software development to examine the core process—taking a requirement and producing working, maintainable code that delights its users.",
                    coverImageUrl = "https://covers.openlibrary.org/b/id/15136784-M.jpg",
                    isAvailable = true
                ))
                dao.insertBook(BookEntity(
                    title = "Atomic Habits",
                    author = "James Clear",
                    category = "Self-Help",
                    edition = "1st Edition (2018)",
                    synopsis = "No matter your goals, Atomic Habits offers a proven framework for improving—every day. James Clear, one of the world's leading experts on habit formation, reveals practical strategies that will teach you exactly how to form good habits, break bad ones, and master the tiny behaviors that lead to remarkable results.",
                    coverImageUrl = "https://covers.openlibrary.org/b/id/15217381-M.jpg",
                    isAvailable = true
                ))
                dao.insertBook(BookEntity(
                    title = "To Kill a Mockingbird",
                    author = "Harper Lee",
                    category = "Fiction",
                    edition = "Classic Paperback Edition",
                    synopsis = "The unforgettable novel of a childhood in a sleepy Southern town and the crisis of conscience that rocked it. 'To Kill a Mockingbird' became both an instant bestseller and a critical success when it was published in 1960.",
                    coverImageUrl = "https://covers.openlibrary.org/b/id/14817209-M.jpg",
                    isAvailable = true
                ))
                dao.insertBook(BookEntity(
                    title = "1984",
                    author = "George Orwell",
                    category = "Science Fiction",
                    edition = "Penguin Modern Classics",
                    synopsis = "Written in 1948, 1984 was George Orwell's chilling prophecy about the future. And while 1984 has come and gone, his dystopian vision of a government that controls all information and surveillance is more relevant now than ever.",
                    coverImageUrl = "https://covers.openlibrary.org/b/id/15255315-M.jpg",
                    isAvailable = true
                ))
            }
        }
    }
}
