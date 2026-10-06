# Kitabu Library Management Application - Requirements Audit

Detailed check-off of all project requirements from `MADB372_26_S2_SF1_Instructions.pdf`.

## Phase 1: Project Setup & Dependencies
- `[x]` **Gradle Setup (`build.gradle.kts`, `libs.versions.toml`)**:
  - KSP plugin enabled.
  - Room dependencies (`room-runtime`, `room-compiler`, `room-ktx`) added.
  - Navigation Compose and Lifecycle ViewModel Compose (`collectAsStateWithLifecycle`) added.
- `[x]` **Project Package Structure**:
  - Organized packages: `data/local/`, `data/`, `ui/catalog/`, `ui/dashboard/`, `ui/navigation/`, `ui/theme/` ensuring clean MVVM separation.

## Phase 2: Data Layer & Business Logic (Section B - 40 Marks)
- `[x]` **Data Entities (`data/local/`)**:
  - `BookEntity`: `bookId` (`@PrimaryKey(autoGenerate = true)`), `title`, `author`, `category`, `isAvailable` (Default: `true`).
  - `BookingEntity`: `bookingId` (`@PrimaryKey(autoGenerate = true)`), `bookOwnerId` (Foreign Key targeting `BookEntity.bookId` with `CASCADE` delete), `userName`, `bookingDate`, `returnDeadline`, `status` (`BookingStatus` Enum: `PENDING`, `ACTIVE`, `RETURNED`).
- `[x]` **Converters & DAOs (`data/local/`)**:
  - `Converters.kt`: Maps `BookingStatus` enum to String primitives.
  - `LibraryDao.kt` (Full CRUD Operations):
    - Create: `insertBook()`, `insertBooking()`.
    - Read: `getAvailableBooks()`, `searchBooks(query: String)` using SQL `LIKE` queries (`%query%`), `getActiveBookingsWithBookInfo()` joining `BookingEntity` and `BookEntity` (`BookingWithBook`).
    - Update: `extendRentalPeriod()`, `updateBookingStatusToReturned()`, `updateBookAvailability()`.
    - Delete: `cancelPendingBooking()`.
- `[x]` **Database Initialization (`AppDatabase.kt`)**:
  - Thread-safe Singleton using `Room.databaseBuilder`.
  - Sample book data pre-population callback on database creation.
  - Configured `fallbackToDestructiveMigration()`.

## Phase 3: Repository & ViewModels (Section C - 15 Marks)
- `[x]` **Repository Layer (`LibraryRepository.kt`)**:
  - Encapsulates DAO calls and provides clean data streams to ViewModels.
- `[x]` **CatalogViewModel**:
  - Exposes `searchQuery` state and filtered books list using `StateFlow` (`stateIn` with `SharingStarted.WhileSubscribed(5000)`).
  - Handles reservation actions inside `viewModelScope.launch`.
- `[x]` **DashboardViewModel**:
  - Exposes active user reservations (`activeBookings`).
  - Implements functions for **Renew**, **Return**, and **Cancel** actions using asynchronous Coroutines.

## Phase 4: UI Layout & Navigation Design (Section A - 40 Marks)
- `[x]` **Book Catalog Screen**:
  - Top bar with `OutlinedTextField` for dynamic title/author filtering with search type matching.
  - `LazyColumn` displaying book cards featuring brown book preview thumbnails, titles, authors, categories, and badges (Emerald Green for "Available" and Warm Amber for "Borrowed").
  - Interactive Dialog triggering on book click to input user name (capitalization & next IME action) and rental duration (numeric keypad & done IME action) to complete reservation with success snackbar notification.
- `[x]` **User Reservations Dashboard Screen**:
  - `LazyColumn` listing active rentals.
  - Displays book details, reservation date, return deadline, and dynamic calculation of **days remaining** (with overdue warnings in Rose Red).
  - **Action Buttons**: **Renew**, **Return**, and **Cancel** with success snackbar notifications.
- `[x]` **Navigation Compose & Reactive State**:
  - Implemented `NavHost` with screens for Catalog and Dashboard.
  - Used `collectAsStateWithLifecycle()` on screens for reactive UI updates.
  - Added manual Light/Dark mode toggle switch in top app bars.
  - Styled with Deep Navy, Muted Sage, Vibrant Coral, and Light Purple bars in light mode.

## Phase 5: Testing, Validation & Documentation
- `[x]` **Input Validation & Sanity Checks**:
  - Input type matching applied to all text/number fields.
  - Database transactions executed off the Main Thread using Coroutines.
- `[x]` **Build & Verification**:
  - Application builds successfully on Android Emulator (API 26+) with Gradle build success.
