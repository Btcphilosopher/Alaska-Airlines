package com.example.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.services.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab {
    HOME, TRIPS, BOOK, EXPLORE, ACCOUNT
}

enum class BookStep {
    SEARCH, FLIGHT_RESULTS, FARE_SELECTION, PASSENGERS, SEATS, BAGS, PAYMENT, CONFIRMATION
}

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    
    // Services
    val searchService: FlightSearchService = FlightSearchServiceImpl()
    val bookingService: BookingService = BookingServiceImpl(database.bookingDao(), database.boardingPassDao())
    val tripService: TripService = TripServiceImpl(database.bookingDao())
    val flightStatusService: FlightStatusService = FlightStatusServiceImpl()
    val airportService: AirportService = AirportServiceImpl()
    val loyaltyService: LoyaltyService = LoyaltyServiceImpl(database.userDao())
    val notificationService: NotificationService = NotificationServiceImpl(database.notificationDao())

    // UI States
    val currentTab = MutableStateFlow(AppTab.HOME)
    val currentBookStep = MutableStateFlow(BookStep.SEARCH)

    // Loyalty State
    val userLoyalty: StateFlow<UserLoyaltyEntity?> = loyaltyService.getLoyaltyInfo()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Bookings & Upcoming Trip
    val allBookings: StateFlow<List<FlightBookingEntity>> = bookingService.getAllBookings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val nextTrip: StateFlow<FlightBookingEntity?> = tripService.getNextTrip()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Notifications
    val notifications: StateFlow<List<NotificationEntity>> = notificationService.getNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Boarding Pass (calculated reactively or cached)
    val activeBoardingPass = database.boardingPassDao().getBoardingPass(1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Search Flow States
    val searchOrigin = MutableStateFlow("SEA")
    val searchDestination = MutableStateFlow("ANC")
    val searchDate = MutableStateFlow("Aug 28")
    val searchReturnDate = MutableStateFlow("Sep 05")
    val searchPassengers = MutableStateFlow("1 Adult")
    val searchCabin = MutableStateFlow("Main")
    
    val searchResults = MutableStateFlow<List<FlightResult>>(emptyList())
    val isSearching = MutableStateFlow(false)
    val searchError = MutableStateFlow<String?>(null)

    // Booking Wizard Selected Info
    val selectedFlight = MutableStateFlow<FlightResult?>(null)
    val selectedFareClass = MutableStateFlow("Main") // Saver, Main, First
    val selectedFarePrice = MutableStateFlow(0.0)
    val passengerName = MutableStateFlow("Tom")
    val selectedSeat = MutableStateFlow("")
    val checkedBagsCount = MutableStateFlow(0)
    val paymentCardNumber = MutableStateFlow("")
    val paymentCardExpiry = MutableStateFlow("")
    val paymentCardCVV = MutableStateFlow("")
    
    // Seat Selection Map Helper
    val occupiedSeats = setOf("12B", "12C", "11A", "11C", "10A", "10B", "14A", "14F", "15D", "15E")
    val premiumSeats = setOf("1A", "1B", "1C", "1D", "2A", "2B", "2C", "2D", "6A", "6B", "6C", "6F")
    
    // Live Flight Status State
    val flightStatusSearchNumber = MutableStateFlow("AS 123")
    val liveFlightStatus = MutableStateFlow<FlightBookingEntity?>(null)
    val isCheckingStatus = MutableStateFlow(false)

    // Airport Companion State
    val selectedAirportCode = MutableStateFlow("SEA")
    val selectedAirportInfo = MutableStateFlow<AirportInfo?>(null)

    // Connection Mode
    val isOffline = MutableStateFlow(false)

    init {
        // Populate standard mock data if empty
        viewModelScope.launch {
            // Check if loyalty table is empty
            userLoyalty.filterNotNull().collect {
                // Already populated
            }
        }
        
        // Seed default database state
        seedDatabase()
        updateAirportInfo("SEA")
    }

    private fun seedDatabase() {
        viewModelScope.launch {
            // Insert Loyalty Info
            loyaltyService.updateLoyalty(
                UserLoyaltyEntity(
                    id = 1,
                    name = "Tom",
                    status = "MVP GOLD",
                    miles = 42850,
                    nextStatusMiles = 18200
                )
            )

            // Insert initial imminent trip
            val defaultTrip = FlightBookingEntity(
                id = 1,
                flightNumber = "AS 123",
                origin = "SEA",
                destination = "ANC",
                date = "Aug 28",
                departureTime = "08:15",
                arrivalTime = "11:02",
                price = 249.0,
                cabin = "Main",
                fareClass = "Main Cabin",
                seat = "12A",
                status = "ON TIME",
                isImminent = true
            )
            
            // This will also auto-generate BoardingPass
            bookingService.bookFlight(defaultTrip)

            // Seed some notifications
            notificationService.clearNotifications()
            notificationService.addNotification(
                title = "GATE CHANGE",
                content = "AS 123 is now departing from Gate C16.",
                type = "GATE_CHANGE"
            )
            notificationService.addNotification(
                title = "BAGGAGE",
                content = "Your checked bag has arrived at Carousel 4.",
                type = "BAGGAGE"
            )
            notificationService.addNotification(
                title = "YOUR FLIGHT IS BOARDING",
                content = "Gate C14. Boarding began at 07:35.",
                type = "BOARDING"
            )
        }
    }

    fun toggleOfflineMode() {
        isOffline.value = !isOffline.value
    }

    fun performFlightSearch() {
        viewModelScope.launch {
            isSearching.value = true
            searchError.value = null
            try {
                val results = searchService.searchFlights(
                    searchOrigin.value.trim().uppercase(),
                    searchDestination.value.trim().uppercase(),
                    searchDate.value
                )
                if (results.isEmpty()) {
                    searchError.value = "No flights found matching your selection."
                } else {
                    searchResults.value = results
                    currentBookStep.value = BookStep.FLIGHT_RESULTS
                }
            } catch (e: Exception) {
                searchError.value = "Failed to search flights. Please try again."
            } finally {
                isSearching.value = false
            }
        }
    }

    fun selectFlightForBooking(flight: FlightResult) {
        selectedFlight.value = flight
        selectedSeat.value = ""
        checkedBagsCount.value = 0
        currentBookStep.value = BookStep.FARE_SELECTION
    }

    fun selectFare(className: String, price: Double) {
        selectedFareClass.value = className
        selectedFarePrice.value = price
        currentBookStep.value = BookStep.PASSENGERS
    }

    fun submitPassengers(name: String) {
        passengerName.value = name
        currentBookStep.value = BookStep.SEATS
    }

    fun selectSeat(seat: String) {
        selectedSeat.value = seat
    }

    fun confirmSeat() {
        currentBookStep.value = BookStep.BAGS
    }

    fun confirmBags(count: Int) {
        checkedBagsCount.value = count
        currentBookStep.value = BookStep.PAYMENT
    }

    fun submitPayment(card: String, expiry: String, cvv: String) {
        paymentCardNumber.value = card
        paymentCardExpiry.value = expiry
        paymentCardCVV.value = cvv
        
        // Execute booking and persist to Room database
        viewModelScope.launch {
            val flight = selectedFlight.value ?: return@launch
            val bookingId = (System.currentTimeMillis() % 100000).toInt()
            val newBooking = FlightBookingEntity(
                id = bookingId,
                flightNumber = flight.flightNumber,
                origin = flight.origin,
                destination = flight.destination,
                date = searchDate.value,
                departureTime = flight.departureTime,
                arrivalTime = flight.arrivalTime,
                price = selectedFarePrice.value + (checkedBagsCount.value * 30.0),
                cabin = selectedFareClass.value,
                fareClass = "${selectedFareClass.value} Cabin",
                seat = selectedSeat.value.ifEmpty { "14B" },
                status = "CONFIRMED",
                isImminent = true
            )
            
            bookingService.bookFlight(newBooking)
            
            // Add loyalty miles
            val distanceMiles = if (flight.origin == "SEA" && flight.destination == "ANC") 1448 else 500
            val currentLoyalty = userLoyalty.value
            if (currentLoyalty != null) {
                val newMiles = currentLoyalty.miles + distanceMiles
                val newNextMiles = maxOf(0, currentLoyalty.nextStatusMiles - distanceMiles)
                loyaltyService.updateLoyalty(
                    currentLoyalty.copy(miles = newMiles, nextStatusMiles = newNextMiles)
                )
            }

            // Push notification
            notificationService.addNotification(
                title = "BOOKING CONFIRMED",
                content = "Your flight ${flight.flightNumber} to ${flight.destination} is successfully booked! Seat: ${selectedSeat.value}",
                type = "BOARDING"
            )

            currentBookStep.value = BookStep.CONFIRMATION
        }
    }

    fun searchLiveFlightStatus(number: String) {
        viewModelScope.launch {
            isCheckingStatus.value = true
            val status = flightStatusService.getFlightStatus(number)
            liveFlightStatus.value = status
            isCheckingStatus.value = false
        }
    }

    fun updateAirportInfo(code: String) {
        selectedAirportCode.value = code
        selectedAirportInfo.value = airportService.getAirportInfo(code)
    }

    fun resetBookingFlow() {
        selectedFlight.value = null
        selectedSeat.value = ""
        checkedBagsCount.value = 0
        currentBookStep.value = BookStep.SEARCH
    }
}
