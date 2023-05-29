package com.example.motivational.qoutes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.databinding.ItemCatBinding
import com.example.motivational.qoutes.databinding.ItemQuotBinding
import com.example.motivational.qoutes.interfaces.InterfaceCatClick
import com.example.motivational.qoutes.interfaces.InterfaceQuotClick
import com.example.motivational.qoutes.utils.UtilLists
import com.example.motivational.qoutes.utils.UtilMiscs

class AdapterQouts(var context: Context,
                   var myList:List<QuotModel>,
                   var clickListener: InterfaceQuotClick
)
    : RecyclerView.Adapter<AdapterQouts.ViewHolderClass>() {

    class ViewHolderClass(val binding:ItemQuotBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(ItemQuotBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.item_quot,parent,false)))
    }

    override fun getItemCount(): Int {
        return myList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        try {
            holder.binding.catImage.setImageResource(UtilLists.wallpapers[myList[position].wall])
            holder.binding.catText.text=myList[position].Quote
            holder.binding.root.setOnClickListener {
                clickListener.onClick(myList[position])
            }
        }catch (exc:Exception){
            exc.printStackTrace()
        }

    }
}