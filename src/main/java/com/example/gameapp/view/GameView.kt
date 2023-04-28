package com.example.gameapp.view

import android.content.Context
import android.graphics.*
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.navigation.findNavController
import com.example.gameapp.R
import com.example.gameapp.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class GameView(context: Context, private val size: Point) : SurfaceView(context) {
    var canvas: Canvas = Canvas()
    private val paint: Paint = Paint()
    private val player = Player(context, size.x, size.y)
    private var enemyLVL1List = mutableListOf<EnemyLVL1>()
    private var enemyLVL2List = mutableListOf<EnemyLVL2>()
    private val bossList = mutableListOf<Boss>()
    private var enemiesHitted = mutableListOf<Enemy>()
    private var enemiesToRemove = mutableListOf<Enemy>()
    private var enemiesToExplode = mutableListOf<Enemy>()
    private var bossToRemove = mutableListOf<Boss>()
    var bossPhase = false
    var bossDefeated = false
    private var itemList = mutableListOf<Item>()
    private var consumedItems = mutableListOf<Item>()

    var playing = player.lives > 0
    var score = 0


    private val shoots = mutableListOf<Shoot>()
    private val certainShoots = mutableListOf<Shoot>()
    private var enemyShoots = mutableListOf<EnemyShoot>()
    private val bossShoots = mutableListOf<BossShoot>()

    val background = BitmapFactory.decodeResource(context.resources, R.drawable.road_background_cut)
    val backgroundNight = BitmapFactory.decodeResource(context.resources, R.drawable.background_night)

    var backgroundSound: MediaPlayer? = MediaPlayer.create(context, R.raw.background_game_music)
    var backgroundSoundBoss: MediaPlayer? = MediaPlayer.create(context, R.raw.boss_music)

    var soundPool: SoundPool? = SoundPool(5, AudioManager.STREAM_MUSIC, 0)

    val playerShoot = soundPool?.load(context, R.raw.shooting_star, 0)
    val enemyShoot = soundPool?.load(context, R.raw.metal_scrap, 0)
    val bossShot = soundPool?.load(context, R.raw.scissors_sound, 0)


    init {
        startGame()
        backgroundSound?.start()
        backgroundSound?.setOnCompletionListener {
            backgroundSound?.start()
        }
    }

    private fun startGame() {
        CoroutineScope(Dispatchers.Main).launch {
            while (playing && !bossDefeated) {
                draw()
                update()
                delay(10)
            }
            backgroundSoundBoss?.release()
            backgroundSound?.release()
            soundPool?.release()
            val toScore = GameFragmentDirections.actionGameFragmentToScoreFragment(score, bossDefeated)
            findNavController().navigate(toScore)
        }
    }

    fun playSound(id: Int) {
        soundPool?.play(id, 0.99f, 0.99f, 0, 0, 1f)
    }

    private fun draw() {
        if (holder.surface.isValid) {
            canvas = holder.lockCanvas()
            if (score <= 750){
                canvas.drawBitmap(background, 0f, 0f, paint)
            } else canvas.drawBitmap(backgroundNight, 0f, 0f, paint)

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
            canvas.drawText("${player.armor}\uD83D\uDEE1", (paint.descent()), 135f, paint)
            if (player.energized) {
                canvas.drawText("⚡", (paint.descent()), 185f, paint)
            }

            // ITEMS
            if (itemList.isNotEmpty()){
                itemList.forEach { item ->
                    canvas.drawBitmap(item.bitmap,
                        item.positionX,
                        item.positionY, paint)
                }
            }

            //ENEMY
            if (enemyLVL1List.isNotEmpty()) {
                enemyLVL1List.forEach { enemy ->
                    if (enemy.lives != 0){
                        canvas.drawBitmap(
                            enemy.bitmap, enemy.positionX.toFloat() - (enemy.width / 2),
                            enemy.positionY.toFloat() - (enemy.height / 2), paint
                        )
                    }
                }
            }

            if (bossList.isNotEmpty()){
                bossList.forEach { boss ->
                    canvas.drawBitmap(
                        boss.bitmap, boss.positionX.toFloat() - (boss.width / 2),
                        boss.positionY.toFloat() - (boss.height / 2), paint
                    )
                }
            }

            if (enemiesToExplode.isNotEmpty()){
                enemiesToExplode.forEach { enemyExplosion ->
                    canvas.drawBitmap(
                        enemyExplosion.destroyedBitmap,
                        enemyExplosion.positionX.toFloat() - (enemyExplosion.width / 2),
                        enemyExplosion.positionY.toFloat() - (enemyExplosion.height / 2), paint
                    )
                }
            }

            if (enemyLVL2List.isNotEmpty()) {
                enemyLVL2List.forEach { enemy ->
                    canvas.drawBitmap(
                        enemy.bitmap, enemy.positionX.toFloat() - (enemy.width / 2),
                        enemy.positionY.toFloat() - (enemy.height / 2), paint
                    )
                }
            }
            //PLAYER
            canvas.drawBitmap(player.bitmap, player.positionX.toFloat() - (player.width / 2),
                1500f, paint)
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

            if (bossShoots.isNotEmpty()) {
                bossShoots.forEach { bossShoot ->
                    canvas.drawBitmap(
                        bossShoot.bitmap, bossShoot.positionX,
                        bossShoot.positionY, paint
                    )
                }
            }
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun update() {
        playing = player.lives > 0
        enemiesToExplode = mutableListOf<Enemy>()
        // GENERATE
        if (!bossPhase){
            generateEnemy()
        }
        generateItem()
        // ITEMS
        updateItems()
        // ENEMIES
        // Cada enemigo intenta disparar
        // Mira que por cada disparo toque cada enemigo
        // Si el disparo toca al enemigo, se añade ese disparo y ese enemigo a otras listas
        // para luego eliminarlos
        enemiesLVL1()
        enemiesLVL2()
        enemyBoss()
        // SHOOTS
        updatePlayerShoots()
        updateEnemyShoots()
        updateBossShoots()
        // PLAYER
        player.updatePlayer()

        if (bossPhase){
            backgroundSound?.release()
            backgroundSoundBoss?.start()
        }
    }

    fun shoot() {
        if (shoots.size < 5) {
            shoots.add(Shoot(context, size.x, size.y,
                    player.positionX.toFloat() - ((size.x / 15f) / 2))
            )
            if (playerShoot != null) {
                playSound(playerShoot)
            }
        }
    }

    private fun bossShoot(enemyPositionX: Float, initialPositionY: Float){
        if (bossShoots.size < 3) {
            bossShoots.add(
                BossShoot(
                    context,
                    size.x, size.y, enemyPositionX - ((size.x / 15f) / 2), initialPositionY
                )
            )
            if (bossShot != null) {
                playSound(bossShot)
            }

        }
    }

    private fun enemyShoot(enemyPositionX: Float, initialPositionY: Float) {
        if (enemyShoots.size < 6) {
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

    private fun updateItems(){
        if (itemList.isNotEmpty()){
            itemList.forEach { item ->
                if (RectF.intersects(item.hitbox, player.hitbox)){
                    consumedItems.add(item)
                    player.useItem(item)
                }
                if (item.positionY >= size.y) {
                    consumedItems.add(item)
                }
            }
            consumedItems.forEach { itemConsumed ->
                itemList.remove(itemConsumed)
            }
        }
        if (itemList.isNotEmpty()) itemList.forEach { it.updateItem() }
    }

    private fun updatePlayerShoots(){
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
    }

    private fun updateBossShoots(){
        if (bossShoots.isNotEmpty()) {
            val shootedShoots = mutableListOf<BossShoot>()
            val playerHitShoots = mutableListOf<BossShoot>()
            bossShoots.forEach { shoot ->
                shoot.updateShoot()
                if (shoot.positionY >= size.y) {
                    shootedShoots.add(shoot)
                }

                if (RectF.intersects(shoot.hitbox, player.hitbox)) {
                    playerHitShoots.add(shoot)
                }
            }
            shootedShoots.forEach { shoot ->
                bossShoots.remove(shoot)
            }
            playerHitShoots.forEach { shoot ->
                bossShoots.remove(shoot)
                player.hitted()
            }
        }
    }
    private fun updateEnemyShoots(){
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
    }

    private fun enemiesLVL1(){
        if (enemyLVL1List.isNotEmpty()) {
            enemyLVL1List.forEach { enemy ->

                if (enemy.shoot()) {
                    enemyShoot(enemy.positionX.toFloat(), enemy.positionY.toFloat())
                }

                shoots.forEach { shoot ->
                    if (RectF.intersects(enemy.hitbox, shoot.hitbox)) {
                        enemy.lives--
                        enemiesHitted.add(enemy)
                        enemiesToRemove.add(enemy)
                        certainShoots.add(shoot)
                        enemiesToExplode.add(enemy)
                        score += 10
                    }
                }
                certainShoots.forEach { certainShoot ->
                    shoots.remove(certainShoot)
                }
            }
            enemiesToRemove.forEach { enemyHitted ->
                enemyLVL1List.remove(enemyHitted)
            }
        }

        if (enemyLVL1List.isNotEmpty()) {
            enemyLVL1List.forEach { it.updateEnemy() }
        }
    }

    private fun enemiesLVL2(){
        if (enemyLVL2List.isNotEmpty()) {
            enemyLVL2List.forEach { enemy ->
                if (enemy.shoot()) {
                    enemyShoot(enemy.positionX.toFloat(), enemy.positionY.toFloat())
                }
                shoots.forEach { shoot ->
                    if (RectF.intersects(enemy.hitbox, shoot.hitbox)) {
                        enemy.lives--
                        enemy.speed * -1
                        if (enemy !in enemiesHitted){
                            enemiesHitted.add(enemy)
                        }
                        if (enemy.lives <= 0){
                            enemiesToRemove.add(enemy)
                            enemiesToExplode.add(enemy)
                            score += 25
                        }
                        certainShoots.add(shoot)
                    }
                }
                certainShoots.forEach { certainShoot ->
                    shoots.remove(certainShoot)
                }
            }
            enemiesToRemove.forEach { enemyHitted ->
                enemyLVL2List.remove(enemyHitted)
            }
        }
        if (enemyLVL2List.isNotEmpty()) {
            enemyLVL2List.forEach { it.updateEnemy() }
        }


    }

    private fun enemyBoss(){
        if (bossList.isNotEmpty()) {
            // Cuando spawnea, desaparecen los demás
            enemyLVL1List = mutableListOf()
            enemyLVL2List = mutableListOf()
            enemyShoots = mutableListOf()

            bossList.forEach { boss ->
                if (boss.shoot()) {
                   bossShoot(boss.positionX.toFloat(), boss.positionY.toFloat())
                }
                shoots.forEach { shoot ->
                    if (RectF.intersects(boss.hitbox, shoot.hitbox)) {
                        boss.lives--
                        if (boss.lives <= 0){
                            bossToRemove.add(boss)
                            enemiesToExplode.add(boss)
                            score += 500
                        }
                        certainShoots.add(shoot)

                    }
                }
                certainShoots.forEach { certainShoot ->
                    shoots.remove(certainShoot)
                }
            }
            bossToRemove.forEach { boss ->
                bossList.remove(boss)
                bossDefeated = true
            }
        }
        if (bossList.isNotEmpty()) {
            bossList.forEach { it.updateEnemy() }
        }


    }

    private fun generateLVL1Enemies(){
        val randomGeneratorLvL1 = Random().nextDouble()
        if (enemyLVL1List.size < 4 && randomGeneratorLvL1 < 0.4) {
            val enemyLVL1 = EnemyLVL1(context, size.x, size.y)
            enemyLVL1List.add(enemyLVL1)
        }
    }
    private fun generateLVL2Enemies(){
        val randomGeneratorLvL2 = Random().nextDouble()
        if (enemyLVL2List.size < 3 && randomGeneratorLvL2 < 0.4) {
            val enemyLVL2 = EnemyLVL2(context, size.x, size.y)
            enemyLVL2List.add(enemyLVL2)
        }
    }

    private fun generateEnemy() {

        when (score){
            in 0..499 -> {
                generateLVL1Enemies()
            }
            in 500 .. 1499 -> {
                generateLVL1Enemies()
                generateLVL2Enemies()
            }
        }
       if (score >= 1500 && bossList.isEmpty() && !bossDefeated){
            bossList.add(Boss(context, size.x, size.y))

           bossPhase = true

       }

    }


    private fun generateItem() {
        val randomItemGenerator = Random().nextDouble()

        when {
            randomItemGenerator < 0.05 ->{
                if (itemList.filterIsInstance<Colinabo>().isEmpty() && !player.feed){
                    itemList.add(Colinabo(context, size.x, size.y))
                }
            }
            randomItemGenerator < 0.15 ->{
                if (itemList.filterIsInstance<Helmet>().isEmpty() && player.armor == 0){
                    itemList.add(Helmet(context, size.x, size.y))
                }
            }
            randomItemGenerator < 0.25 -> {
                if (itemList.filterIsInstance<EnergyDrink>().isEmpty() && !player.energized){
                    itemList.add(EnergyDrink(context, size.x, size.y))
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.action) {
                // Aquí capturem els events i el codi que volem executar per cadascún
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    // Modifiquem la velocitat del jugador perquè es mogui?
                    if (event.x > player.positionX && size.x != player.positionX) {
                        if (player.energized){
                            player.speed = 20
                        } else player.speed = 10
                    } else{
                        if (player.energized){
                            player.speed = -20
                        } else player.speed = -10
                    }
                }
                MotionEvent.ACTION_UP -> player.speed = 0
            }
        }
        return true
    }
}
