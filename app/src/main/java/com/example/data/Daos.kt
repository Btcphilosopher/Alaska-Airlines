package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_loyalty WHERE id = :id LIMIT 1")
    fun getUserLoyalty(id: Int = 1): Flow<UserLoyaltyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserLoyalty(loyalty: UserLoyaltyEntity)
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM flight_bookings ORDER BY id DESC")
    fun getAllBookings(): Flow<List<FlightBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: FlightBookingEntity)

    @Delete
    suspend fun deleteBooking(booking: FlightBookingEntity)

    @Query("DELETE FROM flight_bookings")
    suspend fun deleteAllBookings()
}

@Dao
interface BoardingPassDao {
    @Query("SELECT * FROM boarding_passes WHERE id = :id LIMIT 1")
    fun getBoardingPass(id: Int = 1): Flow<BoardingPassEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoardingPass(boardingPass: BoardingPassEntity)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("DELETE FROM notifications")
    suspend fun deleteAllNotifications()
}
