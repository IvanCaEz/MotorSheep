package com.example.gameapp.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF

interface Enemy {
    val bitmap: Bitmap
    var destroyedBitmap: Bitmap
    val width: Float
    val height: Float
    var positionX: Int
    var positionY: Int
    var lives: Int
    var speed: Int
    var hitbox: RectF

    fun updateEnemy()
    fun shoot(): Boolean


}