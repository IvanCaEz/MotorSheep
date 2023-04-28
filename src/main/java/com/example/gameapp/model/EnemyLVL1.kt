package com.example.gameapp.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import com.example.gameapp.R
import java.util.*

class EnemyLVL1(context: Context, screenX: Int, screenY: Int): Enemy {
    override var lives = 1
    override var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.robot)
    override var destroyedBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.explosion_icon)
    override val width = screenX / 8f
    override val height = screenY / 10f
    override var positionY = (height.toInt()..(screenY/2)).random()
    val screenXLimit = screenX
    override var positionX = (width.toInt()..screenX-width.toInt()).random()

    override var speed = 10
    override var hitbox = RectF()

    init {
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), false)
        hitbox.left = positionX.toFloat()
        hitbox.top = positionY.toFloat()
        hitbox.right = hitbox.left + width
        hitbox.bottom = hitbox.top + height
    }

    override fun updateEnemy() {
        if (lives == 0){
            bitmap = Bitmap.createScaledBitmap(destroyedBitmap, width.toInt(), height.toInt(), false)
        }
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

    override fun shoot() =  Random().nextDouble() < 0.3

}