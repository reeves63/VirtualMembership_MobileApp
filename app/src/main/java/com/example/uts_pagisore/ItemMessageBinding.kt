package com.example.uts_pagisore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

data class ItemMessageBinding(
    val root: View,
    val textTitle: TextView,
    val textDescription: TextView,
    val textTime: TextView
) {
    companion object {
        fun inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): ItemMessageBinding {
            val view = inflater.inflate(R.layout.item_messages, parent, attachToParent)
            return ItemMessageBinding(
                view,
                view.findViewById(R.id.textTitle),
                view.findViewById(R.id.textDescription),
                view.findViewById(R.id.textTime)
            )
        }
    }
}