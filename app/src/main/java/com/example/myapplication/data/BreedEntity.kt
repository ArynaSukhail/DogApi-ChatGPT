package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Entity(tableName = "breeds")
data class BreedEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val temperament: String,
    val weight: String,
    val height: String,
    val imageUrl: String
)
