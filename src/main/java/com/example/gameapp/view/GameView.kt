package com.example.gameapp.view

import android.content.Context
import android.graphics.*
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.navigation.findNavController
import com.example.gameapp.R
import com.example.gameapp.model.Enemy
import com.example.gameapp.model.EnemyShoot
import com.example.gameapp.model.Player
import com.example.gameapp.model.Shoot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class GameView(context: Context, private val size: Point) : SurfaceView(context) {
    var canvas: Canvas = Canvas()
    private val paint: Paint = Paint()
    private val player = Player(context, size.x, size.y)
    private val enemyList = mutableListOf<Enemy>()
    private var enemiesHitted = mutableListOf<Enemy>()
    var playing = player.lives > 0
    var score = 0
    private val shoots = mutableListOf<Shoot>()
    private val certainShoots = mutableListOf<Shoot>()
    private val enemyShoots = mutableListOf<EnemyShoot>()
    val background = BitmapFactory.decodeResource(context.resources, R.drawable.road_background)

    var backgroundSound: MediaPlayer? = MediaPlayer.create(context, R.raw.background_game_music)
    var soundPool: SoundPool? = SoundPool(5, AudioManager.STREAM_MUSIC, 0)

    val playerShoot = soundPool?.load(context, R.raw.shooting_star, 0)
    val enemyShoot = soundPool?.load(context, R.raw.metal_scrap, 0)


    init {
        startGame()
        backgroundSound?.start()
        backgroundSound?.setOnCompletionListener {
            backgroundSound?.start()
        }
    }

    private fun startGame() {
        CoroutineScope(Dispatchers.Main).launch {
            while (playing) {
                draw()
                update()
                delay(10)
            }
            backgroundSound?.release()
            soundPool?.release()
            val toScore = GameFragmentDirections.actionGameFragmentToScoreFragment(score)
            findNavController().navigate(toScore)
        }
    }

    fun playSound(id: Int) {
        soundPool?.play(id, 0.99f, 0.99f, 0, 0, 1f)
    }

    private fun draw() {
        if (holder.surface.isValid) {
            canvas = holder.lockCanvas()
            canvas.drawBitmap(background, 0f, 0f, paint)
            //SCORE
            paint.color = Color.GREEN
            paint.textSize = 60f
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("Score: $score", (size.x - paint.descent()), 75f, paint)
            // LIVES
            paint.color = Color.WHITE
            paint.textSize = 60f
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText("${player.lives}❤", (paint.descent()), 75f, paint)
            //ENEMY
            if (enemyList.isNotEmpty()) {
                enemyList.forEach { enemy ->
                    canvas.drawBitmap(
                        enemy.bitmap, enemy.positionX.toFloat() - (enemy.width / 2),
                        enemy.randomStartPositionY.toFloat() - (enemy.height / 2), paint
                    )
                }
            }
            //PLAYER
            canvas.drawBitmap(
                player.bitmap,
                player.positionX.toFloat() - (player.width / 2),
                1500f,
                paint
            )
            // SHOOT
            if (shoots.isNotEmpty()) {
                shoots.forEach { shoot ->
                    canvas.drawBitmap(
                        shoot.bitmap, shoot.positionX,
                        shoot.positionY.toFloat(), paint
                    )
                }
            }
            if (enemyShoots.isNotEmpty()) {
                enemyShoots.forEach { shoot ->
                    canvas.drawBitmap(
                        shoot.bitmap, shoot.positionX,
                        shoot.positionY, paint
                    )
                }
            }
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun update() {
        // ENEMY
        playing = player.lives > 0
        generateEnemy()
        if (enemyList.isNotEmpty()) {
            enemyList.forEach { enemy ->
                // Cada enemigo intenta disparar
                if (enemy.shoot()) {
                    enemyShoot(enemy.positionX.toFloat(), enemy.randomStartPositionY.toFloat())
                }
                // Mira que por cada disparo toque cada enemigo
                // Si el disparo toca al enemigo, se añade ese disparo y ese enemigo a otras listas
                // para luego eliminarlos
                shoots.forEach { shoot ->
                    if (RectF.intersects(enemy.hitbox, shoot.hitbox)) {
                        enemiesHitted.add(enemy)
                        certainShoots.add(shoot)
                        score += 10
                    }
                }
                certainShoots.forEach { certainShoot ->
                    shoots.remove(certainShoot)
                }
            }
            enemiesHitted.forEach { enemyHitted ->
                enemyList.remove(enemyHitted)
            }
        }
        if (enemyList.isNotEmpty()) {
            enemyList.forEach { it.updateEnemy() }
        }


        // SHOOT
        if (shoots.isNotEmpty()) {
            val shootedShoots = mutableListOf<Shoot>()
            shoots.forEach { shoot ->
                shoot.updateShoot()
                if (shoot.positionY <= 0) {
                    shootedShoots.add(shoot)
                }
            }
            shootedShoots.forEach { shoot ->
                shoots.remove(shoot)
            }
        }
        // SHOOT ENEMY
        if (enemyShoots.isNotEmpty()) {
            val shootedShoots = mutableListOf<EnemyShoot>()
            val playerHitShoots = mutableListOf<EnemyShoot>()
            enemyShoots.forEach { shoot ->
                shoot.updateShoot()
                if (shoot.positionY >= size.y) {
                    shootedShoots.add(shoot)
                }

                if (RectF.intersects(shoot.hitbox, player.hitbox)) {
                    playerHitShoots.add(shoot)
                }
            }
            shootedShoots.forEach { shoot ->
                enemyShoots.remove(shoot)
            }
            playerHitShoots.forEach { shoot ->
                enemyShoots.remove(shoot)
                player.hitted()
            }
        }
        // PLAYER
        player.updatePlayer()

    }

    fun shoot() {
        if (shoots.size < 5) {
            shoots.add(
                Shoot(
                    context,
                    size.x, size.y, player.positionX.toFloat() - ((size.x / 15f) / 2)
                )
            )
            // playerShootSound?.start()
            if (playerShoot != null) {
                playSound(playerShoot)
            }
        }
    }

    fun enemyShoot(enemyPositionX: Float, initialPositionY: Float) {
        if (enemyShoots.size < 5) {
            enemyShoots.add(
                EnemyShoot(
                    context,
                    size.x, size.y, enemyPositionX - ((size.x / 15f) / 2), initialPositionY
                )
            )
            if (enemyShoot != null) {
                playSound(enemyShoot)
            }

        }
    }

    private fun generateEnemy() {
        val randomGenerator = Random().nextDouble()
        if (enemyList.size < 5 && randomGenerator < 0.3) {
            val enemy = Enemy(context, size.x, size.y)
            enemyList.add(enemy)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.action) {
                // Aquí capturem els events i el codi que volem executar per cadascún
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    // Modifiquem la velocitat del jugador perquè es mogui?
                    if (event.x > player.positionX && size.x != player.positionX) {
                        player.speed = 10
                    } else player.speed = -10
                }
                MotionEvent.ACTION_UP -> player.speed = 0
            }
        }
        return true
    }
}
