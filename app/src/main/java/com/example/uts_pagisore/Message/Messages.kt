package com.example.uts_pagisore.Message

data class Message(
    val title: String = "",
    val description: String = "",
    val time: String = "",
    val shopId: String = "", // Add shopId to track message source
    val id: String = "" // Add unique message ID
)