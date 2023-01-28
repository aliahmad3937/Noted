package com.cc.tbd.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cc.tbd.R
import com.cc.tbd.databinding.FragmentSubscriptionDetailBinding
import com.cc.tbd.databinding.FragmentSubscriptionPackagesBinding
import com.cc.tbd.ui.activities.MainActivity


class SubscriptionDetail : Fragment() {
    private lateinit var binding: FragmentSubscriptionDetailBinding
    private lateinit var mContext: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =DataBindingUtil.inflate(inflater,R.layout.fragment_subscription_detail, container, false)


        binding.textView29.text = mContext.plan
        binding.days.text = mContext.days
        binding.purchaseNow.text = mContext.price



        binding.purchaseNow.setOnClickListener {
            Toast.makeText(requireContext(),"Plan Purchase Successfully!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_subscriptionDetail_to_homeFragment)
        }




        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }

}