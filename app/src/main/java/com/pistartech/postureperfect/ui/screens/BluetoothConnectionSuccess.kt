package com.pistartech.postureperfect.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.pistartech.postureperfect.R
import com.pistartech.postureperfect.utils.LocalGifImage

/**
 * Created by Siru malayil on 07-04-2025.
 */

@RequiresApi(Build.VERSION_CODES.P)
@Composable
@Preview
fun Preview() {
    val navController = rememberNavController()
    BluetoothConnectionSuccess(navController = navController, onBackClick = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun BluetoothConnectionSuccess(onBackClick: ()-> Unit, navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                { Text("") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF4F3FF)
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { onBackClick() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back Icon"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()
            .padding(paddingValues)
            .padding(0.dp)) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.ic_bluetooth_success),
                contentDescription = "background image",
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.fillMaxSize()
                .padding(16.dp)
                .systemBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                LocalGifImage(drawable = R.raw.anim_success, loop = false)

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "The chair has been connected successfully",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.poppins_bold))
                    )
                )

                Spacer(Modifier.height(20.dp))

                OutlinedButton(
                    modifier = Modifier
                        .width(150.dp)
                        .padding(8.dp),
                    onClick = {
                        navController.navigate("home")
                    },
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color.Blue),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(
                        text = "Continue",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(Modifier.height(30.dp))
            }
        }
    }
}