package com.example.gameapp.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import com.example.gameapp.R
import java.util.*

class Boss(context: Context, screenX: Int, screenY: Int): Enemy {
    override var lives = 20
    override var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.boss_icon)
    var enragedBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.boss_enraged_icon)
    override var destroyedBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.explosion_icon)

    override val width = screenX / 3f
    override val height = screenY / 5f
    val screenXLimit = screenX
    override var positionX = screenX / 2
    override var positionY = height.toInt()
    override var speed = 15
    override var hitbox = RectF()
    var enraged = false
    var shootTax = 0.5

    init {
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), false)
        hitbox.left = positionX.toFloat()
        hitbox.top = positionY.toFloat()
        hitbox.right = hitbox.left + width
        hitbox.bottom = hitbox.top + height
    }

    override fun updateEnemy() {
        if (lives == 10) {
            enraged = true
            bitmap = Bitmap.createScaledBitmap(enragedBitmap, width.toInt(), height.toInt(), false)
        } else if (lives == 0) {
            bitmap = Bitmap.createScaledBitmap(destroyedBitmap, width.toInt(), height.toInt(), false)
        }
        if (enraged) shootTax = 0.8
        if (positionX <= 0) {
            positionX = 0 + width.toInt()/2
            speed = if (enraged) +30
            else +15
        } else if (positionX >= screenXLimit) {
            positionX = screenXLimit - width.toInt()/2
            speed = if (enraged) -30
            else -15
        } else positionX += speed
        hitbox.left = positionX.toFloat()-(width/2)
        hitbox.right = hitbox.left + width
    }

    override fun shoot() =  Random().nextDouble() < shootTax


}