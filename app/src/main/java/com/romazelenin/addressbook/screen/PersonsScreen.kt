package com.romazelenin.addressbook.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.romazelenin.addressbook.MainViewModel
import com.romazelenin.addressbook.R
import com.romazelenin.addressbook.domain.entity.Department
import com.romazelenin.addressbook.domain.entity.State
import com.romazelenin.addressbook.domain.entity.User
import com.romazelenin.addressbook.ui.theme.AddressBookTheme
import com.romazelenin.addressbook.ui.theme.Gray
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun PersonsScreen(navController: NavController, viewModel: MainViewModel) {
    val pagerState = rememberPagerState()
    val focusRequester = remember { FocusRequester() }

    val context = LocalContext.current
    val pages = remember {
        listOf(
            Department.all to context.getString(R.string.all),
            Department.android to context.getString(R.string.android),
            Department.design to context.getString(R.string.design),
            Department.analytics to context.getString(R.string.analytics),
            Department.backend to context.getString(R.string.backend),
            Department.back_office to context.getString(R.string.back_office),
            Department.frontend to context.getString(R.string.frontend),
            Department.hr to context.getString(R.string.hr),
            Department.ios to context.getString(R.string.ios),
            Department.management to context.getString(R.string.management),
            Department.pr to context.getString(R.string.pr),
            Department.qa to context.getString(R.string.qa),
            Department.support to context.getString(R.string.support)
        )
    }

    val users by viewModel.users.collectAsState(initial = State.Loading())

    Scaffold(topBar = {
        Column {
            TopAppBar() {

                var searchIsFocused by remember { mutableStateOf(false) }
                var query by remember { mutableStateOf("") }

                Box(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        shape = RoundedCornerShape(36.dp)
                    ) {}
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusEvent {
                                searchIsFocused = it.isFocused
                            },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.search),
                                contentDescription = null,
                                tint = if (searchIsFocused) Color.Black else Gray
                            )
                        },
                        trailingIcon = {
                            if (!searchIsFocused) {
                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(
                                        modifier = Modifier.offset(y = 5.dp),
                                        painter = painterResource(id = R.drawable.sorted_list),
                                        contentDescription = null
                                    )
                                }
                            } else {
                                if (query.isNotEmpty()) {
                                    IconButton(onClick = { query = "" }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_baseline_clear_24),
                                            contentDescription = null,
                                            tint = Color.LightGray
                                        )
                                    }
                                }
                            }
                        },
                        placeholder = { Text(text = stringResource(R.string.search_placeholder_text)) },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.Black,
                            backgroundColor = Color.Transparent,
                            cursorColor = MaterialTheme.colors.onPrimary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        singleLine = true,
                        value = query,
                        onValueChange = { query = it.trimStart() }
                    )
                }
            }

            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    )
                }) {
                val scope = rememberCoroutineScope()
                pages.forEachIndexed { index, department ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch { pagerState.scrollToPage(page = index) }
                        },
                        selectedContentColor = Color.Black,
                        unselectedContentColor = Gray
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = department.second,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }) {
        val swipeRefreshState = rememberSwipeRefreshState(users is State.Loading)

        HorizontalPager(
            count = pages.size,
            state = pagerState
        ) { page ->
            SwipeRefresh(state = swipeRefreshState, onRefresh = { viewModel.refresh() }) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    when (users) {
                        is State.Failed -> {
                            navController.navigate("error") {
                                launchSingleTop = true
                            }
                        }
                        is State.Loading -> {
                            /*  var visibilityShimmer = true
                              items(5) {
                                   ListItem(
                                       icon = {
                                           AsyncImage(
                                               modifier = Modifier.placeholder(
                                                   visible = visibilityShimmer,
                                                   highlight = PlaceholderHighlight.shimmer(),
                                               ),
                                               model = null,
                                               placeholder = painterResource(id = R.drawable.ic_baseline_person_outline_24),
                                               error = painterResource(id = R.drawable.ic_baseline_person_outline_24),
                                               contentDescription = null
                                           )
                                       },
                                       secondaryText = {
                                           Text(
                                               modifier = Modifier.placeholder(
                                                   visible = visibilityShimmer,
                                                   highlight = PlaceholderHighlight.shimmer(),
                                               ),
                                               text = "Content to display",
                                               color = Color.Gray
                                           )
                                       }
                                   ) {
                                       Row(verticalAlignment = Alignment.CenterVertically) {
                                           Text(
                                               modifier = Modifier.placeholder(
                                                   visible = visibilityShimmer,
                                                   highlight = PlaceholderHighlight.shimmer(),
                                               ),
                                               text = "Content to display after content has loaded",
                                               color = Color.Black
                                           )
                                           Spacer(modifier = Modifier.width(2.dp))
                                           Text(
                                               modifier = Modifier.placeholder(
                                                   visible = visibilityShimmer,
                                                   highlight = PlaceholderHighlight.shimmer(),
                                               ),
                                               text = "Content to display after content has loaded",
                                               color = Color.LightGray,
                                               fontSize = 12.sp
                                           )
                                       }

                                   }

                              }*/
                        }
                        is State.Success -> {
                            val filteredUsers = if (pages[page].first != Department.all) {
                                (users as State.Success<List<User>>).data.filter { it.department == pages[page].first }
                            } else {
                                (users as State.Success<List<User>>).data
                            }
                            items(filteredUsers) {
                                ListItem(
                                    modifier = Modifier.clickable { navController.navigate("details/${it.id}") },
                                    icon = {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(it.avatarUrl)
                                                .crossfade(true)
                                                .build(),
                                            placeholder = painterResource(id = R.drawable.ic_baseline_person_outline_24),
                                            error = painterResource(id = R.drawable.ic_baseline_person_outline_24),
                                            contentDescription = null
                                        )
                                    },
                                    secondaryText = {
                                        Text(
                                            text = it.position,
                                            color = Color.Gray
                                        )
                                    }
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${it.firstName} ${it.lastName}",
                                            color = Color.Black
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = it.userTag.lowercase(),
                                            color = Color.LightGray,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PersonsScreenPreview() {
    AddressBookTheme {
        //PersonsScreen()
    }
}