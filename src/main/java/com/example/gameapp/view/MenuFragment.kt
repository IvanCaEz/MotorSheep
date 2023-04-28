package com.example.gameapp.view

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.gameapp.R
import com.example.gameapp.databinding.FragmentMenuBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MenuFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    lateinit var backgroundSound: MediaPlayer
    var musicIsPlaying = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentMenuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backgroundSound =  MediaPlayer.create(context, R.raw.menu_background_music)
        backgroundSound.start()
        backgroundSound.setOnCompletionListener {
            backgroundSound.start()
        }

        binding.playButton.setOnClickListener {
            val toGame = MenuFragmentDirections.actionMenuFragmentToGameFragment()
            findNavController().navigate(toGame)
        }

        binding.howToPlayButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("How To Play")
                .setMessage("You are a sheep in a motorbike (a MotorSheep) who is trying to get revenge from the robots of the farm" +
                        " where they lived all of their life.\n\nYour goal is to not die by the RoboFarmers who will" +
                        " try to destroy your motobike and capture you.\n\nTo help you, your motobike is equiped" +
                        " with a special cannon that launches shooting stars, so, don't hesitate and destroy those who" +
                        " wronged you!\n\n\n" +
                        "But beware of the infamous wool collector...")
                .setPositiveButton("Got it!") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
        binding.itemsButton.setOnClickListener {

            val dialog = ItemDialog()

            dialog.show(requireActivity().supportFragmentManager, "Items")
        }
    }
    override fun onPause() {
        super.onPause()
        if(musicIsPlaying){
            backgroundSound.pause()
        }

    }
    override fun onResume() {
        super.onResume()
        backgroundSound.start()
    }
}