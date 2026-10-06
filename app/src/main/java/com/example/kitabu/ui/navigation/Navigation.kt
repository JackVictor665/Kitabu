package com.example.kitabu.ui.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kitabu.ui.catalog.CatalogScreen
import com.example.kitabu.ui.catalog.CatalogViewModel
import com.example.kitabu.ui.dashboard.DashboardScreen
import com.example.kitabu.ui.dashboard.DashboardViewModel
import com.example.kitabu.ui.theme.ThemeMode

val LightPurpleBar = Color(0xFF6439BD)

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Catalog : Screen("catalog", "Catalog", Icons.Default.Home)
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.List)
}

@Composable
fun KitabuNavGraph(
    catalogViewModel: CatalogViewModel,
    dashboardViewModel: DashboardViewModel,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val items = listOf(Screen.Catalog, Screen.Dashboard)

    val isLightMode = themeMode == ThemeMode.LIGHT || (themeMode == ThemeMode.SYSTEM && !isSystemInDarkTheme())
    val barColor = if (isLightMode) LightPurpleBar else MaterialTheme.colorScheme.primaryContainer

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = barColor,
                contentColor = Color.White
            ) {
                val navBackStackEntry = navController.currentBackStackEntryAsState().value
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.tertiary,
                            selectedTextColor = MaterialTheme.colorScheme.tertiary,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f),
                            indicatorColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Catalog.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Catalog.route) {
                CatalogScreen(
                    viewModel = catalogViewModel,
                    themeMode = themeMode,
                    onToggleTheme = onToggleTheme
                )
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    themeMode = themeMode,
                    onToggleTheme = onToggleTheme
                )
            }
        }
    }
}
