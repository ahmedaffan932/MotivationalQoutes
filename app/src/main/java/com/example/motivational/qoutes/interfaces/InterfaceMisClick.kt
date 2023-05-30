package com.example.motivational.qoutes.interfaces

import com.example.motivational.qoutes.database.QuotModel

interface InterfaceMisClick {
    fun onMisTouch(model:QuotModel?):Boolean
    fun onWallChange(wall:Int)
}