package com.example.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import androidx.compose.foundation.BorderStroke
import com.example.components.AlaskaButton
import com.example.components.SectionHeader
import com.example.state.AppTab
import com.example.state.AppViewModel
import com.example.ui.theme.AlaskaDeepBlue
import com.example.ui.theme.AlaskaIceBlue
import com.example.ui.theme.AlaskaPacificBlue
import com.example.ui.theme.AlaskaWarmAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onNavigateToTab: (AppTab) -> Unit,
    onShowBoardingPass: () -> Unit,
    onShowTripDetails: () -> Unit
) {
    val scrollState = rememberScrollState()
    val loyalty by viewModel.userLoyalty.collectAsState()
    val nextTrip by viewModel.nextTrip.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()
    val searchOrigin by viewModel.searchOrigin.collectAsState()
    val searchDestination by viewModel.searchDestination.collectAsState()

    var originInput by remember { mutableStateOf(searchOrigin) }
    var destInput by remember { mutableStateOf(searchDestination) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Hero Image with Alaska Editorial Branding
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            // PNW Cinematic Image
            Image(
                painter = painterResource(id = R.drawable.img_pnw_hero_1787213682504),
                contentDescription = "Pacific Northwest wilderness and mountains",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Gradient scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // Brand overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row with brand name and network state
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ALASKA",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 4.sp,
                        modifier = Modifier.testTag("app_brand_logo")
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isOffline) {
                            Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = "Offline cached mode",
                                tint = AlaskaWarmAccent,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 8.dp)
                                    .testTag("offline_indicator")
                            )
                            Text(
                                text = "CACHED",
                                color = AlaskaWarmAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            IconButton(
                                onClick = { viewModel.toggleOfflineMode() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                // Greeting & Editorial Caption
                Column {
                    Text(
                        text = "Good morning, ${loyalty?.name ?: "Tom"}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.testTag("home_greeting")
                    )
                    Text(
                        text = "Ready for the Pacific Northwest wilderness?",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Quick Flight Search Widget ("WHERE TO?")
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-30).dp)
                .testTag("quick_search_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "WHERE TO?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AlaskaDeepBlue,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // From
                    OutlinedTextField(
                        value = originInput,
                        onValueChange = { originInput = it },
                        label = { Text("From") },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_origin_input"),
                        singleLine = true
                    )

                    // To
                    OutlinedTextField(
                        value = destInput,
                        onValueChange = { destInput = it },
                        label = { Text("To") },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_destination_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AlaskaButton(
                    text = "SEARCH FLIGHTS",
                    onClick = {
                        viewModel.searchOrigin.value = originInput
                        viewModel.searchDestination.value = destInput
                        viewModel.performFlightSearch()
                        onNavigateToTab(AppTab.BOOK)
                    },
                    testTag = "home_search_flights_button"
                )
            }
        }

        // Active Trip Banner
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            SectionHeader(title = "YOUR NEXT TRIP")
            Spacer(modifier = Modifier.height(12.dp))

            nextTrip?.let { trip ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = AlaskaDeepBlue),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFF262626)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upcoming_trip_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = trip.origin,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (trip.origin == "SEA") "Seattle" else trip.origin,
                                    fontSize = 14.sp,
                                    color = AlaskaIceBlue
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "to",
                                tint = AlaskaWarmAccent,
                                modifier = Modifier.size(24.dp)
                            )

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = trip.destination,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (trip.destination == "ANC") "Anchorage" else trip.destination,
                                    fontSize = 14.sp,
                                    color = AlaskaIceBlue
                                )
                            }
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AirplanemodeActive,
                                        contentDescription = "Flight",
                                        tint = AlaskaIceBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = trip.flightNumber,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = trip.date,
                                    fontSize = 13.sp,
                                    color = AlaskaIceBlue
                                )
                            }

                            Button(
                                onClick = onShowTripDetails,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = AlaskaDeepBlue
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.testTag("view_trip_button")
                            ) {
                                Text(
                                    text = "VIEW TRIP",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Imminent boarding pass trigger if boarding soon
                        if (trip.isImminent) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                color = AlaskaPacificBlue,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, Color(0xFF525252)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onShowBoardingPass() }
                                    .testTag("imminent_boarding_banner")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AirplaneTicket,
                                        contentDescription = "Boarding Pass",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "BOARDING PASS READY",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Tap to view boarding pass for flight ${trip.flightNumber}",
                                            fontSize = 11.sp,
                                            color = AlaskaIceBlue
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } ?: run {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AlaskaIceBlue),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No upcoming trips. Ready to explore?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
