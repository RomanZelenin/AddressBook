package com.romazelenin.addressbook.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.romazelenin.addressbook.R
import com.romazelenin.addressbook.ui.theme.AddressBookTheme

@Composable
fun DetailsScreen(navController: NavController) {
    Column() {
        Column(
            modifier = Modifier.background(MaterialTheme.colors.surface),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(elevation = 0.dp, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_left_24),
                        contentDescription = null
                    )
                }
            }, title = {}, backgroundColor = MaterialTheme.colors.surface)
            AsyncImage(
                modifier = Modifier.size(104.dp),
                model = "",
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.ic_baseline_person_outline_24),
                error = painterResource(id = R.drawable.ic_baseline_person_outline_24),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Алиса Иванова", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = "al", color = Color.LightGray, fontSize = 17.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Designer", fontSize = 13.sp)
            Spacer(modifier = Modifier.height(16.dp))
        }
        Row(modifier=Modifier.padding(16.dp)) {
            Icon(painter = painterResource(id = R.drawable.star), contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "1 мая 1991")
            Text(modifier = Modifier.weight(1f), text = "30 лет", textAlign = TextAlign.End)
        }
        Row(modifier=Modifier.padding(16.dp)) {
            Icon(painter = painterResource(id = R.drawable.phone), contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "+7 (999) 209 88 64")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DetailsScreenPreview() {
    AddressBookTheme {
        DetailsScreen(rememberNavController())
    }
}