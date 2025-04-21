package com.example.equipment_management_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.equipment_management_system.ui.theme.Equipment_Management_SystemTheme



class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            // Track the current navigation destination (Reactive)
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val previousStackEntry = navController.previousBackStackEntry
            val previousRoute =previousStackEntry?.destination?.route
            // Map routes to readable titles
            val previousScreenTitle = when {
                previousRoute == "Highlight_Equipment" -> "Highlighted Equipment"
                previousRoute == "search" -> "Search Equipment"
                previousRoute == "user" -> "User Profile"
                previousRoute == "register" -> "Register Account"
                previousRoute == "location" -> "Locations"
                previousRoute?.startsWith("location/") == true -> "Location Details"
                previousRoute?.startsWith("equipments/") == true -> "Equipment Details"
                else -> "Equipment Management"
            }

            // Check if we can navigate up (not on start destination)
            val canNavigateBack = navController.previousBackStackEntry != null


            Equipment_Management_SystemTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            navigationIcon = {
                                if (canNavigateBack) {
                                    IconButton(onClick = { navController.navigateUp() }) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            },
                            title = {Text(previousScreenTitle)}
                        )
                    },
                    bottomBar = {BottomNavBar(navController)}
                ) { innerPadding ->
                    NavHost(
                        navController = navController, // reference to your nav controller
                        startDestination = "Highlight_Equipment", // default screen to display
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("Highlight_Equipment") { // composable for "home" screen
                            FeedScreen(navController)
                        }
                        composable("search"){
                            Search_Equipment(navController)
                        }
                        composable ("user" ){
                            loginInterface(navController)
                        }
                        composable("register"){
                            Register(navController)
                        }
                        composable("location/{location}"){backStackEntry->
                            LocationDetailScreen(navController,backStackEntry.arguments?.getString("location")?:"")

                        }
                        composable("location") {
                            LocationsScreen(navController)
                        }
                        composable("equipments/{id}/{rented}") { backStackEntry ->
                            // Extract the department id from the NavBackStackEntry's arguments
                            Equipment_Detail( navController,backStackEntry.arguments?.getString("id")?:"",
                                backStackEntry.arguments?.getBoolean("rented") == true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    // Track the current navigation destination (Reactive)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define a list of navigation items with their routes
    val navigationItems = listOf(
        Pair("Highlighted Equipments", "Highlight_Equipment"),
        Pair("Location", "location"),
        Pair("Search", "search"),
        Pair("User", "user")
    )

    NavigationBar {
        navigationItems.forEachIndexed { index, (title, route) ->
            // Determine if this item is selected based on the current route
            val isSelected = when {
                currentRoute == route -> true
                // For detailed routes - handle detail pages correctly
                route == "location" && currentRoute?.startsWith("location/") == true -> true
                route == "Highlight_Equipment" && currentRoute?.startsWith("equipments/") == true -> true
                else -> false
            }

            NavigationBarItem(
                icon = { Icon(Icons.Filled.Favorite, contentDescription = title) },
                label = { Text(title) },
                selected = isSelected,
                onClick = {
                    if (currentRoute != route) {
                        // IMPORTANT: Remove the popUpTo logic which is breaking your back stack
                        navController.navigate(route) {
                            // Only use launchSingleTop to avoid duplicate destinations
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}
//@Composable
//fun BottomNavBar(navController: NavController) {
//    // Define a list of navigation items
//    val items = listOf("Highlighted Equipments", "Location", "Search", "User")
//
//    var selectedItem by remember { mutableIntStateOf(0) }
//
//    NavigationBar {
//        // Iterate over the items list and create a NavigationBarItem for each item
//        items.forEachIndexed { index, item ->
//            NavigationBarItem(
//                icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
//                label = { Text(item) },
//                selected = selectedItem == index,
//                onClick = { selectedItem = index
//                    when (index) {
//                        0 -> navController.navigate("Highlight_Equipment")
//                        // NavHostComposable
//                        1 -> navController.navigate("location")
//                        2 -> navController.navigate("search")
//                        3 -> navController.navigate("user")
//                    }
//                }
//            )
//        }
//    }
//}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Equipment_Management_SystemTheme {
        Greeting("Android")
    }
}