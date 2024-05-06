package com.example.motivational.qoutes.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.motivational.qoutes.utils.Misc

@Entity(tableName = "quots")
data class QuotModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val Author: String,
    val Category: String,
    val Popularity: Double,
    val Quote: String,
    @TypeConverters(CustomObjectConverter::class)
    val Tags: List<String>,
    var isFav:Int=0,
    var wall:Int= Misc.getRandom()
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(Author)
        parcel.writeString(Category)
        parcel.writeDouble(Popularity)
        parcel.writeString(Quote)
        parcel.writeStringList(Tags)
        parcel.writeInt(isFav)
        parcel.writeInt(wall)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuotModel> {
        override fun createFromParcel(parcel: Parcel): QuotModel {
            return QuotModel(parcel)
        }

        override fun newArray(size: Int): Array<QuotModel?> {
            return arrayOfNulls(size)
        }
    }

}