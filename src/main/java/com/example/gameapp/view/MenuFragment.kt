package com.example.gameapp.view

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentMenuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playButton.setOnClickListener {
            val toGame = MenuFragmentDirections.actionMenuFragmentToGameFragment()
            findNavController().navigate(toGame)
        }

        binding.helpButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("How To Play")
                .setMessage("You are a sheep in a motorbike who is trying to get revenge from the robots of the farm" +
                        " where they lived all of their life.\n\nYour goal is to not die by the RoboFarmers who will" +
                        " try to destroy your motobike and capture you.\nTo help you, your motobike is equiped" +
                        " with a special cannon that launches shooting stars, so, don't hesitate and destroy those who" +
                        " wronged you!")
                .setPositiveButton("Got it!") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}