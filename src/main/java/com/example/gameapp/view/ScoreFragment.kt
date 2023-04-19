package com.example.gameapp.view

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentScoreBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scoreTv.text = arguments?.getInt("score").toString()

        binding.toMenuButton.setOnClickListener {
            val toMenu = ScoreFragmentDirections.actionScoreFragmentToMenuFragment()
            findNavController().navigate(toMenu)
        }
    }
}