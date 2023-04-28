package com.example.gameapp.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import com.example.gameapp.R

class BossShoot(context: Context, screenX: Int, screenY: Int, val positionX: Float, initialPositionY: Float) {
    var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.scissors_big_icon)
    val width = screenX / 5f
    val height = screenY / 7f
    var speed = -22
    var positionY = initialPositionY
    var hitbox = RectF()


    init{
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(),false)
        hitbox.left = positionX
        hitbox.top = positionY
        hitbox.right = hitbox.left + width
        hitbox.bottom = hitbox.top + height
    }

    fun updateShoot(){
        positionY -= speed
        hitbox.top = positionY
        hitbox.bottom = hitbox.top + height
    }



}