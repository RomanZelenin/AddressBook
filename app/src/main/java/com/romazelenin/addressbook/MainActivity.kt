package com.romazelenin.addressbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.romazelenin.addressbook.screen.DetailsScreen
import com.romazelenin.addressbook.screen.ErrorScreen
import com.romazelenin.addressbook.screen.PersonsScreen
import com.romazelenin.addressbook.ui.theme.AddressBookTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AddressBookTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "users") {
                    composable("users") {
                        PersonsScreen(navController = navController, viewModel = viewModel)
                    }
                    composable("error") {
                        ErrorScreen(navController = navController, viewModel = viewModel)
                    }
                    composable(
                        "details/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        DetailsScreen(
                            navController = navController,
                            userId = backStackEntry.arguments!!.getString("userId")!!,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

