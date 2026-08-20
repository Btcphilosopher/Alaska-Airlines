package com.example.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.AlaskaButton
import com.example.components.AlaskaSecondaryButton
import com.example.components.GradientCardHeader
import com.example.components.SectionHeader
import androidx.compose.foundation.BorderStroke
import com.example.services.*
import com.example.state.BookStep
import com.example.state.AppViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(
    viewModel: AppViewModel,
    onShowBoardingPass: () -> Unit
) {
    val currentStep by viewModel.currentBookStep.collectAsState()
    val origin by viewModel.searchOrigin.collectAsState()
    val destination by viewModel.searchDestination.collectAsState()
    val date by viewModel.searchDate.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchError by viewModel.searchError.collectAsState()

    val selectedFlight by viewModel.selectedFlight.collectAsState()
    val selectedFareClass by viewModel.selectedFareClass.collectAsState()
    val selectedFarePrice by viewModel.selectedFarePrice.collectAsState()
    val passengerName by viewModel.passengerName.collectAsState()
    val selectedSeat by viewModel.selectedSeat.collectAsState()
    val checkedBagsCount by viewModel.checkedBagsCount.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Step Progress Bar
        BookProgressIndicator(currentStep = currentStep)

        // Main wizard screen switch
        Box(modifier = Modifier.weight(1f)) {
            when (currentStep) {
                BookStep.SEARCH -> SearchStepView(viewModel)
                BookStep.FLIGHT_RESULTS -> FlightResultsStepView(viewModel, results, isSearching, searchError)
                BookStep.FARE_SELECTION -> FareSelectionStepView(viewModel, selectedFlight)
                BookStep.PASSENGERS -> PassengersStepView(viewModel)
                BookStep.SEATS -> SeatsStepView(viewModel, selectedSeat)
                BookStep.BAGS -> BagsStepView(viewModel, checkedBagsCount)
                BookStep.PAYMENT -> PaymentStepView(viewModel, selectedFlight, selectedFareClass, selectedFarePrice, checkedBagsCount)
                BookStep.CONFIRMATION -> ConfirmationStepView(viewModel, selectedFlight, selectedSeat, onShowBoardingPass)
            }
        }
    }
}

@Composable
fun BookProgressIndicator(currentStep: BookStep) {
    val progress = when (currentStep) {
        BookStep.SEARCH -> 0.1f
        BookStep.FLIGHT_RESULTS -> 0.25f
        BookStep.FARE_SELECTION -> 0.4f
        BookStep.PASSENGERS -> 0.55f
        BookStep.SEATS -> 0.7f
        BookStep.BAGS -> 0.8f
        BookStep.PAYMENT -> 0.9f
        BookStep.CONFIRMATION -> 1.0f
    }

    Column(modifier = Modifier.fillMaxWidth().background(AlaskaDeepBlue).padding(top = 40.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FLIGHT BOOKING",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AlaskaIceBlue,
                letterSpacing = 1.sp
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 11.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth().height(3.dp),
            color = AlaskaWarmAccent,
            trackColor = AlaskaDeepBlue
        )
    }
}

@Composable
fun SearchStepView(viewModel: AppViewModel) {
    var origin by remember { mutableStateOf(viewModel.searchOrigin.value) }
    var destination by remember { mutableStateOf(viewModel.searchDestination.value) }
    var departureDate by remember { mutableStateOf(viewModel.searchDate.value) }
    var returnDate by remember { mutableStateOf(viewModel.searchReturnDate.value) }
    var passengers by remember { mutableStateOf(viewModel.searchPassengers.value) }
    var cabin by remember { mutableStateOf(viewModel.searchCabin.value) }
    var flexibleDates by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(title = "SEARCH FLIGHTS")
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AlaskaIceBlue),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Origin
                    OutlinedTextField(
                        value = origin,
                        onValueChange = { origin = it },
                        label = { Text("FROM") },
                        leadingIcon = { Icon(Icons.Default.FlightTakeoff, "takeoff") },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().testTag("book_origin_input"),
                        singleLine = true
                    )

                    // Destination
                    OutlinedTextField(
                        value = destination,
                        onValueChange = { destination = it },
                        label = { Text("TO") },
                        leadingIcon = { Icon(Icons.Default.FlightLand, "landing") },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().testTag("book_destination_input"),
                        singleLine = true
                    )
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = departureDate,
                    onValueChange = { departureDate = it },
                    label = { Text("DEPART") },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).testTag("book_depart_input")
                )

                OutlinedTextField(
                    value = returnDate,
                    onValueChange = { returnDate = it },
                    label = { Text("RETURN") },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).testTag("book_return_input")
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = passengers,
                    onValueChange = { passengers = it },
                    label = { Text("PASSENGERS") },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).testTag("book_passengers_input")
                )

                OutlinedTextField(
                    value = cabin,
                    onValueChange = { cabin = it },
                    label = { Text("CABIN") },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).testTag("book_cabin_input")
                )
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { flexibleDates = !flexibleDates }
            ) {
                Checkbox(checked = flexibleDates, onCheckedChange = { flexibleDates = it })
                Text("Search flexible dates (lower fares)", fontSize = 14.sp, color = AlaskaDarkCharcoal)
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            AlaskaButton(
                text = "SEARCH",
                onClick = {
                    viewModel.searchOrigin.value = origin
                    viewModel.searchDestination.value = destination
                    viewModel.searchDate.value = departureDate
                    viewModel.searchReturnDate.value = returnDate
                    viewModel.searchPassengers.value = passengers
                    viewModel.searchCabin.value = cabin
                    viewModel.performFlightSearch()
                },
                testTag = "book_search_action_button"
            )
        }
    }
}

@Composable
fun FlightResultsStepView(
    viewModel: AppViewModel,
    results: List<FlightResult>,
    isSearching: Boolean,
    error: String?
) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${viewModel.searchOrigin.value} ➔ ${viewModel.searchDestination.value}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AlaskaDeepBlue
            )
            IconButton(onClick = { viewModel.currentBookStep.value = BookStep.SEARCH }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Search")
            }
        }

        if (isSearching) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AlaskaPacificBlue)
            }
        } else if (error != null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.ErrorOutline, "Error", tint = Color.Red, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(error, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                AlaskaButton(text = "Go Back", onClick = { viewModel.currentBookStep.value = BookStep.SEARCH })
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(results) { flight ->
                    var isExpanded by remember { mutableStateOf(false) }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded }
                            .testTag("flight_result_card_${flight.flightNumber.replace(" ", "_")}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "${flight.departureTime} ➔ ${flight.arrivalTime}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AlaskaDarkCharcoal
                                    )
                                    Text(
                                        text = "${flight.duration} • ${flight.stops}",
                                        fontSize = 13.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = "from $${flight.saverPrice.toInt()}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AlaskaPacificBlue
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${flight.flightNumber} • ${flight.aircraft}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "Details",
                                    fontSize = 12.sp,
                                    color = AlaskaWarmAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (isExpanded) {
                                Divider(modifier = Modifier.padding(vertical = 12.dp))
                                Text(
                                    text = "Fare options for this flight:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AlaskaDeepBlue
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Saver option
                                    FareSelectorBlock(
                                        name = "Saver",
                                        price = flight.saverPrice,
                                        onClick = { viewModel.selectFlightForBooking(flight); viewModel.selectFare("Saver", flight.saverPrice) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    // Main option
                                    FareSelectorBlock(
                                        name = "Main",
                                        price = flight.mainPrice,
                                        onClick = { viewModel.selectFlightForBooking(flight); viewModel.selectFare("Main", flight.mainPrice) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    // First option
                                    FareSelectorBlock(
                                        name = "First",
                                        price = flight.firstPrice,
                                        onClick = { viewModel.selectFlightForBooking(flight); viewModel.selectFare("First", flight.firstPrice) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FareSelectorBlock(name: String, price: Double, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        color = AlaskaSoftGrey,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .clickable { onClick() }
            .testTag("fare_block_${name.lowercase()}")
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AlaskaDarkCharcoal)
            Spacer(modifier = Modifier.height(4.dp))
            Text("$${price.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AlaskaPacificBlue)
        }
    }
}

@Composable
fun FareSelectionStepView(viewModel: AppViewModel, flight: FlightResult?) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        SectionHeader(title = "COMPARE FARES")
        Spacer(modifier = Modifier.height(16.dp))

        flight?.let { f ->
            val fareBenefits = listOf(
                FareBenefit("Carry-on baggage", true, true, true),
                FareBenefit("Seat selection", false, true, true),
                FareBenefit("Refundable / Flexible", false, false, true),
                FareBenefit("Priority Boarding", false, false, true),
                FareBenefit("Main cabin seating", false, true, false),
                FareBenefit("First class luxury", false, false, true)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    // Fare overview cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FareComparisonHeaderCard("SAVER", f.saverPrice, "No seat choice", Modifier.weight(1f), viewModel.selectedFareClass.value == "Saver") {
                            viewModel.selectFare("Saver", f.saverPrice)
                        }
                        FareComparisonHeaderCard("MAIN", f.mainPrice, "Seat selection", Modifier.weight(1f), viewModel.selectedFareClass.value == "Main") {
                            viewModel.selectFare("Main", f.mainPrice)
                        }
                        FareComparisonHeaderCard("FIRST", f.firstPrice, "Luxury travel", Modifier.weight(1f), viewModel.selectedFareClass.value == "First") {
                            viewModel.selectFare("First", f.firstPrice)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("BENEFITS COMPARISON", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }

                items(fareBenefits) { benefit ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AlaskaSoftGrey),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(benefit.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AlaskaDarkCharcoal)
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Icon(
                                    imageVector = if (benefit.saver) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = "Saver",
                                    tint = if (benefit.saver) Color(0xFF1B5E20) else Color.LightGray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Icon(
                                    imageVector = if (benefit.main) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = "Main",
                                    tint = if (benefit.main) Color(0xFF1B5E20) else Color.LightGray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Icon(
                                    imageVector = if (benefit.first) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = "First",
                                    tint = if (benefit.first) Color(0xFF1B5E20) else Color.LightGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AlaskaSecondaryButton(
                    text = "BACK",
                    onClick = { viewModel.currentBookStep.value = BookStep.FLIGHT_RESULTS },
                    modifier = Modifier.weight(1f)
                )
                AlaskaButton(
                    text = "CONTINUE",
                    onClick = { viewModel.currentBookStep.value = BookStep.PASSENGERS },
                    modifier = Modifier.weight(1f),
                    testTag = "fare_continue_button"
                )
            }
        }
    }
}

@Composable
fun FareComparisonHeaderCard(
    name: String,
    price: Double,
    caption: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) AlaskaDeepBlue else AlaskaSoftGrey,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, if (isSelected) AlaskaWarmAccent else Color(0xFFE5E5E5)),
        modifier = modifier
            .clickable { onClick() }
            .height(100.dp)
            .testTag("fare_comparison_card_${name.lowercase()}")
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text("$${price.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = if (isSelected) AlaskaIceBlue else AlaskaPacificBlue)
            Spacer(modifier = Modifier.height(2.dp))
            Text(caption, fontSize = 10.sp, color = if (isSelected) Color.LightGray else Color.Gray)
        }
    }
}

@Composable
fun PassengersStepView(viewModel: AppViewModel) {
    var name by remember { mutableStateOf(viewModel.passengerName.value) }
    var email by remember { mutableStateOf("tom@ahyx.org") }
    var phone by remember { mutableStateOf("+1 206 555-0199") }
    var tsaPreCheck by remember { mutableStateOf("K1092837482") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        SectionHeader(title = "PASSENGER INFORMATION")
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("FULL NAME (As on ID)") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("passenger_name_input")
                )
            }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("EMAIL") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("passenger_email_input")
                )
            }

            item {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("PHONE") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("passenger_phone_input")
                )
            }

            item {
                OutlinedTextField(
                    value = tsaPreCheck,
                    onValueChange = { tsaPreCheck = it },
                    label = { Text("TSA PRECHECK / KNOWN TRAVELER NUMBER") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("passenger_tsa_input")
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AlaskaSecondaryButton(
                text = "BACK",
                onClick = { viewModel.currentBookStep.value = BookStep.FARE_SELECTION },
                modifier = Modifier.weight(1f)
            )
            AlaskaButton(
                text = "NEXT",
                onClick = {
                    viewModel.submitPassengers(name)
                },
                modifier = Modifier.weight(1f),
                testTag = "passenger_submit_button"
            )
        }
    }
}

@Composable
fun SeatsStepView(viewModel: AppViewModel, selectedSeat: String) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        SectionHeader(title = "SELECT SEAT")
        Spacer(modifier = Modifier.height(16.dp))

        // Seat Map scrollable rows
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Seat Map rows list
            LazyColumn(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("FIRST CLASS CABIN", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }

                items((1..3).toList()) { row ->
                    SeatRowItem(row, viewModel, selectedSeat)
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("MAIN CABIN", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }

                items((10..18).toList()) { row ->
                    SeatRowItem(row, viewModel, selectedSeat)
                }
            }

            // Legend / Summary pane
            Column(
                modifier = Modifier.width(100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("LEGEND", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                
                LegendItem("Available", AlaskaIceBlue)
                LegendItem("Premium", AlaskaWarmAccent)
                LegendItem("Occupied", Color.LightGray)
                LegendItem("Selected", AlaskaPacificBlue)

                Spacer(modifier = Modifier.height(12.dp))
                Text("YOUR SEAT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text(
                    selectedSeat.ifEmpty { "None" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AlaskaPacificBlue
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AlaskaSecondaryButton(
                text = "BACK",
                onClick = { viewModel.currentBookStep.value = BookStep.PASSENGERS },
                modifier = Modifier.weight(1f)
            )
            AlaskaButton(
                text = "CONFIRM SEAT",
                onClick = { viewModel.confirmSeat() },
                enabled = selectedSeat.isNotEmpty(),
                modifier = Modifier.weight(1f),
                testTag = "seat_confirm_button"
            )
        }
    }
}

@Composable
fun SeatRowItem(rowNum: Int, viewModel: AppViewModel, selectedSeat: String) {
    val seats = if (rowNum <= 3) listOf("A", "B", "C", "D") else listOf("A", "B", "C", "D", "E", "F")
    
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = String.format("%02d", rowNum),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.width(18.dp)
        )

        seats.forEachIndexed { index, letter ->
            val seatCode = "$rowNum$letter"
            val isOccupied = viewModel.occupiedSeats.contains(seatCode)
            val isPremium = viewModel.premiumSeats.contains(seatCode)
            val isSelected = selectedSeat == seatCode

            // Subtly animate seat selection transitions
            val animColor by animateColorAsState(
                targetValue = when {
                    isSelected -> AlaskaPacificBlue
                    isOccupied -> Color.LightGray
                    isPremium -> AlaskaWarmAccent.copy(alpha = 0.25f)
                    else -> AlaskaIceBlue
                },
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
            )
            val animScale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else 1.0f
            )

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .scale(animScale)
                    .clip(RoundedCornerShape(6.dp))
                    .background(animColor)
                    .clickable(enabled = !isOccupied) { viewModel.selectSeat(seatCode) }
                    .border(
                        width = if (isSelected) 2.dp else if (isPremium) 1.dp else 0.dp,
                        color = if (isSelected) AlaskaDeepBlue else if (isPremium) AlaskaWarmAccent else Color.Transparent,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .testTag("seat_button_$seatCode"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else if (isOccupied) Color.DarkGray else AlaskaDeepBlue
                )
            }

            // Aisle spacer in the middle of standard Boeing 737 rows
            if (rowNum > 3 && index == 2) {
                Spacer(modifier = Modifier.width(12.dp))
            } else if (rowNum <= 3 && index == 1) {
                Spacer(modifier = Modifier.width(24.dp))
            }
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontSize = 11.sp, color = AlaskaDarkCharcoal, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun BagsStepView(viewModel: AppViewModel, checkedBagsCount: Int) {
    var count by remember { mutableStateOf(checkedBagsCount) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        SectionHeader(title = "CHECKED BAGGAGE")
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = AlaskaSoftGrey),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Alaska Checked Bags Policy",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AlaskaDeepBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "First checked bag is $30. Carry-on bags are free of charge. MVP Gold and First Class travelers enjoy complimentary checked bags.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("CHECKED BAGS", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AlaskaDarkCharcoal)
                Text("Total: $${count * 30}", fontSize = 13.sp, color = AlaskaPacificBlue, fontWeight = FontWeight.Bold)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { if (count > 0) count-- },
                    modifier = Modifier.testTag("bag_minus_button")
                ) {
                    Icon(imageVector = Icons.Default.RemoveCircleOutline, contentDescription = "Minus")
                }
                Text(
                    text = count.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp).testTag("bags_count_display")
                )
                IconButton(
                    onClick = { count++ },
                    modifier = Modifier.testTag("bag_plus_button")
                ) {
                    Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = "Plus")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AlaskaSecondaryButton(
                text = "BACK",
                onClick = { viewModel.currentBookStep.value = BookStep.SEATS },
                modifier = Modifier.weight(1f)
            )
            AlaskaButton(
                text = "PROCEED TO PAYMENT",
                onClick = { viewModel.confirmBags(count) },
                modifier = Modifier.weight(1f),
                testTag = "bags_confirm_button"
            )
        }
    }
}

@Composable
fun PaymentStepView(
    viewModel: AppViewModel,
    flight: FlightResult?,
    fareClass: String,
    farePrice: Double,
    bagsCount: Int
) {
    var cardNum by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    val basePrice = farePrice
    val bagsPrice = bagsCount * 30.0
    val totalPrice = basePrice + bagsPrice

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        SectionHeader(title = "SECURE PAYMENT")
        Spacer(modifier = Modifier.height(16.dp))

        // Price details Summary
        Card(
            colors = CardDefaults.cardColors(containerColor = AlaskaSoftGrey),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("TRIP PRICE BREAKDOWN", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("$fareClass Cabin Base Fare", fontSize = 13.sp, color = AlaskaDarkCharcoal)
                    Text("$${basePrice.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AlaskaDarkCharcoal)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Checked Bags ($bagsCount)", fontSize = 13.sp, color = AlaskaDarkCharcoal)
                    Text("$${bagsPrice.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AlaskaDarkCharcoal)
                }
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total (USD)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AlaskaDeepBlue)
                    Text("$${totalPrice.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = AlaskaWarmAccent)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card input form
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = cardNum,
                onValueChange = { cardNum = it },
                label = { Text("CREDIT CARD NUMBER") },
                leadingIcon = { Icon(Icons.Default.CreditCard, "Card") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("payment_card_input")
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = expiry,
                    onValueChange = { expiry = it },
                    label = { Text("EXPIRY (MM/YY)") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("payment_expiry_input")
                )

                OutlinedTextField(
                    value = cvv,
                    onValueChange = { cvv = it },
                    label = { Text("CVV") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("payment_cvv_input")
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AlaskaSecondaryButton(
                text = "BACK",
                onClick = { viewModel.currentBookStep.value = BookStep.BAGS },
                modifier = Modifier.weight(1f)
            )
            AlaskaButton(
                text = "PAY & BOOK NOW",
                onClick = { viewModel.submitPayment(cardNum, expiry, cvv) },
                enabled = cardNum.isNotEmpty() && expiry.isNotEmpty() && cvv.isNotEmpty(),
                modifier = Modifier.weight(1f),
                testTag = "payment_submit_button"
            )
        }
    }
}

@Composable
fun ConfirmationStepView(
    viewModel: AppViewModel,
    flight: FlightResult?,
    seat: String,
    onShowBoardingPass: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = Color(0xFF1B5E20),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "BOOKING CONFIRMED!",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AlaskaDeepBlue
        )
        Text(
            text = "Your adventure with Alaska Airlines is booked.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = AlaskaSoftGrey),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Flight", fontSize = 13.sp, color = Color.Gray)
                    Text(flight?.flightNumber ?: "AS 123", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AlaskaDarkCharcoal)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Route", fontSize = 13.sp, color = Color.Gray)
                    Text("${flight?.origin ?: "SEA"} ➔ ${flight?.destination ?: "ANC"}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AlaskaDarkCharcoal)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Seat Assigned", fontSize = 13.sp, color = Color.Gray)
                    Text(seat.ifEmpty { "14B" }, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AlaskaPacificBlue)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Confirmation Code", fontSize = 13.sp, color = Color.Gray)
                    Text("AK98S2", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AlaskaWarmAccent)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        AlaskaButton(
            text = "VIEW BOARDING PASS",
            onClick = onShowBoardingPass,
            testTag = "view_boarding_pass_confirm_button"
        )
        Spacer(modifier = Modifier.height(12.dp))
        AlaskaSecondaryButton(
            text = "RETURN HOME",
            onClick = { viewModel.resetBookingFlow() },
            testTag = "return_home_confirm_button"
        )
    }
}
