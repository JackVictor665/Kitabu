package com.example.kitabu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kitabu.data.LibraryRepository
import com.example.kitabu.data.local.AppDatabase
import com.example.kitabu.ui.catalog.CatalogViewModel
import com.example.kitabu.ui.dashboard.DashboardViewModel
import com.example.kitabu.ui.navigation.KitabuNavGraph
import com.example.kitabu.ui.theme.KitabuTheme
import com.example.kitabu.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { LibraryRepository(database.libraryDao()) }

    private val catalogViewModel: CatalogViewModel by viewModels {
        CatalogViewModel.provideFactory(repository)
    }

    private val dashboardViewModel: DashboardViewModel by viewModels {
        DashboardViewModel.provideFactory(repository)
    }

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
            KitabuTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KitabuNavGraph(
                        catalogViewModel = catalogViewModel,
                        dashboardViewModel = dashboardViewModel,
                        themeMode = themeMode,
                        onToggleTheme = { themeViewModel.toggleTheme() }
                    )
                }
            }
        }
    }
}
