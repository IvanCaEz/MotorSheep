package com.example.gameapp.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import com.example.gameapp.R

class Player(context: Context, screenX: Int, screenY: Int) {
    var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.sheep)
    val width = screenX / 8f
    val height = screenY / 10f
    var positionX = screenX / 2
    val screenXLimit = screenX
    var speed = 0
    var lives = 5
    var hitbox = RectF()


    init {
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), false)
        hitbox.left = positionX.toFloat()-(width/2)
        hitbox.top = 1500f
        hitbox.right = hitbox.left + width
        hitbox.bottom = hitbox.top + height
    }

    fun hitted(){
        lives -= 1
    }

    fun updatePlayer() {
        if (positionX <= 0) {
            positionX = 0 + width.toInt()/2
            speed = 0
        } else if (positionX >= screenXLimit) {
            positionX = screenXLimit - width.toInt()/2
            speed = 0
        } else positionX += speed
    }

}