package com.romazelenin.addressbook.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.romazelenin.addressbook.MainViewModel
import com.romazelenin.addressbook.R
import com.romazelenin.addressbook.ui.theme.Purple500

@Composable
fun ErrorScreen(navController: NavController, viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(54.dp),
            painter = painterResource(id = R.drawable.flying_saucer),
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.crash),
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            fontSize = 17.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.fast_fix),
            color = Color.LightGray,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            modifier = Modifier.clickable {
                viewModel.refresh()
                navController.popBackStack()
            },
            text = stringResource(R.string.repeat_attempt),
            color = Purple500,
            fontSize = 16.sp
        )
    }
}