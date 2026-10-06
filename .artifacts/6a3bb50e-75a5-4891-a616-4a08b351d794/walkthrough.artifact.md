# Kitabu Library Management Application - Walkthrough

I have successfully implemented the complete Kitabu Library Management Android Application adhering to all requirements in `MADB372_26_S2_SF1_Instructions.pdf`.

## What Was Accomplished

### 1. Build & Dependencies (`build.gradle.kts`, `libs.versions.toml`)
- Configured **KSP** plugin and Room compiler/runtime/ktx dependencies (`2.8.5`).
- Added **Navigation Compose** and **Lifecycle ViewModel Compose / Runtime Compose** (`2.8.7`).
- Configured Material Icons Extended for bottom navigation.

### 2. Data Layer (`data/local/`)
- **Entities**: `BookEntity` and `BookingEntity` (with Foreign Key targeting `BookEntity.bookId` with CASCADE delete).
- **Enum & Converters**: `BookingStatus` (`PENDING`, `ACTIVE`, `RETURNED`) with Room `TypeConverters`.
- **Relations**: `BookingWithBook` joining reservation and book information.
- **DAO**: `LibraryDao` providing full CRUD operations (`insertBook`, `insertBooking`, `getAvailableBooks`, `searchBooks`, `getActiveBookingsWithBookInfo`, `extendRentalPeriod`, `updateBookingStatusToReturned`, `updateBookAvailability`, `cancelPendingBooking`).
- **Database**: `AppDatabase` (thread-safe singleton with pre-population callback inserting sample books and fallback to destructive migration).

### 3. Repository & ViewModels (`data/`, `ui/`)
- **`LibraryRepository`**: Encapsulates DAO data streams and database actions.
- **`CatalogViewModel`**: Exposes search query state and filtered reactive book streams using `StateFlow` (`stateIn` with `WhileSubscribed(5000)`) and handles book reservations.
- **`DashboardViewModel`**: Exposes active user reservations and implements asynchronous Coroutine actions for **Renew**, **Return**, and **Cancel**.

### 4. UI Layout & Navigation (`ui/`)
- **`CatalogScreen`**: Features an `OutlinedTextField` search bar, `LazyColumn` of book cards with availability badges (Green for Available, Orange for Borrowed), and an interactive reservation dialog for inputting user name and rental duration.
- **`DashboardScreen`**: Features a `LazyColumn` of active rentals showing book details, reservation date, return deadline, dynamic calculation of days remaining, and action buttons (**Renew**, **Return**, **Cancel**).
- **`KitabuNavGraph` & `MainActivity`**: Implements bottom navigation bar switching between Catalog and Dashboard screens using `collectAsStateWithLifecycle()`.

## Validation Results

- **Automated Build**: Executed `./gradlew assembleDebug` successfully with build status **SUCCESS**.
