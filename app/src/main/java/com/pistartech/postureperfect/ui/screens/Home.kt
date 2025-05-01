package com.pistartech.postureperfect.ui.screens

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.pistartech.postureperfect.R
import com.pistartech.postureperfect.model.AnalyticsData
import com.pistartech.postureperfect.model.PieChartSegment
import com.pistartech.postureperfect.viewmodel.BluetoothViewModel
import kotlinx.coroutines.delay
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by Siru malayil on 10-04-2025.
 */

@Preview
@Composable
fun PreviewHomeScreen() {
    val navController = rememberNavController()
    val bluetoothViewmodel = null
    Home(navController, bluetoothViewmodel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavHostController?, bluetoothViewmodel: BluetoothViewModel?) {
    val user = "Siraj"
    val receivedData = bluetoothViewmodel?.receivedFloatData?.value?.chunked(32) ?: emptyList()

    val cardData = listOf(
        AnalyticsData(title = "Correct sitting posture time", value = "12 Hrs", color = Color(0xFF1FAA59),
            icon = R.drawable.ic_check),  // green
        AnalyticsData(title = "Incorrect sitting posture time",
            value = "4 Hrs", color = Color(0xFFC70039),
            icon =R.drawable.ic_x_circle), // red
        AnalyticsData(title = "Total sitting time",
            value = "16 Hrs", color = Color(0xFF8E2DE2),
            icon = R.drawable.ic_chair_w),            // purple
        AnalyticsData(title = "Percentage of correct sitting",
            value = "60 %", color = Color(0xFF007BFF),
            icon = R.drawable.ic_percent)    // blue
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    Icon(
                        modifier = Modifier.size(50.dp),
                        painter = painterResource(R.drawable.ic_pistar),
                        contentDescription = "App Logo",
                        tint = Color.Unspecified
                    )
                },
                actions = {
                    IconButton(onClick = {
                        navController?.navigate("profile")
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_user),
                            contentDescription = "User Icon",
                            tint = Color.Unspecified
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        val columns = 2
        val itemHeight = 120.dp
        val verticalSpacing = 12.dp

        val rowCount = (cardData.size + columns - 1) / columns
        val gridHeight = (itemHeight * rowCount) + (verticalSpacing * (rowCount - 1))

        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxHeight()
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            item {
                Row(
                    Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = 16.sp)) {
                                append("Hello ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily(Font(R.font.poppins_bold)),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            ) {
                                append(user)
                            }
                        }
                    )
                }
            }

            item {
                Spacer(Modifier.height(20.dp))

                Card (
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ){
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "TODAY", fontSize = 20.sp,
                            fontWeight = FontWeight(700),
                            fontFamily = FontFamily(Font(R.font.poppins_bold))
                        )
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier= Modifier
                                .clickable {
                                    navController?.navigate("analytics")
                                })
                        {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_pie_chart), // Replace with your icon
                                contentDescription = "Analytics",
                                tint = Color(0xFF4B4DED),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "View Analytics",
                                color = Color(0xFF4B4DED),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.height(gridHeight)
                    ) {
                        // Card grid
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            items(cardData.size) { index ->
                                Card(
                                    modifier = Modifier
                                        .height(100.dp)
                                        .fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = cardData[index].color),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(
                                        Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row {
                                            Icon(
                                                painter = painterResource(cardData[index].icon),
                                                contentDescription = "Content icon",
                                                tint = Color.White
                                            )
                                            Text(
                                                modifier = Modifier.padding(start = 5.dp),
                                                text = cardData[index].title,
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily(Font(R.font.poppins_medium)),
                                                color = Color.White
                                            )
                                        }
                                        Text(
                                            text = cardData[index].value,
                                            fontSize = 20.sp,
                                            fontFamily = FontFamily(Font(R.font.poppins_bold)),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(20.dp))

                Column (
                    Modifier.padding(horizontal = 16.dp)
                ){
                    Text(
                        "Today's analytics",
                        fontWeight = FontWeight(700),
                        fontFamily = FontFamily(Font(R.font.poppins_bold)),
                        fontSize = 20.sp)

                    Spacer((Modifier.height(20.dp)))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp) // Add vertical padding here for the content inside the Card
                        ) {
                            val segments: List<PieChartSegment> = listOf(
                                PieChartSegment("Incorrect", 32f, Color(0xFFFF6B6B)), // red
                                PieChartSegment("Correct", 40f, Color(0xFF2ECC71)),   // green
                                PieChartSegment("Not Sitting", 40f, Color(0xFF7F6BFF)) // purple
                            )

                            SittingPieChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                segments = segments
                            )
                        }
                    }

                    Spacer(Modifier.height(30.dp))

                    Text(
                        "Your real time posture",
                        fontWeight = FontWeight(700),
                        fontFamily = FontFamily(Font(R.font.poppins_bold)),
                        fontSize = 20.sp)

                    Spacer(Modifier.height(20.dp))

                    Card (
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ){
                        Column(modifier = Modifier.fillMaxWidth()
                            .padding(start = 8.dp, bottom = 8.dp)) {
//                            val isSafe = runModel(
//                                context = LocalContext.current,
//                                inputData = receivedData)
//                            Log.d("Tflite model", "runModel: $isSafe")
//                            LaunchedEffect(receivedData) {
//                                Log.d("receivedData", "receivedData: $receivedData")
//                                delay(300) // debounce effect
//                            }
                            HeatmapWithAxes(receivedData)
                        }
                    }
                }
            }
        }
    }
}

fun generateHeatmapData(rows: Int = 30, cols: Int = 30): List<List<Float>> {
    return List(rows) { row ->
        List(cols) { col ->
            val x = col.toFloat() / cols
            val y = row.toFloat() / rows
            ((sin(2 * Math.PI * x) + cos(2 * Math.PI * y)) / 2 + 1).toFloat() / 2
        }
    }
}

@Composable
fun SimpleHeatmap(data: List<List<Float>>, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .background(Color.White)
    ) {
        val cellWidth = size.width / data[0].size
        val cellHeight = size.height / data.size

        data.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, value ->
                drawRect(
                    color = getColorForValue(value),
                    topLeft = Offset(colIndex * cellWidth, rowIndex * cellHeight),
                    size = Size(cellWidth, cellHeight)
                )
            }
        }
    }
}

@Composable
fun HeatmapWithAxes(heatmapData: List<List<Float>>) {
    val rows = heatmapData.size
    val cols = heatmapData.firstOrNull()?.size ?: 0

    val paddedData = remember(heatmapData) { heatmapData }

    Row(modifier = Modifier.background(Color.White)) {
        // Y-axis labels
        Column(
            modifier = Modifier
                .padding(end = 4.dp)
                .height(210.dp)
                .width(20.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            val yLabels = (5..rows step 5).reversed()
            yLabels.forEach { label ->
                Text(
                    text = label.toString(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.poppins_bold)),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Column {
            // Heatmap Canvas
            SimpleHeatmap(data = paddedData, modifier = Modifier
                .height(210.dp)
                .fillMaxWidth())

            // X-axis labels
            Row(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val xLabels = (5..cols step 5)
                xLabels.forEach { label ->
                    Text(
                        text = label.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.poppins_bold)),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(20.dp)
                    )
                }
            }
        }
    }
}

fun getColorForValue(value: Float): Color {
    return when {
        value < 0.2f -> Color(0xFF0000FF) // Blue
        value < 0.4f -> Color(0xFF00FFFF) // Cyan
        value < 0.6f -> Color(0xFF00FF00) // Green
        value < 0.8f -> Color(0xFFFFFF00) // Yellow
        else -> Color(0xFFFF0000) // Red
    }
}

@Composable
fun SittingPieChart(
    modifier: Modifier = Modifier,
    totalHours: Int = 16,
    segments: List<PieChartSegment>
) {

    Box(modifier = modifier.size(220.dp),
        contentAlignment = Alignment.Center) {
        val segmentCenters = remember { mutableStateListOf<Offset>() }

        Canvas(modifier = Modifier.size(220.dp)) {
            segmentCenters.clear()

            val gapAngle = 3f
            val totalAngle = 360f
            val totalGap = gapAngle * segments.size
            val availableSweep = totalAngle - totalGap
            val stroke = Stroke(width = 36.dp.toPx(), cap = StrokeCap.Butt)
            val chartRadius = size.minDimension / 2
            var currentAngle = -90f

            segments.forEach { segment ->
                val sweepAngle = (segment.percentage / 100f) * availableSweep

                drawArc(
                    color = segment.color,
                    startAngle = currentAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = stroke
                )

                // Calculate label position
                val centerAngle = currentAngle + sweepAngle / 2f
                val radians = Math.toRadians(centerAngle.toDouble())
                val labelRadius = chartRadius - (stroke.width / 2f)

                val labelX = center.x + cos(radians) * labelRadius
                val labelY = center.y + sin(radians) * labelRadius

                segmentCenters.add(Offset(labelX.toFloat(), labelY.toFloat()))

//                drawCircle(
//                    color = Color.Red,
//                    radius = 4.dp.toPx(),
//                    center = Offset(labelX.toFloat(), labelY.toFloat())
//                )

                drawArc(
                    color = Color.White,
                    startAngle = currentAngle + sweepAngle,
                    sweepAngle = gapAngle,
                    useCenter = false,
                    style = stroke
                )

                currentAngle += sweepAngle + gapAngle
            }
        }

//        segmentCenters.forEachIndexed { index, offset ->
//            val labelSize = 48.dp
//            val labelSizePx = with(LocalDensity.current) { labelSize.toPx() }
//
//            Box(
//                modifier = Modifier
//                    .absoluteOffset {
//                        IntOffset(
//                            (offset.x - labelSizePx / 2).toInt(),
//                            (offset.y - labelSizePx / 2).toInt()
//                        )
//                    }
//                    .size(labelSize)
//                    .clip(CircleShape)
//                    .background(Color.White),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = "${segments[index].percentage.toInt()}%",
//                    fontSize = 12.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }


        // Center Text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$totalHours HRS",
                fontSize = 26.sp,
                fontFamily = FontFamily(Font(R.font.poppins_bold)),
                fontWeight = FontWeight(700)
            )
            Text(text = "TOTAL SITTING HOURS",
                fontSize = 12.sp,
                fontWeight = FontWeight((500)),
                color = Color.Gray)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        segments.forEach {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Card(
                    Modifier.padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = it.color
                    ),
                    shape = RoundedCornerShape(6.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = it.label.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight(700),
                            fontFamily = FontFamily(Font(R.font.poppins_medium)),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}


fun loadModelFile(context: Context, fileName: String): MappedByteBuffer {
    val fileDescriptor: AssetFileDescriptor = context.assets.openFd(fileName)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
}

fun runModel(context: Context, inputData: List<Float>?): Boolean {
    val model = Interpreter(loadModelFile(context, "model.tflite"))

    // Convert List<Float> to FloatArray
    val inputArray = inputData?.toFloatArray()

    // Prepare input buffer
    val inputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, inputArray?.size ?: 0), DataType.FLOAT32)
    inputBuffer.loadArray(inputArray)

    // Prepare output buffer (adjust shape to your model's output)
    val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 1), DataType.FLOAT32)

    model.run(inputBuffer.buffer, outputBuffer.buffer.rewind())

    val output = outputBuffer.floatArray[0]

    // Return true or false based on some logic — for example:
    return output > 0.5f
}