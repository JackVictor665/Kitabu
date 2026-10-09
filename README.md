[README.md](https://github.com/user-attachments/files/33248048/README.md)
# 📚 Kitabu – Library Book Rental & Reservation System

Kitabu (Swahili for "book") is a native Android app built for **MADB372 – Mobile Application Development B (STADIO)**. It lets students browse a library catalogue, reserve available titles, and manage their rentals (renew, return or cancel) with return deadlines tracked in real time. Everything is stored locally, so it works fully offline.

## ✨ Features

- **Catalogue:** browse available and borrowed books, with green "Available" and amber "Borrowed" badges, and search by title or author.
- **Reserve:** tap an available book and choose a rental duration in a bottom sheet.
- **Dashboard:** view active reservations with the reservation date, return deadline and days remaining, then **renew**, **return** or **cancel** them.
- **Reactive UI:** badges and lists update immediately when the database changes.

## 🛠️ Tech Stack & Architecture

Kotlin · Jetpack Compose (Material 3) · Navigation Compose · Room (SQLite) with KSP · Coroutines & Flow · Coil

The app follows **MVVM** with a single activity: `ui/` (Compose screens, ViewModels, navigation) → `data/LibraryRepository` (validation and business logic) → `data/local/` (Room entities, DAO, type converters and database). Room queries return `Flow`, which the ViewModels expose as `StateFlow` and the screens collect with `collectAsStateWithLifecycle()`. All writes run in `viewModelScope.launch`, so nothing touches the main thread. Reserve, return and cancel are atomic `@Transaction` operations across the `books` and `bookings` tables (linked by a foreign key with `CASCADE` delete).

## 🚀 Getting Started

1. Clone the repo: `git clone <your-repo-url>`
2. Open the project in **Android Studio** (a recent stable version) with **JDK 17+**.
3. Let Gradle sync, then run the `app` configuration on an emulator or a device (Android 7.0 / API 24 or higher).

The database is pre-populated with sample books on first launch. If you change an entity, bump the database version or uninstall the app (`fallbackToDestructiveMigration()` is enabled for development).

## 📸 Screenshots

| Catalogue | Reserve | Dashboard |
|:---:|:---:|:---:|
| ![Catalogue](docs/screenshots/catalogue.png) | ![Reserve](docs/screenshots/reserve.png) | ![Dashboard](docs/screenshots/dashboard.png) |

## 🗂️ Extras

A SQL reference of the schema and CRUD queries is in [`docs/Kitabu_MADB372_SF1.sql`](docs/Kitabu_MADB372_SF1.sql). It is for documentation only, because Room creates the real database.

---
