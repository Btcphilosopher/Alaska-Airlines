package com.example.services

import com.example.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

// Domain Models
data class FlightResult(
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val stops: String = "NONSTOP",
    val aircraft: String = "737 MAX",
    val saverPrice: Double,
    val mainPrice: Double,
    val firstPrice: Double,
    val baggageInfo: String = "Carry-on included for all fares. 1st checked bag free for First Class."
)

data class FareBenefit(
    val title: String,
    val saver: Boolean,
    val main: Boolean,
    val first: Boolean
)

data class AirportInfo(
    val code: String,
    val name: String,
    val terminal: String,
    val gate: String,
    val securityStatus: String, // e.g. "Normal (10-15 min wait)"
    val lounges: List<String>,
    val baggageClaim: String,
    val amenities: List<String>
)

// Interfaces as requested by the user specifications
interface FlightSearchService {
    suspend fun searchFlights(from: String, to: String, date: String): List<FlightResult>
}

interface BookingService {
    fun getAllBookings(): Flow<List<FlightBookingEntity>>
    suspend fun bookFlight(booking: FlightBookingEntity)
    suspend fun updateSeat(bookingId: Int, seat: String)
    suspend fun clearAllBookings()
}

interface TripService {
    fun getNextTrip(): Flow<FlightBookingEntity?>
}

interface FlightStatusService {
    suspend fun getFlightStatus(flightNumber: String): FlightBookingEntity?
}

interface AirportService {
    fun getAirportInfo(code: String): AirportInfo
}

interface LoyaltyService {
    fun getLoyaltyInfo(): Flow<UserLoyaltyEntity?>
    suspend fun updateLoyalty(loyalty: UserLoyaltyEntity)
}

interface NotificationService {
    fun getNotifications(): Flow<List<NotificationEntity>>
    suspend fun addNotification(title: String, content: String, type: String)
    suspend fun clearNotifications()
}

// Implementations of the above Services interacting with Room DAOs for Offline-First Data
class FlightSearchServiceImpl : FlightSearchService {
    private val allMockFlights = listOf(
        // Seattle to Anchorage
        FlightResult("AS 123", "SEA", "ANC", "08:15", "11:02", "3h 47m", "NONSTOP", "737 MAX 9", 189.0, 249.0, 499.0),
        FlightResult("AS 456", "SEA", "ANC", "12:30", "15:18", "3h 48m", "NONSTOP", "737-900ER", 199.0, 269.0, 529.0),
        FlightResult("AS 789", "SEA", "ANC", "17:45", "20:34", "3h 49m", "NONSTOP", "737 MAX 8", 179.0, 239.0, 479.0),
        
        // Anchorage to Seattle
        FlightResult("AS 321", "ANC", "SEA", "09:00", "13:35", "3h 35m", "NONSTOP", "737 MAX 9", 189.0, 249.0, 499.0),
        FlightResult("AS 654", "ANC", "SEA", "14:15", "18:52", "3h 37m", "NONSTOP", "737 MAX 8", 199.0, 259.0, 519.0),

        // Portland to Seattle
        FlightResult("AS 502", "PDX", "SEA", "07:00", "07:50", "0h 50m", "NONSTOP", "Embraer 175", 89.0, 119.0, 219.0),
        FlightResult("AS 504", "PDX", "SEA", "11:30", "12:22", "0h 52m", "NONSTOP", "Embraer 175", 99.0, 129.0, 239.0),

        // San Francisco to Seattle
        FlightResult("AS 210", "SFO", "SEA", "10:15", "12:28", "2h 13m", "NONSTOP", "737 MAX 9", 129.0, 169.0, 349.0),
        FlightResult("AS 212", "SFO", "SEA", "16:40", "18:55", "2h 15m", "NONSTOP", "737-900ER", 139.0, 189.0, 379.0)
    )

    override suspend fun searchFlights(from: String, to: String, date: String): List<FlightResult> {
        // Filter mock flights or generate realistic ones
        val filtered = allMockFlights.filter { 
            it.origin.equals(from, ignoreCase = true) && 
            it.destination.equals(to, ignoreCase = true) 
        }
        if (filtered.isNotEmpty()) return filtered
        
        // Generate on-the-fly realistic flights if user searches other cities
        val fUpper = from.uppercase()
        val tUpper = to.uppercase()
        return listOf(
            FlightResult("AS 811", fUpper, tUpper, "09:30", "12:45", "3h 15m", "NONSTOP", "737 MAX 9", 159.0, 219.0, 459.0),
            FlightResult("AS 815", fUpper, tUpper, "15:10", "18:25", "3h 15m", "NONSTOP", "737-800", 149.0, 199.0, 419.0)
        )
    }
}

class BookingServiceImpl(
    private val bookingDao: BookingDao,
    private val boardingPassDao: BoardingPassDao
) : BookingService {
    override fun getAllBookings(): Flow<List<FlightBookingEntity>> = bookingDao.getAllBookings()

    override suspend fun bookFlight(booking: FlightBookingEntity) {
        bookingDao.insertBooking(booking)
        // Automatically create boarding pass for the booked flight!
        val bPass = BoardingPassEntity(
            id = if (booking.id == 0) 1 else booking.id,
            flightNumber = booking.flightNumber,
            origin = booking.origin,
            destination = booking.destination,
            date = booking.date,
            departureTime = booking.departureTime,
            arrivalTime = booking.arrivalTime,
            seat = booking.seat.ifEmpty { "12D" },
            gate = "C14",
            boardingTime = calculateBoardingTime(booking.departureTime),
            barcodePayload = "AS-${booking.flightNumber}-${booking.origin}-${booking.destination}-${booking.seat}"
        )
        boardingPassDao.insertBoardingPass(bPass)
    }

    override suspend fun updateSeat(bookingId: Int, seat: String) {
        // Since we can query and insert, let's find the booking, update, and insert
        // Or simplify for mock database
    }

    override suspend fun clearAllBookings() {
        bookingDao.deleteAllBookings()
    }

    private fun calculateBoardingTime(departureTime: String): String {
        // E.g., subtract 40 mins
        val parts = departureTime.split(":")
        if (parts.size == 2) {
            val hour = parts[0].toIntOrNull() ?: 8
            val min = parts[1].toIntOrNull() ?: 15
            var totalMin = hour * 60 + min - 40
            if (totalMin < 0) totalMin += 24 * 60
            val h = totalMin / 60
            val m = totalMin % 60
            return String.format("%02d:%02d", h, m)
        }
        return "07:35"
    }
}

class TripServiceImpl(private val bookingDao: BookingDao) : TripService {
    override fun getNextTrip(): Flow<FlightBookingEntity?> {
        return bookingDao.getAllBookings().map { list ->
            // Try to find imminent one or simply the first one
            list.firstOrNull { it.isImminent } ?: list.firstOrNull()
        }
    }
}

class FlightStatusServiceImpl : FlightStatusService {
    override suspend fun getFlightStatus(flightNumber: String): FlightBookingEntity? {
        val num = flightNumber.uppercase()
        return if (num == "AS 123" || num == "123") {
            FlightBookingEntity(
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
        } else {
            null
        }
    }
}

class AirportServiceImpl : AirportService {
    override fun getAirportInfo(code: String): AirportInfo {
        return when (code.uppercase()) {
            "SEA" -> AirportInfo(
                code = "SEA",
                name = "Seattle-Tacoma International Airport",
                terminal = "Main Terminal",
                gate = "C14",
                securityStatus = "Normal (8-12 min wait)",
                lounges = listOf("Alaska Lounge (Concourse C)", "Alaska Lounge (North Satellite)"),
                baggageClaim = "Carousel 12",
                amenities = listOf("Caffe Vita (C10)", "Sub Pop Records (Central Terminal)", "Beecher's Cheese (C3)")
            )
            "ANC" -> AirportInfo(
                code = "ANC",
                name = "Ted Stevens Anchorage Airport",
                terminal = "South Terminal",
                gate = "B4",
                securityStatus = "Light (3-5 min wait)",
                lounges = listOf("Alaska Lounge (Concourse C)"),
                baggageClaim = "Carousel 4",
                amenities = listOf("Silver Gulch Brewery (Concourse C)", "Alaska Native Art Shop")
            )
            else -> AirportInfo(
                code = code.uppercase(),
                name = "${code.uppercase()} International Airport",
                terminal = "Terminal 1",
                gate = "A12",
                securityStatus = "Normal (15 min wait)",
                lounges = listOf("Partner Lounge"),
                baggageClaim = "Carousel 3",
                amenities = listOf("Starbucks Coffee", "Grab & Go Market")
            )
        }
    }
}

class LoyaltyServiceImpl(private val userDao: UserDao) : LoyaltyService {
    override fun getLoyaltyInfo(): Flow<UserLoyaltyEntity?> = userDao.getUserLoyalty()

    override suspend fun updateLoyalty(loyalty: UserLoyaltyEntity) {
        userDao.insertUserLoyalty(loyalty)
    }
}

class NotificationServiceImpl(private val notificationDao: NotificationDao) : NotificationService {
    override fun getNotifications(): Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()

    override suspend fun addNotification(title: String, content: String, type: String) {
        notificationDao.insertNotification(
            NotificationEntity(title = title, content = content, type = type)
        )
    }

    override suspend fun clearNotifications() {
        notificationDao.deleteAllNotifications()
    }
}
