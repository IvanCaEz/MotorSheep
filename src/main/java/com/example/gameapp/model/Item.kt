package com.example.gameapp.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF

interface Item {
    val bitmap: Bitmap
    val width: Float
    val height: Float
    var speed: Int
    var positionX: Float
    var positionY: Float
    var hitbox: RectF
    val effect: Int
    fun updateItem()




}