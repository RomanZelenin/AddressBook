package com.romazelenin.addressbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.romazelenin.addressbook.screen.DetailsScreen
import com.romazelenin.addressbook.screen.ErrorScreen
import com.romazelenin.addressbook.screen.PersonsScreen
import com.romazelenin.addressbook.ui.theme.AddressBookTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterialNavigationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AddressBookTheme {
                val bottomSheetNavigator = rememberBottomSheetNavigator()
                val navController = rememberNavController(bottomSheetNavigator)

                ModalBottomSheetLayout(
                    bottomSheetNavigator,
                    sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ) {
                    var selectedSortedState by rememberSaveable { mutableStateOf(true) }

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
                        bottomSheet("sorting") {
                            LaunchedEffect(bottomSheetNavigator){
                                viewModel.sortedUsers(Sort.alphabet)
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.sheet_line),
                                    contentDescription = null
                                )
                                Text(
                                    text = getString(R.string.sorting),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedSortedState,
                                        onClick = {
                                            selectedSortedState = true
                                            viewModel.sortedUsers(Sort.alphabet)
                                        },
                                    )
                                    Text(text = "По алфавиту", color = Color.Black)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = !selectedSortedState,
                                        onClick = {
                                            selectedSortedState = false
                                            viewModel.sortedUsers(Sort.birthaday)
                                        })
                                    Text(text = "По дню рождения", color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

