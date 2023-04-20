package com.example.gameapp.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import com.example.gameapp.R
import java.util.*

class Enemy(context: Context, screenX: Int, screenY: Int) {
    var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.robot)
    val width = screenX / 8f
    val height = screenY / 10f
    val randomStartPositionX = (width.toInt()..screenX-width.toInt()).random()
    val randomStartPositionY = (height.toInt()..(screenY/2)).random()
    val screenXLimit = screenX
    var positionX = randomStartPositionX
    var speed = 10
    var hitbox = RectF()

    init {
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), false)
        hitbox.left = positionX.toFloat()
        hitbox.top = randomStartPositionY.toFloat()
        hitbox.right = hitbox.left + width
        hitbox.bottom = hitbox.top + height
    }

    fun updateEnemy() {
        if (positionX <= 0) {
            positionX = 0 + width.toInt()/2
            speed = +10
        } else if (positionX >= screenXLimit) {
            positionX = screenXLimit - width.toInt()/2
            speed = -10
        } else positionX += speed
        hitbox.left = positionX.toFloat()-(width/2)
        hitbox.right = hitbox.left + width
    }

    fun shoot() =  Random().nextDouble() < 0.3

}