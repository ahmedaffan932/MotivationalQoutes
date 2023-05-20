package com.example.motivational.qoutes.database

import androidx.lifecycle.LiveData

class QuotRepository(private val myDao: QuotDao) {
    val readAllData: LiveData<List<QuotModel>> = myDao.readAllVideos()

    fun getAllCats(): List<String>{
       return myDao.getAllCategories()
    }

    suspend fun insertUsers(users: List<QuotModel>){
        myDao.insertUsers(users)
    }
    suspend fun updateQoute(qoute: QuotModel){
        myDao.updateQoute(qoute)
    }
    fun readByCat(cat:String): List<QuotModel>{
        return myDao.readByCat(cat)
    }
    fun readAllFav(): List<QuotModel>{
        return myDao.readAllFav()
    }
    fun getRandomObject(): QuotModel{
        return myDao.getRandomObject()
    }

}