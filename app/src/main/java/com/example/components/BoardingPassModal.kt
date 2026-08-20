package com.example.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.state.AppViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardingPassModal(
    viewModel: AppViewModel,
    onDismiss: () -> Unit
) {
    val bPass by viewModel.activeBoardingPass.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()

    var brightnessBoosted by remember { mutableStateOf(false) }
    var isSavedToWallet by remember { mutableStateOf(false) }

    bPass?.let { pass ->
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_boarding_pass_button")
                ) {
                    Text("CLOSE", color = AlaskaDeepBlue, fontWeight = FontWeight.Bold)
                }
            },
            title = null,
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(AlaskaDeepBlue)
                        .border(1.5.dp, AlaskaIceBlue, RoundedCornerShape(6.dp))
                        .testTag("boarding_pass_panel"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header logo
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ALASKA",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 4.sp
                        )
                        Text(
                            text = "BOARDING PASS",
                            fontSize = 10.sp,
                            color = AlaskaIceBlue,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    // Route summary
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(pass.origin, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Text("Seattle", fontSize = 12.sp, color = AlaskaIceBlue)
                        }

                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "route",
                            tint = AlaskaWarmAccent,
                            modifier = Modifier.size(24.dp)
                        )

                        Column(horizontalAlignment = Alignment.End) {
                            Text(pass.destination, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Text("Anchorage", fontSize = 12.sp, color = AlaskaIceBlue)
                        }
                    }

                    // Dotted cut line divider
                    DottedCutLine()

                    // Key Flight details
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            PassMetric("FLIGHT", pass.flightNumber, Modifier.weight(1f))
                            PassMetric("DATE", pass.date, Modifier.weight(1f))
                            PassMetric("SEAT", pass.seat, Modifier.weight(1f))
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            PassMetric("GATE", pass.gate, Modifier.weight(1f))
                            PassMetric("BOARDING", pass.boardingTime, Modifier.weight(1f))
                            PassMetric("CABIN", "Main Cabin", Modifier.weight(1f))
                        }
                    }

                    // Dotted cut line divider
                    DottedCutLine()

                    // QR / Barcode container
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Drawing custom barcode / QR pattern
                        Text(
                            text = "SCAN QR CODE AT GATE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Styled mock barcode
                        MockBarcodeDrawing()

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = pass.barcodePayload,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color.DarkGray
                        )

                        if (isOffline) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = AlaskaWarmAccent.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "OFFLINE COMPATIBLE PASS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AlaskaWarmAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Footer actions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Optimize brightness
                        IconButton(
                            onClick = { brightnessBoosted = !brightnessBoosted },
                            modifier = Modifier.testTag("brightness_button")
                        ) {
                            Icon(
                                imageVector = if (brightnessBoosted) Icons.Default.BrightnessHigh else Icons.Default.BrightnessMedium,
                                contentDescription = "Brightness",
                                tint = if (brightnessBoosted) AlaskaWarmAccent else AlaskaDeepBlue
                            )
                        }

                        // Save to wallet
                        Button(
                            onClick = { isSavedToWallet = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSavedToWallet) Color.LightGray else AlaskaDeepBlue
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("save_to_wallet_button")
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = "Save")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isSavedToWallet) "SAVED TO WALLET" else "SAVE TO WALLET", fontSize = 12.sp)
                        }
                    }
                }
            },
            shape = RoundedCornerShape(6.dp),
            containerColor = Color.Transparent
        )
    }
}

@Composable
fun PassMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 10.sp, color = AlaskaIceBlue, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
    }
}

@Composable
fun DottedCutLine() {
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        drawLine(
            color = AlaskaIceBlue.copy(alpha = 0.5f),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect,
            strokeWidth = 2f
        )
    }
}

@Composable
fun MockBarcodeDrawing() {
    Canvas(
        modifier = Modifier
            .width(180.dp)
            .height(50.dp)
    ) {
        // Simple stylized barcode pattern of lines
        val barCount = 36
        val barWidth = size.width / barCount
        
        for (i in 0 until barCount) {
            val isFilled = (i * 7) % 3 != 0
            if (isFilled) {
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(i * barWidth, 0f),
                    size = androidx.compose.ui.geometry.Size(barWidth * 0.7f, size.height)
                )
            }
        }
    }
}
