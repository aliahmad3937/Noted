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
import com.cc.tbd.ui.utils.ProgressUtil
import com.cc.tbd.ui.utils.ToastUtils
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)


           binding.textView4.setOnClickListener {
               findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
           }

//        binding.button.setOnClickListener {
//            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
//        }

        auth = FirebaseAuth.getInstance()


        binding.button.setOnClickListener{
            if (checkValidation()) {
                ProgressUtil.showProgress(requireContext())
                auth.signInWithEmailAndPassword(binding.editTextTextEmailAddress.text.toString(), binding.editTextTextPassword.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful){
                        ProgressUtil.hideProgress()
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }else{
                        ToastUtils.showToast(requireContext(), "Fail")
                        ProgressUtil.hideProgress()
                    }
                }
            }

        }


        return binding.root

    }

    fun checkValidation(): Boolean {
        var result: Boolean = true

        if (binding.editTextTextEmailAddress.text.toString().isEmpty()) {
            binding.editTextTextEmailAddress.error = "Email Required!"
            result = false
        }

        if (binding.editTextTextPassword.text.toString().isEmpty()) {
            binding.editTextTextPassword.error = "Password Required!"
            result = false
        }


        return result
    }

    override fun onResume() {
        super.onResume()
        if(FirebaseAuth.getInstance().currentUser != null){
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }

}