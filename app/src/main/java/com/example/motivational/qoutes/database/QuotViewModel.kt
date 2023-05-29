package com.example.motivational.qoutes.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuotViewModel(application: Application) : AndroidViewModel(application)  {
    val readAllData: LiveData<List<QuotModel>>
    private val repository: QuotRepository

    init {
        val myDao = AppDatabase.getInstance(application).userDao()
        repository = QuotRepository(myDao)
        readAllData = repository.readAllData
    }

    suspend fun getAllCats() :List<String> {
        return repository.getAllCats()
    }

    fun insertUsers(users: List<QuotModel>){
        CoroutineScope(Dispatchers.IO).launch {
            repository.insertUsers(users)
        }
    }
    suspend fun updateQoute(qoute: QuotModel){
        CoroutineScope(Dispatchers.IO).launch {
            repository.updateQoute(qoute)
        }
    }

    suspend fun readByCat(cat:String): List<QuotModel>{
        return repository.readByCat(cat)
    }

    suspend fun readAllFav(): List<QuotModel>{
        return repository.readAllFav()
    }
    fun getRandomObject(): QuotModel{
        return repository.getRandomObject()
    }

}