package com.test.chairservice.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lulu.chairservice.R
import com.test.chairservice.model.PostureData
import com.test.chairservice.ui.theme.ColorCorrectSittingPosture
import com.test.chairservice.ui.theme.ColorIncorrectSittingPosture
import com.test.chairservice.ui.theme.gradientAverageSitting
import com.test.chairservice.ui.theme.gradientCorrectPosture
import com.test.chairservice.ui.theme.gradientIncorrectPosture
import kotlinx.coroutines.delay

/**
 * Created by Siru malayil on 07-04-2025.
 */

@Preview
@Composable
fun PreviewAnalyticsScreen() {
    val navController = rememberNavController()

    Analytics(
        onBackClick = {},
        navController = navController
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Analytics(onBackClick: () -> Unit = {}, navController: NavHostController?) {
    val tabTitles = listOf("This Week", "This Month", "6 Months")
    var selectedTabIndex by remember { mutableStateOf(0) }


    Scaffold(
        topBar = {
            TopAppBar(
                {
                    Text(
                        "Analytics",
                        fontFamily = FontFamily(Font(R.font.poppins_bold)),
                        fontSize = 18.sp,
                        fontWeight = FontWeight(700)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onBackClick() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back Icon"
                        )
                    }
                },
                modifier = Modifier.shadow(4.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabRow(selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .height(3.dp),
                        color = Color.Blue
                    )
                }) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // Content based on selected tab
            when (selectedTabIndex) {
                0 -> PostureBarChart(data = sampleData)
                1 -> PostureBarChart(data = sampleData)
                2 -> PostureBarChart(data = sampleData)
            }
        }
    }
}

val sampleData = listOf(
    PostureData("JUN", 11f, 2f),
    PostureData("JUL", 14f, 4f),
    PostureData("AUG", 8f, 3f),
    PostureData("SEP", 6f, 3f),
    PostureData("OCT", 5f, 2f),
    PostureData("NOV", 9f, 2f)
)

@Composable
fun PostureBarChart(data: List<PostureData>) {
    val maxHours = 14f
    val barWidth = 26.dp
    val barSpacing = 20.dp

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier.height(30.dp))

        // Y-axis labels
        Row(modifier = Modifier.wrapContentHeight()) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.height(160.dp).padding(end = 16.dp)
            ) {
                (2..12 step 2).reversed().forEach {
                    Text(
                        text = "$it",
                        fontSize = 12.sp,
                        color = Color(0xFFBDCADB)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(barSpacing),
                modifier = Modifier.wrapContentHeight(),
            ) {
                data.forEachIndexed { index, postureData ->
                    var animationPlayed by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        delay(index * 100L) // Stagger each bar a bit
                        animationPlayed = true
                    }

                    val animatedCorrectRatio by animateFloatAsState(
                        targetValue = if (animationPlayed) postureData.correctHours / maxHours else 0f,
                        animationSpec = tween(durationMillis = 800)
                    )

                    val animatedIncorrectRatio by animateFloatAsState(
                        targetValue = if (animationPlayed) postureData.incorrectHours / maxHours else 0f,
                        animationSpec = tween(durationMillis = 800)
                    )


                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        val totalHeight = 160.dp

                        Box(
                            modifier = Modifier
                                .width(barWidth)
                                .height(totalHeight)
                                .background(Color.Transparent),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Bottom
                            ) {

                                val incorrectHeight = totalHeight * animatedIncorrectRatio

                                Box(
                                    modifier = Modifier
                                        .offset(y = (4.dp))
                                        .height(incorrectHeight + 4.dp * animatedIncorrectRatio)
                                        .width(barWidth)
                                        .background(
                                            shape = RoundedCornerShape(
                                                topStart = 4.dp,
                                                topEnd = 4.dp
                                            ),
                                            color = Color(0xFFFF7B5B)) // Incorrect (orange)
                                )
                                Box(
                                    modifier = Modifier
                                        .height(totalHeight * animatedCorrectRatio)
                                        .width(barWidth)
                                        .background(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF34D19B)) // Correct (cyan-green)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            postureData.month,
                            color = Color(0xFFBDCADB),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.rotate(-90f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Showing:".uppercase(),
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                fontSize = 10.sp,
                letterSpacing = 3.sp,
                color = Color(0xFF04293C),
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(color = ColorCorrectSittingPosture,
                        shape = CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Correct".uppercase(),
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                fontSize = 10.sp,
                letterSpacing = 3.sp,
                color = Color(0xFF04293C),
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(color = ColorIncorrectSittingPosture,
                        shape = CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Incorrect".uppercase(),
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                fontSize = 10.sp,
                letterSpacing = 3.sp,
                color = Color(0xFF04293C),
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Legends
        Column{
            GradientLegend(brush = gradientCorrectPosture,
                label = "Correct sitting posture on <> : ", time = "8 Hrs")
            GradientLegend(brush = gradientIncorrectPosture,
                label = "Incorrect sitting posture on <> : ", time = "2 Hrs")
            GradientLegend(brush = gradientAverageSitting,
                label = "Average sitting time this : ", time = "4 Month")
        }

        Spacer(modifier = Modifier.height(8.dp))

    }
}

@Composable
fun GradientLegend(brush: Brush, label: String, time: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(brush = brush, shape = RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.poppins_medium)))
                ) {
                    append(label)
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.poppins_bold))
                    )
                ) {
                    append(time)
                }
            }
        )
    }
}

