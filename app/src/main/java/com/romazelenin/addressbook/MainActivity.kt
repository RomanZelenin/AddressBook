package com.romazelenin.addressbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.romazelenin.addressbook.screen.NavigationScreen
import com.romazelenin.addressbook.ui.theme.AddressBookTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AddressBookTheme {
                NavigationScreen(viewModel = viewModel)
            }
        }
    }
}

