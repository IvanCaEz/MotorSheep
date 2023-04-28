package com.example.gameapp.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import com.example.gameapp.R

class EnemyShoot(context: Context, screenX: Int, screenY: Int, val positionX: Float,  initialPositionY: Float) {
    var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.gear_icon)
    val width = screenX / 12f
    val height = screenY / 15f
    var speed = -15
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