package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.PlannerEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannerEventDao {
    @Query("SELECT * FROM planner_events ORDER BY date ASC, startTime ASC")
    fun getAllEvents(): Flow<List<PlannerEventEntity>>

    @Query("SELECT * FROM planner_events WHERE date = :date ORDER BY startTime ASC")
    fun getEventsByDate(date: String): Flow<List<PlannerEventEntity>>

    @Query("SELECT * FROM planner_events WHERE id = :id LIMIT 1")
    suspend fun getEventById(id: Long): PlannerEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: PlannerEventEntity): Long

    @Update
    suspend fun updateEvent(event: PlannerEventEntity)

    @Delete
    suspend fun deleteEvent(event: PlannerEventEntity)

    @Query("DELETE FROM planner_events WHERE id = :id")
    suspend fun deleteEventById(id: Long)

    @Query("UPDATE planner_events SET isDone = :isDone WHERE id = :id")
    suspend fun updateEventStatus(id: Long, isDone: Boolean)

    @Query("SELECT COUNT(*) FROM planner_events")
    suspend fun getCount(): Int
}
