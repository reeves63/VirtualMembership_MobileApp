package com.example.uts_pagisore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShopAdapter(private val shops: List<Shop>, private val clickListener: (Shop) -> Unit) :
    RecyclerView.Adapter<ShopAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val shopName: TextView = view.findViewById(R.id.textShopName)
        val shopDescription: TextView = view.findViewById(R.id.textShopDescription)
        val shopCategory: TextView = view.findViewById(R.id.textShopCategory)

        fun bind(shop: Shop, clickListener: (Shop) -> Unit) {
            shopName.text = shop.name
            shopDescription.text = shop.description
            shopCategory.text = shop.categories
            itemView.setOnClickListener { clickListener(shop) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(shops[position], clickListener)
    }

    override fun getItemCount(): Int = shops.size
}
