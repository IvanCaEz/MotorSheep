package com.example.gameapp.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import com.example.gameapp.R

class EnergyDrink(context: Context, screenX: Int, screenY: Int, ):Item {
    override var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.energy_icon)
    override val width = screenX / 11f
    override val height = screenY / 14f
    val randomStartPositionX = (width.toInt()..screenX-width.toInt()).random()
    override var positionY = height
    override var positionX = randomStartPositionX.toFloat()
    override var speed = -10
    override var hitbox = RectF()
    override val effect = 10

    init {
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), false)
        hitbox.left = positionX
        hitbox.top = positionY
        hitbox.right = hitbox.left + width
        hitbox.bottom = hitbox.top + height
    }



    override fun updateItem() {
        positionY -= speed

        hitbox.top = positionY
        hitbox.bottom = hitbox.top + height
    }

}