package com.nadzirakarimantika.dicodingevent.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
class EventEntity(
    @field:ColumnInfo(name = "name")
    @field:PrimaryKey
    val name: String,

    @field:ColumnInfo(name = "beginTime")
    val beginTime: String,

    @field:ColumnInfo(name = "imageLogo")
    val imageLogo: String,

    @field:ColumnInfo(name = "bookmarked")
    var isBookmarked: Boolean
)