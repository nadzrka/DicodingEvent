@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nadzirakarimantika.dicodingevent.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM event where status = 1")
    fun getUpcomingEvent(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event where status = 0")
    fun getFinishedEvent(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event where bookmarked = 1")
    fun getBookmarkedEvent(): LiveData<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEvent(event: List<EventEntity>)

    @Update
    fun updateEvent(event: EventEntity)

    @Query("SELECT * FROM event WHERE name LIKE :query")
    fun searchEvents(query: String): LiveData<List<EventEntity>>

    @Query("DELETE FROM event WHERE bookmarked = 0")
    fun deleteAll()

    @Query("DELETE FROM event WHERE status = 1")
    fun deleteUpcomingEvents()

    @Query("DELETE FROM event WHERE status = 0")
    fun deleteFinishedEvents()

    @Query("SELECT * FROM event WHERE id = :eventId")
    fun getEventById(eventId: String): LiveData<EventEntity>

    @Query("SELECT EXISTS(SELECT * FROM event WHERE name = :title AND bookmarked = 1)")
    fun isEventBookmarked(title: String): Boolean
}