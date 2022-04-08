package com.romazelenin.addressbook.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import coil.compose.AsyncImage
import com.romazelenin.addressbook.MainViewModel
import com.romazelenin.addressbook.R
import com.romazelenin.addressbook.ui.theme.AddressBookTheme

@Composable
fun DetailsScreen(navController: NavController, userId:String, viewModel: MainViewModel) {
   val user by viewModel.getUser(userId).collectAsState(initial = null)
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
                Text(text = "${user?.firstName} ${user?.lastName}", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = "${user?.userTag?.lowercase()}", color = Color.LightGray, fontSize = 17.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "${user?.position}", fontSize = 13.sp)
            Spacer(modifier = Modifier.height(16.dp))
        }
        Row(modifier=Modifier.padding(16.dp)) {
            Icon(painter = painterResource(id = R.drawable.star), contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "${user?.birthday}")
            Text(modifier = Modifier.weight(1f), text = "30 лет", textAlign = TextAlign.End, color = Color.Gray)
        }
        Row(modifier=Modifier.padding(16.dp)) {
            Icon(painter = painterResource(id = R.drawable.phone), contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "${user?.phone}")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DetailsScreenPreview() {
    AddressBookTheme {
        //DetailsScreen(rememberNavController())
    }
}