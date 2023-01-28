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
import com.cc.tbd.databinding.FragmentLoginBinding
import com.cc.tbd.databinding.FragmentSubscriptionPackagesBinding
import com.cc.tbd.ui.activities.MainActivity


class SubscriptionPackages : Fragment() {
    private lateinit var binding: FragmentSubscriptionPackagesBinding
    private lateinit var mContext: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater ,R.layout.fragment_subscription_packages, container, false)


        binding.imageView28.setOnClickListener {
          mContext.plan = "Startup"
            mContext.price = "$19.99"
            mContext.days = "28"
            findNavController().navigate(R.id.action_subscriptionPackages_to_subscriptionDetail)
        }


        binding.imageView29.setOnClickListener {
            mContext.plan = "Growth"
            mContext.price = "$119.98"
            mContext.days = "180"
            findNavController().navigate(R.id.action_subscriptionPackages_to_subscriptionDetail)
        }


        binding.imageView30.setOnClickListener {
            mContext.plan = "Business"
            mContext.price = "$329.99"
            mContext.days = "365"
            findNavController().navigate(R.id.action_subscriptionPackages_to_subscriptionDetail)

        }


        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }



}