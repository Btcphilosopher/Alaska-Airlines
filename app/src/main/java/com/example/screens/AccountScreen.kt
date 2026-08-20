package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.components.GradientCardHeader
import com.example.components.SectionHeader
import com.example.state.AppViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: AppViewModel
) {
    val scrollState = rememberScrollState()
    val loyalty by viewModel.userLoyalty.collectAsState()
    val flightStatusNumber by viewModel.flightStatusSearchNumber.collectAsState()
    val liveStatus by viewModel.liveFlightStatus.collectAsState()
    val isCheckingStatus by viewModel.isCheckingStatus.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()

    var showBenefitsList by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 80.dp)
    ) {
        // High-contrast Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AlaskaDeepBlue)
                .padding(top = 40.dp, bottom = 24.dp)
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "LOYALTY ACCOUNT",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp,
                modifier = Modifier.testTag("account_header_title")
            )
            Text(
                text = "Tom • Mileage Plan MVP Gold",
                fontSize = 13.sp,
                color = AlaskaIceBlue,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // MVP Gold Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("loyalty_status_card")
            ) {
                Column {
                    GradientCardHeader(title = "Loyalty Status")
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "STATUS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = loyalty?.status ?: "MVP GOLD",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AlaskaDeepBlue
                                )
                            }
                            
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = "Status",
                                tint = AlaskaWarmAccent,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("ACTIVE MILES", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text(
                                    text = String.format("%,d", loyalty?.miles ?: 42850),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AlaskaDarkCharcoal
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("NEXT LEVEL", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "MVP Gold 75K",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AlaskaPacificBlue
                                )
                                Text(
                                    text = "${String.format("%,d", loyalty?.nextStatusMiles ?: 18200)} miles away",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress indicator toward next status
                        LinearProgressIndicator(
                            progress = 0.7f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = AlaskaPacificBlue,
                            trackColor = AlaskaIceBlue
                        )
                    }
                }
            }

            // Benefits Selector dropdown
            Card(
                colors = CardDefaults.cardColors(containerColor = AlaskaIceBlue),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showBenefitsList = !showBenefitsList }
                    .testTag("benefits_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CardMembership, contentDescription = "Benefits", tint = AlaskaDeepBlue)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("MVP Gold Benefits Details", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AlaskaDeepBlue)
                        }
                        Icon(
                            imageVector = if (showBenefitsList) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand"
                        )
                    }

                    if (showBenefitsList) {
                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            BenefitBullet("Complimentary First Class upgrades upon booking")
                            BenefitBullet("Two free checked bags on all flights")
                            BenefitBullet("Express security lines (TSA PreCheck) at select airports")
                            BenefitBullet("Lounge access privileges for partner clubs")
                            BenefitBullet("100% bonus miles earning on eligible flights")
                        }
                    }
                }
            }

            // Live Flight Status search widget
            SectionHeader(title = "LIVE FLIGHT STATUS")
            
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = flightStatusNumber,
                            onValueChange = { viewModel.flightStatusSearchNumber.value = it },
                            label = { Text("Flight Number (e.g. AS 123)") },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("flight_status_search_input"),
                            singleLine = true
                        )

                        Button(
                            onClick = { viewModel.searchLiveFlightStatus(flightStatusNumber) },
                            colors = ButtonDefaults.buttonColors(containerColor = AlaskaDeepBlue),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("flight_status_search_button")
                        ) {
                            if (isCheckingStatus) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                            } else {
                                Text("CHECK")
                            }
                        }
                    }

                    liveStatus?.let { status ->
                        Divider(modifier = Modifier.padding(vertical = 14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(status.flightNumber, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AlaskaDeepBlue)
                                Text("${status.origin} ➔ ${status.destination}", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Surface(
                                    color = if (status.status == "ON TIME") Color(0xFFE2F6EA) else AlaskaWarmAccent.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = status.status,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (status.status == "ON TIME") Color(0xFF1B5E20) else AlaskaWarmAccent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text("Gate C14 • 737 MAX", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
            }

            // Connection state settings toggle
            Card(
                colors = CardDefaults.cardColors(containerColor = AlaskaIceBlue),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Simulate Offline Mode", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AlaskaDeepBlue)
                        Text("Enables cached flight lists & travel guides", fontSize = 11.sp, color = Color.Gray)
                    }

                    Switch(
                        checked = isOffline,
                        onCheckedChange = { viewModel.toggleOfflineMode() },
                        colors = SwitchDefaults.colors(checkedThumbColor = AlaskaDeepBlue, checkedTrackColor = AlaskaIceBlue)
                    )
                }
            }
        }
    }
}

@Composable
fun BenefitBullet(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(AlaskaPacificBlue, shape = RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text, fontSize = 13.sp, color = AlaskaDarkCharcoal, fontWeight = FontWeight.Medium)
    }
}
