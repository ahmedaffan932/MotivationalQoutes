package com.example.motivational.qoutes.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface QuotDao {
    @Insert
    suspend fun insertUsers(users: List<QuotModel>)

    @Query("SELECT DISTINCT Category FROM quots")
    fun getAllCategories(): List<String>

    @Query("SELECT * FROM quots ORDER BY id ASC")
    fun readAllVideos(): LiveData<List<QuotModel>>

    @Query("SELECT * FROM quots WHERE Category =:cat")
    fun readByCat(cat:String): List<QuotModel>

    @Query("SELECT * FROM quots WHERE isFav = 1")
    fun readAllFav(): List<QuotModel>

    @Update
    suspend fun updateQoute(qoute: QuotModel)

    @Query("SELECT * FROM quots ORDER BY RANDOM() LIMIT 1")
    fun getRandomObject(): QuotModel

}