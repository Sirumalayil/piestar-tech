package com.test.chairservice.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lulu.chairservice.R

/**
 * Created by Siru malayil on 07-04-2025.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBackClick: () -> Unit = {}, navController: NavHostController?) {
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = "Profile",
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.poppins_bold))
                    )
                ) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.ic_back),
                            contentDescription = "back icon",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                ),
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            ProfileItem(
                iconRes = R.drawable.ic_profile,
                title = "Setup your profile",
                onClick = {})
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

            ProfileItem(
                iconRes = R.drawable.ic_preferences,
                title = "Setup your preferences",
                onClick = {})
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

            ProfileItem(
                iconRes = R.drawable.ic_notifications,
                title = "Notifications",
                showSwitch = true,
                onClick = {})
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

            ProfileItem(
                iconRes = R.drawable.ic_bluetooth,
                title = "Bluetooth setup again",
                onClick = {})
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

            ProfileItem(
                iconRes = R.drawable.ic_faqs,
                title = "FAQS",
                onClick = {})
        }
    }
}

@Composable
fun CustomSwitch() {

}


@Composable
fun ProfileItem(iconRes: Int, title: String, onClick: () -> Unit,
                showSwitch: Boolean = false) {
    var isEnabled by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = painterResource(id = iconRes),
            contentDescription = title,
            tint = Color.Unspecified,
            modifier = Modifier.size(40.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title,
            fontSize = 16.sp,
            color = Color.Black,
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            modifier = Modifier.weight(500f))
        if (showSwitch) {
            Switch(
                checked = isEnabled,
                onCheckedChange = { isEnabled = it }
            )
        } else
            Icon(painter = painterResource(id = R.drawable.ic_right_arrow), contentDescription = "Forward", tint = Color.Black)
    }
}

@Preview
@Composable
fun PreviewProfile() {
    val navController = rememberNavController()
    ProfileScreen(
        onBackClick = {},
        navController = navController
    )
}