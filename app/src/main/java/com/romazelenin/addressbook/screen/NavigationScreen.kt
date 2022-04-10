package com.romazelenin.addressbook.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.romazelenin.addressbook.MainViewModel
import com.romazelenin.addressbook.R
import com.romazelenin.addressbook.Sort
import com.romazelenin.addressbook.ui.theme.Shapes

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun NavigationScreen(viewModel: MainViewModel) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    val context = LocalContext.current

    ModalBottomSheetLayout(
        bottomSheetNavigator,
        sheetShape = Shapes.large.copy(topStart = CornerSize(24.dp), topEnd = CornerSize(24.dp))
    ) {
        var selectedSortedState by rememberSaveable { mutableStateOf(true) }

        NavHost(navController = navController, startDestination = NavigatorDestenation.users.name) {
            composable(NavigatorDestenation.users.name) {
                PersonsScreen(navController = navController, viewModel = viewModel)
            }
            composable(NavigatorDestenation.error.name) {
                ErrorScreen(navController = navController, viewModel = viewModel)
            }
            composable(
                "${NavigatorDestenation.details.name}/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                DetailsScreen(
                    navController = navController,
                    userId = backStackEntry.arguments!!.getString("userId")!!,
                    viewModel = viewModel
                )
            }
            bottomSheet(NavigatorDestenation.sorting.name) {
                LaunchedEffect(bottomSheetNavigator) {
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
                        contentDescription = null,
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = context.getString(R.string.sorting),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedSortedState = true
                                viewModel.sortedUsers(Sort.alphabet)
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedSortedState,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = context.getString(R.string.alphabetically),
                            color = Color.Black
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedSortedState = false
                                viewModel.sortedUsers(Sort.birthaday)
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !selectedSortedState,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = context.getString(R.string.by_birthday),
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

enum class NavigatorDestenation{
    users, error, details, sorting
}