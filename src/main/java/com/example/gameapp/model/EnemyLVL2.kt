package com.example.gameapp.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import com.example.gameapp.R
import java.util.*

class EnemyLVL2(context: Context, screenX: Int, screenY: Int): Enemy {
    override var lives = 2
    override var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.robot_two_icon)
    var enragedBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.robot_two_enraged)
    override var destroyedBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.explosion_icon)

    override val width = screenX / 6f
    override val height = screenY / 10f
    val screenXLimit = screenX
    override var positionX = (width.toInt()..screenX-width.toInt()).random()
    override var positionY = (height.toInt()..(screenY/2)).random()
    override var speed = 8
    override var hitbox = RectF()
    var enraged = false

    init {
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), false)
        hitbox.left = positionX.toFloat()
        hitbox.top = positionY.toFloat()
        hitbox.right = hitbox.left + width
        hitbox.bottom = hitbox.top + height
    }

    override fun updateEnemy() {
        if (lives == 1) {
            enraged = true
            bitmap = Bitmap.createScaledBitmap(enragedBitmap, width.toInt(), height.toInt(), false)
        } else if (lives == 0) {
            bitmap = Bitmap.createScaledBitmap(destroyedBitmap, width.toInt(), height.toInt(), false)
        }
        if (positionX <= 0) {
            positionX = 0 + width.toInt()/2
            speed = if (enraged) +20
            else +8
        } else if (positionX >= screenXLimit) {
            positionX = screenXLimit - width.toInt()/2
            speed = if (enraged) -20
            else -8
        } else positionX += speed
        hitbox.left = positionX.toFloat()-(width/2)
        hitbox.right = hitbox.left + width
    }

    override fun shoot() =  Random().nextDouble() < 0.5


}