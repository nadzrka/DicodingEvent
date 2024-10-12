package com.nadzirakarimantika.dicodingevent.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nadzirakarimantika.dicodingevent.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM event ORDER BY beginTime DESC")
    fun getEvent(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event where bookmarked = 1")
    fun getBookmarkedEvent(): LiveData<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEvent(event: List<EventEntity>)

    @Update
    fun updateEvent(event: EventEntity)

    @Query("DELETE FROM event WHERE bookmarked = 0")
    fun deleteAll()

    @Query("SELECT EXISTS(SELECT * FROM event WHERE name = :title AND bookmarked = 1)")
    fun isEventBookmarked(title: String): Boolean
}