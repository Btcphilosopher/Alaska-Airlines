package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_loyalty")
data class UserLoyaltyEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val status: String,
    val miles: Int,
    val nextStatusMiles: Int
)

@Entity(tableName = "flight_bookings")
data class FlightBookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val date: String,
    val departureTime: String,
    val arrivalTime: String,
    val price: Double,
    val cabin: String,
    val fareClass: String,
    val seat: String,
    val status: String,
    val isImminent: Boolean = false
)

@Entity(tableName = "boarding_passes")
data class BoardingPassEntity(
    @PrimaryKey val id: Int = 1,
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val date: String,
    val departureTime: String,
    val arrivalTime: String,
    val seat: String,
    val gate: String,
    val boardingTime: String,
    val barcodePayload: String
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String, // GATE_CHANGE, BOARDING, BAGGAGE
    val read: Boolean = false
)
