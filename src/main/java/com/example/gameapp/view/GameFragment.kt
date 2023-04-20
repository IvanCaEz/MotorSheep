package com.example.gameapp.view

import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController


class GameFragment : Fragment() {
    lateinit var fireButton: Button
    lateinit var gameView: GameView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        gameView = GameView(requireContext(), size)
        val game = FrameLayout(requireContext())
        val gameButtons = RelativeLayout(requireContext())
        fireButton= Button(requireContext());
        fireButton.setText("SHOOT!")
        fireButton.setTextColor(Color.WHITE)
        fireButton.setBackgroundColor(Color.HSVToColor(floatArrayOf(1f,0.67f,0.94f)))
        val b1 = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.FILL_PARENT,
            RelativeLayout.LayoutParams.FILL_PARENT
        )

        gameButtons.setLayoutParams(params)
        gameButtons.addView(fireButton)
        b1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        b1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        fireButton.setLayoutParams(b1)
        game.addView(gameView)
        game.addView(gameButtons)

        return game
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fireButton.setOnClickListener {
            gameView.shoot()
        }

    }

    override fun onPause() {
        super.onPause()
        if (gameView.playing){
            gameView.backgroundSound?.pause()
            gameView.soundPool?.release()
        }

    }

    override fun onResume() {
        super.onResume()
        gameView.backgroundSound?.start()
    }




}