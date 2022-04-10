package com.romazelenin.addressbook.screen

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.romazelenin.addressbook.*
import com.romazelenin.addressbook.R
import com.romazelenin.addressbook.domain.entity.Department
import com.romazelenin.addressbook.domain.entity.State
import com.romazelenin.addressbook.domain.entity.User
import com.romazelenin.addressbook.ui.theme.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(
    ExperimentalPagerApi::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun PersonsScreen(navController: NavController, viewModel: MainViewModel) {

    val pagerState = rememberPagerState()
    val context = LocalContext.current
    val pages = viewModel.getDepartments()
    val users by viewModel.users.collectAsState(initial = State.Loading())
    val selectedSort by viewModel.getCurrentSort().collectAsState(initial = Sort.none)
    var query by remember { mutableStateOf("") }
    var searchFieldIsFocused by remember { mutableStateOf(false) }
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
                                        navController.navigate(NavigatorDestenation.sorting.name)
                                    }) {
                                        Icon(
                                            modifier = Modifier.offset(y = 5.dp),
                                            painter = painterResource(id = R.drawable.sorted_list),
                                            tint = if (selectedSort != Sort.none) MaterialTheme.colors.secondary else Color.Unspecified,
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
                                navController.navigate(NavigatorDestenation.error.name) {
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

                            if (filteredUsers.isEmpty()) {
                                item { EmptyResultPage() }
                            } else {
                                when (selectedSort) {
                                    Sort.birthaday -> {
                                        val dateNow = getNowDate()
                                        val indexNextYear = filteredUsers.indexOfFirst {
                                            val birthday = parsingDate(it.birthday)
                                            ((birthday.month <= dateNow.month && birthday.date < dateNow.date) || (birthday.month < dateNow.month))
                                        }
                                        itemsIndexed(filteredUsers) { index, item ->
                                            Column {
                                                if (indexNextYear == index) {
                                                    YearDivider(
                                                        modifier = Modifier.height(68.dp),
                                                        year = getNextYear().toString()
                                                    )
                                                }
                                                UserItem(
                                                    navController = navController,
                                                    user = item,
                                                    showBirthday = true
                                                )
                                            }
                                        }
                                    }
                                    Sort.alphabet -> {
                                        filteredUsers.groupBy { it.firstName.uppercase().first() }
                                            .forEach { entry ->
                                                stickyHeader {
                                                    Text(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(
                                                                horizontal = 16.dp,
                                                                vertical = 8.dp
                                                            ),
                                                        text = (entry.key.toString()),
                                                        textAlign = TextAlign.End,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                                itemsIndexed(entry.value) { index, item ->
                                                    UserItem(
                                                        navController = navController,
                                                        user = item
                                                    )
                                                }
                                            }
                                    }
                                    Sort.none -> {
                                        items(filteredUsers) {
                                            UserItem(navController = navController, user = it)
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
}

@Composable
fun YearDivider(modifier: Modifier = Modifier, year: String) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(
            modifier = Modifier.fillMaxWidth()
        ) {
            drawLine(
                cap = StrokeCap.Round,
                color = Color.LightGray,
                start = Offset(
                    x = 42f,
                    y = center.y
                ),
                end = Offset(
                    x = center.x / 2,
                    y = center.y
                ),
                strokeWidth = 6f
            )
            drawLine(
                cap = StrokeCap.Round,
                color = Color.LightGray,
                start = Offset(
                    x = center.x + center.x / 2,
                    y = center.y
                ),
                end = Offset(
                    x = size.width - 42f,
                    y = center.y
                ),
                strokeWidth = 6f
            )
        }
        Text(
            text = year,
            textAlign = TextAlign.Center,
            color = Color.LightGray
        )
    }
}

@Composable
private fun EmptyResultPage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(54.dp),
            painter = painterResource(id = R.drawable.left_pointing_magnifying_glass),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.empty_search_results),
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            fontSize = 17.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.correct_query),
            color = Color.LightGray,
            fontSize = 16.sp
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun UserItem(
    modifier: Modifier = Modifier,
    navController: NavController,
    user: User,
    showBirthday: Boolean = false
) {
    ListItem(
        modifier = modifier.clickable { navController.navigate("${NavigatorDestenation.details.name}/${user.id}") },
        icon = {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.avatarUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(id = R.drawable.ic_baseline_person_outline_24),
                error = painterResource(id = R.drawable.ic_baseline_person_outline_24),
                contentDescription = null
            )
        },
        secondaryText = {
            Text(
                text = user.position,
                color = Color.Gray
            )
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${user.firstName} ${user.lastName}",
                color = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = user.userTag.lowercase(),
                color = Color.LightGray,
                fontSize = 12.sp
            )
            if (showBirthday) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = formatDate(user.birthday, format = "d MMM"),
                    textAlign = TextAlign.End,
                    color = Color.Gray
                )
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