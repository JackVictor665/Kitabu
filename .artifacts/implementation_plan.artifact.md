# Kitabu Library Management Application - Implementation Plan

Complete Android application implementation for the Kitabu Library Management system based on assignment instructions (`MADB372_26_S2_SF1_Instructions.pdf`).

## User Review Required

> [!IMPORTANT]
> - Room database uses KSP for annotation processing.
> - Navigation Compose and Lifecycle ViewModel Compose (`collectAsStateWithLifecycle`) will be integrated.
> - Pre-populated sample book data will be added via Room callback on database creation.

## Proposed Changes

### Build & Dependencies
#### [MODIFY] [build.gradle.kts](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/build.gradle.kts) & [app/build.gradle.kts](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/app/build.gradle.kts) & [libs.versions.toml](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/gradle/libs.versions.toml)
- Add KSP plugin, Room dependencies (`runtime`, `compiler`, `ktx`), Navigation Compose, and Lifecycle ViewModel Compose.

### Data Layer (`com.example.kitabu.data.local`)
#### [NEW] [BookingStatus.kt](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/app/src/main/java/com/example/kitabu/data/local/BookingStatus.kt)
- Enum with values: `PENDING`, `ACTIVE`, `RETURNED`.

#### [NEW] [BookEntity.kt](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/app/src/main/java/com/example/kitabu/data/local/BookEntity.kt)
- Room entity for books (`bookId`, `title`, `author`, `category`, `isAvailable`).

#### [NEW] [BookingEntity.kt](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/app/src/main/java/com/example/kitabu/data/local/BookingEntity.kt)
- Room entity for bookings (`bookingId`, `bookOwnerId`, `userName`, `bookingDate`, `returnDeadline`, `status`) with Foreign Key and CASCADE delete.

#### [NEW] [Converters.kt](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/app/src/main/java/com/example/kitabu/data/local/Converters.kt)
- Type converters for `BookingStatus` enum.

#### [NEW] [LibraryDao.kt](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/app/src/main/java/com/example/kitabu/data/local/LibraryDao.kt)
- Full CRUD operations: `insertBook`, `insertBooking`, `getAvailableBooks`, `searchBooks`, `getActiveBookingsWithBookInfo`, `extendRentalPeriod`, `processReturn`, `cancelPendingBooking`.

#### [NEW] [BookingWithBook.kt](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/app/src/main/java/com/example/kitabu/data/local/BookingWithBook.kt)
- Relation / POJO for joined query of Booking and Book info.

#### [NEW] [AppDatabase.kt](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/app/src/main/java/com/example/kitabu/data/local/AppDatabase.kt)
- Room database singleton with pre-population callback and `fallbackToDestructiveMigration()`.

### Repository & ViewModels (`com.example.kitabu.data` & `com.example.kitabu.ui`)
#### [NEW] [LibraryRepository.kt](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/app/src/main/java/com/example/kitabu/data/LibraryRepository.kt)
- Repository encapsulating DAO interactions.

#### [NEW] [CatalogViewModel.kt](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/app/src/main/java/com/example/kitabu/ui/catalog/CatalogViewModel.kt)
- Manages search query state, filtered books Flow, and reservation creation.

#### [NEW] [DashboardViewModel.kt](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/app/src/main/java/com/example/kitabu/ui/dashboard/DashboardViewModel.kt)
- Manages active reservations, renew, return, and cancel actions.

### UI & Navigation (`com.example.kitabu.ui`)
#### [NEW] [CatalogScreen.kt](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/app/src/main/java/com/example/kitabu/ui/catalog/CatalogScreen.kt)
- Search bar / OutlinedTextField, LazyColumn with book cards, availability badges, and reservation dialog/bottom sheet.

#### [NEW] [DashboardScreen.kt](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/app/src/main/java/com/example/kitabu/ui/dashboard/DashboardScreen.kt)
- Active reservations list, days remaining calculation, Renew/Return/Cancel action buttons.

#### [NEW] [Navigation.kt](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/app/src/main/java/com/example/kitabu/ui/navigation/Navigation.kt) & [MainActivity.kt](file:///C:/Users/jackt/AndroidStudioProjects/Kitabu/app/src/main/java/com/example/kitabu/MainActivity.kt)
- NavHost connecting Catalog and Dashboard screens with bottom navigation bar.

## Verification Plan

### Automated Tests
- Build project successfully using `./gradlew assembleDebug`.

### Manual Verification
- Review UI and database reactivity on emulator.
