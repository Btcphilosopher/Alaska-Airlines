package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.components.BoardingPassModal
import com.example.screens.*
import com.example.state.AppTab
import com.example.state.AppViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.AlaskaDeepBlue
import com.example.ui.theme.AlaskaIceBlue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: AppViewModel = viewModel()
                val currentTab by viewModel.currentTab.collectAsState()
                
                var showBoardingPassModal by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AppBottomNavigationBar(
                            selectedTab = currentTab,
                            onTabSelected = { viewModel.currentTab.value = it }
                        )
                    },
                    contentWindowInsets = WindowInsets.navigationBars
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        when (currentTab) {
                            AppTab.HOME -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateToTab = { viewModel.currentTab.value = it },
                                onShowBoardingPass = { showBoardingPassModal = true },
                                onShowTripDetails = { viewModel.currentTab.value = AppTab.TRIPS }
                            )
                            AppTab.TRIPS -> TripsScreen(
                                viewModel = viewModel,
                                onShowBoardingPass = { showBoardingPassModal = true }
                            )
                            AppTab.BOOK -> BookScreen(
                                viewModel = viewModel,
                                onShowBoardingPass = { showBoardingPassModal = true }
                            )
                            AppTab.EXPLORE -> ExploreScreen(
                                viewModel = viewModel
                            )
                            AppTab.ACCOUNT -> AccountScreen(
                                viewModel = viewModel
                            )
                        }

                        // Digital Boarding Pass Modal Dialog Overlay
                        if (showBoardingPassModal) {
                            BoardingPassModal(
                                viewModel = viewModel,
                                onDismiss = { showBoardingPassModal = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    NavigationBar(
        containerColor = AlaskaDeepBlue,
        contentColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("app_bottom_nav_bar")
    ) {
        NavigationBarItem(
            selected = selectedTab == AppTab.HOME,
            onClick = { onTabSelected(AppTab.HOME) },
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            label = { Text("HOME", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AlaskaDeepBlue,
                unselectedIconColor = AlaskaIceBlue,
                selectedTextColor = Color.White,
                unselectedTextColor = AlaskaIceBlue,
                indicatorColor = AlaskaIceBlue
            ),
            modifier = Modifier.testTag("nav_item_home")
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.TRIPS,
            onClick = { onTabSelected(AppTab.TRIPS) },
            icon = { Icon(imageVector = Icons.Default.AirplaneTicket, contentDescription = "Trips") },
            label = { Text("TRIPS", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AlaskaDeepBlue,
                unselectedIconColor = AlaskaIceBlue,
                selectedTextColor = Color.White,
                unselectedTextColor = AlaskaIceBlue,
                indicatorColor = AlaskaIceBlue
            ),
            modifier = Modifier.testTag("nav_item_trips")
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.BOOK,
            onClick = { onTabSelected(AppTab.BOOK) },
            icon = { Icon(imageVector = Icons.Default.BookOnline, contentDescription = "Book") },
            label = { Text("BOOK", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AlaskaDeepBlue,
                unselectedIconColor = AlaskaIceBlue,
                selectedTextColor = Color.White,
                unselectedTextColor = AlaskaIceBlue,
                indicatorColor = AlaskaIceBlue
            ),
            modifier = Modifier.testTag("nav_item_book")
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.EXPLORE,
            onClick = { onTabSelected(AppTab.EXPLORE) },
            icon = { Icon(imageVector = Icons.Default.TravelExplore, contentDescription = "Explore") },
            label = { Text("EXPLORE", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AlaskaDeepBlue,
                unselectedIconColor = AlaskaIceBlue,
                selectedTextColor = Color.White,
                unselectedTextColor = AlaskaIceBlue,
                indicatorColor = AlaskaIceBlue
            ),
            modifier = Modifier.testTag("nav_item_explore")
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.ACCOUNT,
            onClick = { onTabSelected(AppTab.ACCOUNT) },
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Account") },
            label = { Text("ACCOUNT", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AlaskaDeepBlue,
                unselectedIconColor = AlaskaIceBlue,
                selectedTextColor = Color.White,
                unselectedTextColor = AlaskaIceBlue,
                indicatorColor = AlaskaIceBlue
            ),
            modifier = Modifier.testTag("nav_item_account")
        )
    }
}
