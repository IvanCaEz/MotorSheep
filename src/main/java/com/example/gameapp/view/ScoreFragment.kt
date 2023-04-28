package com.example.gameapp.view

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.gameapp.R
import com.example.gameapp.databinding.FragmentScoreBinding

class ScoreFragment : Fragment() {
    private lateinit var binding: FragmentScoreBinding
    lateinit var backgroundSound: MediaPlayer
    var musicIsPlaying = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScoreBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backgroundSound = MediaPlayer.create(context, R.raw.lose_screen_music)
        backgroundSound.start()
        backgroundSound.setOnCompletionListener {
            backgroundSound.start()
        }

        val wasBossDefeated = requireArguments().getBoolean("wasBossDefeated")

        if (wasBossDefeated) {
            binding.titleTv.text = getString(R.string.won)
            binding.butYouTv.text = getString(R.string.canreturn)
        }
        val score = arguments?.getInt("score").toString()

        binding.scoreTv.text = score

        binding.toMenuButton.setOnClickListener {
            backgroundSound.release()
            musicIsPlaying = false
            val toMenu = ScoreFragmentDirections.actionScoreFragmentToMenuFragment()
            findNavController().navigate(toMenu)

        }

        binding.shareIV.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Got $score vengeance points!")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

    }

    override fun onPause() {
        super.onPause()
        if (musicIsPlaying) {
            backgroundSound.pause()
        }

    }

    override fun onResume() {
        super.onResume()
        backgroundSound.start()
    }
}