package com.example.gameapp.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import com.example.gameapp.R

class Shoot(context: Context, screenX: Int, screenY: Int, val positionX: Float) {
    var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.shooting_star)
    val width = screenX / 15f
    val height = screenY / 12f
    var positionY = 1400
    var speed = -20
    var hitbox = RectF()


    init{
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(),false)
        hitbox.left = positionX
        hitbox.top = positionY.toFloat()
        hitbox.right = hitbox.left + width
        hitbox.bottom = hitbox.top + height
    }

    fun updateShoot(){
        positionY += speed
        hitbox.top = positionY.toFloat()
        hitbox.bottom = hitbox.top + height
    }



}