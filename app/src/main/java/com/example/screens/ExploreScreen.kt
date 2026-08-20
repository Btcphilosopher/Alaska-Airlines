package com.example.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.R
import com.example.components.SectionHeader
import com.example.state.AppViewModel
import com.example.ui.theme.AlaskaDeepBlue
import com.example.ui.theme.AlaskaIceBlue
import com.example.ui.theme.AlaskaPacificBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: AppViewModel
) {
    val destinations = listOf(
        ExploreDestination(
            id = "anchorage",
            title = "ANCHORAGE",
            sub = "Wild begins here.",
            desc = "Immerse yourself in spectacular glaciers, majestic wild forests, and breathtaking mountain peaks.",
            imageRes = R.drawable.img_anchorage_1787213695644
        ),
        ExploreDestination(
            id = "seattle",
            title = "SEATTLE",
            sub = "Rain, mountains, coffee and the sea.",
            desc = "Discover the evergreen Emerald City with its rich coffee culture, iconic skyline, and coastal cruises.",
            imageRes = R.drawable.img_pnw_hero_1787213682504
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                text = "EXPLORE",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp,
                modifier = Modifier.testTag("explore_header_title")
            )
            Text(
                text = "Discover premium curated destinations",
                fontSize = 13.sp,
                color = AlaskaIceBlue,
                fontWeight = FontWeight.Medium
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                SectionHeader(title = "FEATURED DESTINATIONS")
            }

            items(destinations) { destination ->
                var showDetailModal by remember { mutableStateOf(false) }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDetailModal = true }
                        .testTag("explore_destination_card_${destination.id}")
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        ) {
                            Image(
                                painter = painterResource(id = destination.imageRes),
                                contentDescription = destination.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            
                            // Bottom scrim
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.7f)
                                            )
                                        )
                                    )
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    text = destination.title,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    letterSpacing = 2.sp
                                )
                                Text(
                                    text = destination.sub,
                                    fontSize = 13.sp,
                                    color = AlaskaIceBlue,
                                    fontWeight = FontWeight.Medium,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = destination.desc,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "DISCOVER ➔",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AlaskaPacificBlue
                                )
                                Text(
                                    text = "Magazine Issue #82",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                // Detailed bottom sheet or popup dialog when clicked
                if (showDetailModal) {
                    AlertDialog(
                        onDismissRequest = { showDetailModal = false },
                        confirmButton = {
                            Button(
                                onClick = { 
                                    showDetailModal = false
                                    viewModel.searchDestination.value = if (destination.id == "anchorage") "ANC" else "SEA"
                                    viewModel.currentTab.value = com.example.state.AppTab.BOOK
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AlaskaDeepBlue),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("BOOK TO ${destination.title}")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDetailModal = false }) {
                                Text("CLOSE", color = Color.Gray)
                            }
                        },
                        title = {
                            Text(
                                text = destination.title,
                                fontWeight = FontWeight.Bold,
                                color = AlaskaDeepBlue,
                                letterSpacing = 2.sp
                            )
                        },
                        text = {
                            Column {
                                Text(
                                    text = destination.sub,
                                    fontSize = 14.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = AlaskaPacificBlue,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Experience Alaska Airlines' premium hospitality with seasonal PNW-inspired menu pairings, complimentary craft brews, and fast inflight satellite connectivity. Regular daily flights operate from our Seattle-Tacoma hub.",
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        },
                        shape = RoundedCornerShape(6.dp),
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}

data class ExploreDestination(
    val id: String,
    val title: String,
    val sub: String,
    val desc: String,
    val imageRes: Int
)
