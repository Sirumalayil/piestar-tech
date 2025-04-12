package com.test.chairservice.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lulu.chairservice.R

/**
 * Created by Siru malayil on 07-04-2025.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun Faqs(onBackClick: () -> Unit = {}, navController: NavHostController?) {
    val faqItems = listOf(
        "Elementum facilisi aliquam, nisi, orci vulputate?" to "Nibh quisque suscipit fermentum netus nulla cras porttitor euismod nulla. Orci, dictumst nec aliquet id ullamcorper venenatis. Fermentum sulla craspor ttitore ismod nulla. Elit adipiscing proin quis est consectetur. Felis ultricies nisi, quis malesuada sem odio. Potenti nibh natoque amet amet, tincidunt ultricies et. Et nam rhoncus sit nullam diam tincidunt condimentum nullam.",
        "Elementum facilisi aliquam, nisi, orci vulputate?" to "Nibh quisque suscipit fermentum netus nulla cras porttitor euismod nulla. Orci, dictumst nec aliquet id ullamcorper venenatis. Fermentum sulla craspor ttitore ismod nulla. Elit adipiscing proin quis est consectetur. Felis ultricies nisi, quis malesuada sem odio. Potenti nibh natoque amet amet, tincidunt ultricies et. Et nam rhoncus sit nullam diam tincidunt condimentum nullam.",
        "Elementum facilisi aliquam, nisi, orci vulputate?" to "Nibh quisque suscipit fermentum netus nulla cras porttitor euismod nulla. Orci, dictumst nec aliquet id ullamcorper venenatis. Fermentum sulla craspor ttitore ismod nulla. Elit adipiscing proin quis est consectetur. Felis ultricies nisi, quis malesuada sem odio. Potenti nibh natoque amet amet, tincidunt ultricies et. Et nam rhoncus sit nullam diam tincidunt condimentum nullam.",
        "Elementum facilisi aliquam, nisi, orci vulputate?" to "Nibh quisque suscipit fermentum netus nulla cras porttitor euismod nulla. Orci, dictumst nec aliquet id ullamcorper venenatis. Fermentum sulla craspor ttitore ismod nulla. Elit adipiscing proin quis est consectetur. Felis ultricies nisi, quis malesuada sem odio. Potenti nibh natoque amet amet, tincidunt ultricies et. Et nam rhoncus sit nullam diam tincidunt condimentum nullam."
    )

    val expandStates = remember { mutableStateListOf(*Array(faqItems.size){ false}) }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = "FAQS",
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
                modifier = Modifier.shadow(4.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                ),
            )
        }
    ) { paddingValues ->
        LazyColumn (modifier = Modifier.padding(paddingValues)
            .fillMaxSize()) {
            itemsIndexed(faqItems) { index, item ->
                FaqItem(
                    itemCount = index + 1,
                    title = item.first,
                    description = item.second,
                    isExpanded = expandStates[index],
                    onExpandedToggle = { expandStates[index] =!expandStates[index] }
                )
                HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun FaqItem(
    itemCount: Int,
    title: String,
    description: String,
    isExpanded: Boolean,
    onExpandedToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "$itemCount. ",
                fontSize = 16.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.poppins_bold)),
                modifier = Modifier.padding(end = 8.dp)
            )

            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.poppins_bold)),
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(id = if (isExpanded)
                    R.drawable.ic_xmark else R.drawable.ic_plus),
                contentDescription = "Expand/Collapse",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onExpandedToggle() }
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.DarkGray,
                fontFamily = FontFamily(Font(R.font.poppins_regular))
            )
        }
    }
}