package com.romazelenin.addressbook.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.romazelenin.addressbook.R
import com.romazelenin.addressbook.ui.theme.AddressBookTheme
import com.romazelenin.addressbook.ui.theme.Gray

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun PersonsScreen(/*viewModel: MainViewModel*/) {
    val pagerState = rememberPagerState()
    val pages = stringArrayResource(id = R.array.departments)

    Scaffold(topBar = {
        Column {
            TopAppBar() {
                val focusRequester = remember { FocusRequester() }
                var searchIsFocused by remember { mutableStateOf(false) }
                var query by remember { mutableStateOf("") }

                Box(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp), shape = RoundedCornerShape(36.dp)
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
                pages.forEachIndexed { index, department ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            //pagerState.animateScrollToPage(page = index)
                        },
                        selectedContentColor = Color.Black,
                        unselectedContentColor = Gray
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = department,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }) {
        HorizontalPager(
            count = pages.size,
            state = pagerState
        ) { page ->

            Text(modifier = Modifier.fillMaxSize(), text = page.toString())

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PersonsScreenPreview() {
    AddressBookTheme {
        PersonsScreen()
    }
}