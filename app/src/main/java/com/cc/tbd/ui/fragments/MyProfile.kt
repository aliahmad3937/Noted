package com.cc.tbd.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cc.tbd.R
import com.cc.tbd.databinding.FragmentHomeBinding
import com.cc.tbd.databinding.FragmentMyProfileBinding
import com.cc.tbd.models.Note
import com.cc.tbd.ui.utils.BottomSheetDialog


class MyProfile : Fragment() {
    private lateinit var binding: FragmentMyProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile, container, false)


        binding.circleImageView.setOnClickListener {
            val bottomSheet = BottomSheetDialog() {
                it?.let {
                  binding.circleImageView.setImageURI(it)
                }
            }

            bottomSheet.show(
                requireActivity().supportFragmentManager,
                "ModalBottomSheet")
        }


        return binding.root
    }


}