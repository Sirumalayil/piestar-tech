package com.pistartech.postureperfect.ui.screens

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.compose.material3.MaterialTheme
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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.pistartech.postureperfect.R

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
                        color = MaterialTheme.colorScheme.onPrimary,
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
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary
                ),
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            ProfileItem(
                iconRes = R.drawable.ic_profile,
                title = "Setup your profile",
                onClick = {
                    navController?.navigate("setup_profile")
                })
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

            ProfileItem(
                iconRes = R.drawable.ic_preferences,
                title = "Setup your preferences",
                onClick = {
                    //navController?.navigate("")
                })
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

            ProfileItem(
                iconRes = R.drawable.ic_notifications,
                title = "Notifications",
                showSwitch = true,
                onClick = {
                    //Enable Notification
                })
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

            ProfileItem(
                iconRes = R.drawable.ic_bluetooth,
                title = "Bluetooth setup again",
                onClick = {
                    //navController?.navigate("")
                })
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

            ProfileItem(
                iconRes = R.drawable.ic_faqs,
                title = "FAQS",
                onClick = {
                    navController?.navigate("faqs")
                })
        }
    }
}

@Composable
fun ProfileItem(iconRes: Int, title: String, onClick: () -> Unit,
                showSwitch: Boolean = false) {
    var isEnabled by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = painterResource(id = iconRes),
            contentDescription = title,
            tint = Color.Unspecified,
            modifier = Modifier.size(40.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            modifier = Modifier.weight(500f))
        if (showSwitch) {
            Switch(
                checked = isEnabled,
                onCheckedChange = {
                    isEnabled = it
                    if (it) {
                        showNotification(context)
                    } else {
                        cancelNotification(context)
                    }
                }
            )
        } else
            Icon(painter = painterResource(
                id = R.drawable.ic_right_arrow),
                contentDescription = "Forward",
                tint = MaterialTheme.colorScheme.surfaceTint)
    }
}

fun cancelNotification(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(1) // Cancel the notification by ID
}

fun showNotification(context: Context) {
    val channelId = "switch_channel"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Switch Notification",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.mipmap.ic_posture_perfect_launcher) // replace with your icon
        .setContentTitle("Switch Enabled")
        .setContentText("Notifications are now active.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    notificationManager.notify(1, builder.build())
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

