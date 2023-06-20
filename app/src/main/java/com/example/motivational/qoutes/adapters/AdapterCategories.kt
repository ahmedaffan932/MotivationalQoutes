package com.example.motivational.qoutes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.database.Category
import com.example.motivational.qoutes.databinding.ItemCatBinding
import com.example.motivational.qoutes.interfaces.InterfaceCatClick
import com.example.motivational.qoutes.utils.UtilMiscs

class AdapterCategories(var context: Context, var arrCategories: ArrayList<Category>, var clickListener: InterfaceCatClick) :
    RecyclerView.Adapter<AdapterCategories.ViewHolderClass>() {

    class ViewHolderClass(val binding: ItemCatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(
            ItemCatBinding.bind(
                LayoutInflater.from(parent.context).inflate(R.layout.item_cat, parent, false)
            )
        )
    }

    override fun getItemCount(): Int {
        return arrCategories.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        UtilMiscs.zoomInView(holder.binding.root, context, 250)
        holder.binding.catImage.setImageResource(arrCategories[position].catWallpaper)
        holder.binding.catName.text = arrCategories[position].catName
        holder.binding.root.setOnClickListener { clickListener.onClick(arrCategories[position].catName) }
    }
}