package com.test.chairservice.ui.screens

import android.graphics.RectF
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lulu.chairservice.R
import com.test.chairservice.ui.theme.PrimaryColor
import com.test.chairservice.utils.CustomOutlinedEditField

/**
 * Created by Siru malayil on 07-04-2025.
 */

@Preview
@Composable
fun PreviewSetupProfile() {
    val navController = rememberNavController()
    SetUpProfile(
        onBackClick = {},
        navController = navController
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetUpProfile(onBackClick: () -> Unit = {}, navController: NavHostController?) {
    var selectedGender by rememberSaveable { mutableStateOf("Male") }
    var name by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    var height by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Setup your profile",
                        fontFamily = FontFamily(Font(R.font.poppins_bold))
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
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
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                "Select your Gender",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.poppins_bold)),
                    fontSize = 16.sp,
                    fontWeight = FontWeight(700),
                    lineHeight = TextUnit(value = 20f, type = TextUnitType.Sp)
                )
            )

            Spacer(Modifier.height(20.dp))

            Row (
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ){
                GenderOption(
                    label = "Male",
                    isSelected = selectedGender == "Male",
                    onClick = { selectedGender = "Male" }
                )
                GenderOption(
                    label = "Female",
                    isSelected = selectedGender == "Female",
                    onClick = { selectedGender = "Female" }
                )
            }

            Spacer(Modifier.height(20.dp))

            CustomOutlinedEditField(
                label = "Name",
                value = name,
                inputType = KeyboardType.Text,
                onValueChange = {
                    name = it
                }
            )
            CustomOutlinedEditField(
                label = "Enter your Weight",
                value = name,
                inputType = KeyboardType.Number,
                onValueChange = {
                    name = it
                }
            )
            CustomOutlinedEditField(
                label = "Enter your Height",
                value = name,
                inputType = KeyboardType.Number,
                onValueChange = {
                    name = it
                }
            )
            CustomOutlinedEditField(
                label = "Enter your Age",
                value = name,
                inputType = KeyboardType.Number,
                onValueChange = {
                    name = it
                }
            )

            Spacer(Modifier.height(20.dp))

            DashedBorderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                strokeColor = Color.Gray,
                cornerRadius = 12f
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.clickable {

                        },
                        tint = Color.Unspecified,
                        contentDescription = "Upload Icon",
                        painter = painterResource(R.drawable.ic_upload),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Click to Upload your photo",
                        Modifier.padding(vertical = 2.dp),
                        fontWeight = FontWeight.Medium,
                        color = PrimaryColor,
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_medium))
                    )
                    Text("(Max. File size: 1 MB)",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Composable
fun GenderOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) PrimaryColor else Color.White
    val textColor = if (isSelected) Color.White else Color.Gray
    val borderColor = if (isSelected) PrimaryColor else Color.Gray

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .height(42.dp)
            .border(
                width = 0.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(50)
            ) // Rounded pill shape
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 50.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.poppins_medium))
        )
    }
}

@Composable
fun DashedBorderBox(
    modifier: Modifier = Modifier,
    strokeColor: Color = Color.Gray,
    strokeWidth: Float = 4f,
    cornerRadius: Float = 16f,
    dashLength: Float = 10f,
    gapLength: Float = 10f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .drawBehind {
                val paint = Paint().asFrameworkPaint()
                paint.apply {
                    style = android.graphics.Paint.Style.STROKE
                    color = strokeColor.toArgb()
                    pathEffect = android.graphics.DashPathEffect(
                        floatArrayOf(dashLength, gapLength), 0f
                    )
                    isAntiAlias = true
                }

                val rect = RectF(
                    strokeWidth / 2,
                    strokeWidth / 2,
                    size.width - strokeWidth / 2,
                    size.height - strokeWidth / 2
                )

                drawContext.canvas.nativeCanvas.drawRoundRect(
                    rect, cornerRadius, cornerRadius, paint
                )
            }
            .padding(16.dp)
    ) {
        content()
    }
}