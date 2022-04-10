package com.romazelenin.addressbook.screen

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.romazelenin.addressbook.MainViewModel
import com.romazelenin.addressbook.R
import com.romazelenin.addressbook.domain.entity.Department
import com.romazelenin.addressbook.domain.entity.State
import com.romazelenin.addressbook.domain.entity.User
import com.romazelenin.addressbook.ui.theme.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun PersonsScreen(navController: NavController, viewModel: MainViewModel) {

    val pagerState = rememberPagerState()
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
    var query by remember { mutableStateOf("") }
    var searchFieldIsFocused by remember { mutableStateOf(false) }
    var sortingIsClicked by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    var isFirstStartSuccess = rememberSaveable { false }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    var job = remember<Job?> { null }
    var backgroundColorSnackbar by remember { mutableStateOf(Color.Red) }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(hostState = it) { data ->
                Snackbar(
                    modifier = Modifier,
                    snackbarData = data,
                    backgroundColor = backgroundColorSnackbar
                )
            }
        },
        topBar = {
            Column {
                TopAppBar {
                    Box(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(
                                    color = LightGray,
                                    shape = Shapes.small.copy(CornerSize(50))
                                )
                        )
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusEvent { searchFieldIsFocused = it.isFocused },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.search),
                                    tint = if (searchFieldIsFocused) MaterialTheme.colors.onSurface else Gray,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                if (!searchFieldIsFocused) {
                                    IconButton(onClick = {
                                        sortingIsClicked = true
                                        navController.navigate("sorting")
                                    }) {
                                        Icon(
                                            modifier = Modifier.offset(y = 5.dp),
                                            painter = painterResource(id = R.drawable.sorted_list),
                                            tint = if (sortingIsClicked) MaterialTheme.colors.secondary else Color.Unspecified,
                                            contentDescription = null
                                        )
                                    }
                                } else {
                                    if (query.isNotEmpty()) {
                                        IconButton(onClick = { query = "" }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_baseline_clear_24),
                                                contentDescription = null,
                                                tint = MaterialTheme.colors.onSurface
                                            )
                                        }
                                    }
                                }
                            },
                            placeholder = { Text(text = stringResource(R.string.search_placeholder_text)) },
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = MaterialTheme.colors.onSurface,
                                backgroundColor = Color.Transparent,
                                cursorColor = MaterialTheme.colors.onSurface,
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
                    pages.forEachIndexed { index, department ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch { pagerState.scrollToPage(page = index) }
                            },
                            selectedContentColor = MaterialTheme.colors.onSurface,
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
        HorizontalPager(
            modifier = Modifier.motionEventSpy {
                if (it.action == MotionEvent.ACTION_DOWN) {
                    focusManager.clearFocus()
                }
            },
            count = pages.size,
            state = pagerState
        ) { page ->
            SwipeRefresh(
                state = rememberSwipeRefreshState(users is State.Loading),
                onRefresh = { viewModel.refresh() }
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    when (users) {
                        is State.Failed -> {
                            if (!isFirstStartSuccess) {
                                navController.navigate("error") {
                                    launchSingleTop = true
                                }
                            } else {
                                job?.cancel()
                                job = scope.launch {
                                    backgroundColorSnackbar = Color.Red
                                    scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.failed_load_data))
                                }
                            }
                        }
                        is State.Loading -> {
                            if (isFirstStartSuccess) {
                                if (job?.isActive != true) {
                                    job = scope.launch {
                                        backgroundColorSnackbar = Purple500
                                        scaffoldState.snackbarHostState
                                            .showSnackbar(context.getString(R.string.wait_a_second))
                                    }
                                }
                            }
                            val visibilityShimmer = true
                            items(7) {
                                Row(modifier = Modifier.padding(16.dp)) {
                                    Icon(
                                        modifier = Modifier
                                            .placeholder(
                                                visible = visibilityShimmer,
                                                highlight = PlaceholderHighlight.shimmer(),
                                                color = LightGray,
                                                shape = Shapes.small
                                            )
                                            .size(36.dp),
                                        painter = painterResource(id = R.drawable.ic_baseline_person_outline_24),
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Row {
                                            Text(
                                                modifier = Modifier.placeholder(
                                                    visible = visibilityShimmer,
                                                    highlight = PlaceholderHighlight.shimmer(),
                                                    color = LightGray,
                                                    shape = Shapes.small
                                                ),
                                                text = "Roman Zelenin",
                                                color = Color.Black
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                modifier = Modifier.placeholder(
                                                    visible = visibilityShimmer,
                                                    highlight = PlaceholderHighlight.shimmer(),
                                                    color = LightGray,
                                                    shape = Shapes.small
                                                ),
                                                text = "rz",
                                                color = Color.LightGray,
                                                fontSize = 12.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            modifier = Modifier.placeholder(
                                                visible = visibilityShimmer,
                                                highlight = PlaceholderHighlight.shimmer(),
                                                color = LightGray,
                                                shape = Shapes.small
                                            ),
                                            text = "Developer",
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                        is State.Success -> {
                            isFirstStartSuccess = true
                            job?.cancel()

                            var filteredUsers = if (pages[page].first != Department.all) {
                                (users as State.Success<List<User>>).data.filter { it.department == pages[page].first }
                            } else {
                                (users as State.Success<List<User>>).data
                            }

                            if (query.isNotEmpty()) {
                                val clearedQuery = query.trimEnd()
                                filteredUsers = filteredUsers.filter {
                                    (it.firstName + " " + it.lastName).contains(
                                        clearedQuery,
                                        true
                                    ) || it.userTag.contains(clearedQuery, true)
                                }
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
                                            color = MaterialTheme.colors.onSurface
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