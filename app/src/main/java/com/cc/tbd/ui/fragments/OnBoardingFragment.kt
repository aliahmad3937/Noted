package com.cc.tbd.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cc.tbd.R
import com.cc.tbd.databinding.FragmentLoginBinding
import com.cc.tbd.databinding.FragmentOnBoardingBinding


class OnBoardingFragment : Fragment() {
    private lateinit var binding: FragmentOnBoardingBinding
    var count = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_on_boarding, container, false)




        binding.next.setOnClickListener {
                count++
                updateScreen()

        }



        return binding.root
    }

    private fun updateScreen() {
        when(count){
            1 -> {
                binding.imageView25.setImageDrawable(requireContext().getDrawable(R.drawable.arrow_next))
                binding.imageView26.setImageDrawable(requireContext().getDrawable(R.drawable.dot))
                binding.imageView27.setImageDrawable(requireContext().getDrawable(R.drawable.dot))
                binding.textView2.text = getString(R.string.onBoarding_text_1)
                binding.imageView.setImageDrawable(requireContext().getDrawable(R.drawable.boarding_1))
            }
            2 -> {
                binding.imageView25.setImageDrawable(requireContext().getDrawable(R.drawable.dot))
                binding.imageView26.setImageDrawable(requireContext().getDrawable(R.drawable.arrow_next))
                binding.imageView27.setImageDrawable(requireContext().getDrawable(R.drawable.dot))
                binding.textView2.text = getString(R.string.onBoarding_text_2)
                binding.imageView.setImageDrawable(requireContext().getDrawable(R.drawable.boarding_2))
            }
            3 -> {
                binding.imageView25.setImageDrawable(requireContext().getDrawable(R.drawable.dot))
                binding.imageView26.setImageDrawable(requireContext().getDrawable(R.drawable.dot))
                binding.imageView27.setImageDrawable(requireContext().getDrawable(R.drawable.arrow_next))
                binding.textView2.text = getString(R.string.onBoarding_text_3)
                binding.imageView.setImageDrawable(requireContext().getDrawable(R.drawable.boarding_3))
                binding.next.text = "Go"

            }
            else -> {
                count = 1
                findNavController().navigate(R.id.action_onBoardingFragment_to_loginFragment)
            }
        }
    }


}