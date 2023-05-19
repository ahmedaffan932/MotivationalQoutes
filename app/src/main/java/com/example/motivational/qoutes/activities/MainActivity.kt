package com.example.motivational.qoutes.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.adapters.AdapterCategories
import com.example.motivational.qoutes.databinding.ActivityMainBinding
import com.example.motivational.qoutes.interfaces.InterfaceCatClick

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFav.setOnClickListener {

        }
//        binding.recyclerPopularCats.isNestedScrollingEnabled = false
        binding.recyclerPopularCats.layoutManager=GridLayoutManager(this,3)
        binding.recyclerPopularCats.adapter=AdapterCategories(this,object :InterfaceCatClick{
            override fun onClick(catName: String) {
                Toast.makeText(this@MainActivity,catName,Toast.LENGTH_SHORT).show()
            }

        })


    }
}