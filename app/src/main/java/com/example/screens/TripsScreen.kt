package com.example.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.components.GradientCardHeader
import com.example.components.NotificationCard
import com.example.components.SectionHeader
import com.example.state.AppViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    viewModel: AppViewModel,
    onShowBoardingPass: () -> Unit
) {
    val scrollState = rememberScrollState()
    val nextTrip by viewModel.nextTrip.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()
    val airportInfo by viewModel.selectedAirportInfo.collectAsState()

    var showAirportCompanion by remember { mutableStateOf(false) }

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
                text = "MY TRIP",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp,
                modifier = Modifier.testTag("trips_header_title")
            )
            Text(
                text = "Real-time travel guide & airport companion",
                fontSize = 13.sp,
                color = AlaskaIceBlue,
                fontWeight = FontWeight.Medium
            )
        }

        nextTrip?.let { trip ->
            // Route Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .testTag("trips_route_card")
            ) {
                Column {
                    GradientCardHeader(title = "Trip Header")
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${trip.origin} ➔ ${trip.destination}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlaskaDeepBlue
                            )
                            Surface(
                                color = if (trip.status == "ON TIME") Color(0xFFE2F6EA) else AlaskaWarmAccent.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = trip.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (trip.status == "ON TIME") Color(0xFF1B5E20) else AlaskaWarmAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("FLIGHT", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text(trip.flightNumber, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AlaskaDarkCharcoal)
                            }
                            Column {
                                Text("DEPARTURE", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text("${trip.date} • ${trip.departureTime}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AlaskaDarkCharcoal)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("SEAT", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text(trip.seat, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AlaskaPacificBlue)
                            }
                        }

                        if (isOffline) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = AlaskaWarmAccent.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WifiOff,
                                        contentDescription = "Offline cached",
                                        tint = AlaskaWarmAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Viewing cached itinerary (Offline)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AlaskaWarmAccent
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Actions Segment
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onShowBoardingPass,
                    colors = ButtonDefaults.buttonColors(containerColor = AlaskaPacificBlue),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("trips_boarding_pass_action")
                ) {
                    Icon(imageVector = Icons.Default.AirplaneTicket, contentDescription = "Pass")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Boarding Pass", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showAirportCompanion = !showAirportCompanion },
                    colors = ButtonDefaults.buttonColors(containerColor = AlaskaDeepBlue),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("trips_airport_guide_action")
                ) {
                    Icon(imageVector = Icons.Default.Map, contentDescription = "Map")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (showAirportCompanion) "Itinerary" else "Airport Guide", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!showAirportCompanion) {
                // Trip Timeline Segment
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    SectionHeader(title = "TRAVEL TIMELINE")
                    Spacer(modifier = Modifier.height(20.dp))

                    val timelineSteps = listOf(
                        TimelineStep("CHECK-IN", "✓ Completed via mobile", "Completed", true),
                        TimelineStep("BAG DROP", "08:00 • Counter 4A", "Current", true),
                        TimelineStep("SECURITY", "08:20 • TSA PreCheck recommended", "Upcoming", false),
                        TimelineStep("GATE", "C14 • Seattle-Tacoma Int'l", "Upcoming", false),
                        TimelineStep("BOARDING", "08:55 • Group B", "Upcoming", false),
                        TimelineStep("DEPARTURE", "09:20 • Alaska 123", "Upcoming", false)
                    )

                    timelineSteps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(76.dp)
                        ) {
                            // Left vertical node line & circle
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(32.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(
                                            if (step.isCompleted) AlaskaPacificBlue else Color.LightGray,
                                            shape = CircleShape
                                        )
                                        .border(
                                            width = if (step.status == "Current") 4.dp else 0.dp,
                                            color = if (step.status == "Current") AlaskaIceBlue else Color.Transparent,
                                            shape = CircleShape
                                        )
                                )
                                if (index < timelineSteps.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .weight(1f)
                                            .background(
                                                if (step.isCompleted) AlaskaPacificBlue else Color.LightGray
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Right content
                            Column {
                                Text(
                                    text = step.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (step.isCompleted) AlaskaDeepBlue else Color.Gray,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = step.desc,
                                    fontSize = 14.sp,
                                    color = AlaskaDarkCharcoal,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            } else {
                // Airport Companion Segment
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    SectionHeader(title = "AIRPORT COMPANION (SEA)")
                    Spacer(modifier = Modifier.height(14.dp))

                    airportInfo?.let { info ->
                        // Companion metrics
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AlaskaSoftGrey),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("TERMINAL", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                        Text(info.terminal, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AlaskaDeepBlue)
                                    }
                                    Column {
                                        Text("GATE", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                        Text(info.gate, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AlaskaDeepBlue)
                                    }
                                    Column {
                                        Text("SECURITY WAIT", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                        Text(info.securityStatus, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Simplified Map Canvas drawing Gates C14-C16
                        Text(
                            text = "CONCOURSE C TERMINAL MAP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(6.dp))
                                .background(AlaskaSoftGrey)
                                .testTag("airport_terminal_map")
                        ) {
                            // Draw Concourse C layout outline
                            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            
                            // Main walkway corridor
                            drawLine(
                                color = Color.Gray,
                                start = Offset(50f, 180f),
                                end = Offset(size.width - 50f, 180f),
                                strokeWidth = 24f,
                                cap = StrokeCap.Round
                            )

                            // Walkway lines
                            drawLine(
                                color = Color.White,
                                start = Offset(60f, 180f),
                                end = Offset(size.width - 60f, 180f),
                                strokeWidth = 2f,
                                pathEffect = pathEffect
                            )

                            // Gate C12 node
                            drawCircle(color = AlaskaDeepBlue, radius = 12f, center = Offset(120f, 100f))
                            // Gate C14 node (Highlighted)
                            drawCircle(color = AlaskaWarmAccent, radius = 16f, center = Offset(320f, 100f))
                            // Gate C16 node
                            drawCircle(color = AlaskaDeepBlue, radius = 12f, center = Offset(520f, 100f))

                            // Connector paths
                            drawLine(color = AlaskaDeepBlue, start = Offset(120f, 100f), end = Offset(120f, 168f), strokeWidth = 6f)
                            drawLine(color = AlaskaWarmAccent, start = Offset(320f, 100f), end = Offset(320f, 168f), strokeWidth = 8f)
                            drawLine(color = AlaskaDeepBlue, start = Offset(520f, 100f), end = Offset(520f, 168f), strokeWidth = 6f)
                        }

                        // Map labels legend
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Gate C12", fontSize = 11.sp, color = AlaskaDarkCharcoal, fontWeight = FontWeight.Bold)
                            Text("Gate C14 (YOURS)", fontSize = 11.sp, color = AlaskaWarmAccent, fontWeight = FontWeight.Bold)
                            Text("Gate C16", fontSize = 11.sp, color = AlaskaDarkCharcoal, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Lounges info
                        Text(
                            text = "ALASKA LOUNGES IN SEA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        info.lounges.forEach { lounge ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.LocalDrink, contentDescription = "Lounge", tint = AlaskaPacificBlue)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(lounge, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AlaskaDarkCharcoal)
                                }
                            }
                        }
                    }
                }
            }

            // Notifications/Alerts segment
            if (notifications.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    SectionHeader(title = "TRAVEL ALERTS")
                    Spacer(modifier = Modifier.height(8.dp))
                    notifications.take(3).forEach { notification ->
                        NotificationCard(
                            title = notification.title,
                            content = notification.content,
                            type = notification.type,
                            onDismiss = {}
                        )
                    }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No active flight booked. Use the Book tab to find your next adventure!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }
        }
    }
}

data class TimelineStep(
    val title: String,
    val desc: String,
    val status: String,
    val isCompleted: Boolean
)
