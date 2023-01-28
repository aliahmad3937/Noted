package com.cc.tbd.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cc.tbd.R
import com.cc.tbd.databinding.FragmentLoginBinding
import com.cc.tbd.databinding.FragmentRegisterBinding
import com.cc.tbd.models.User
import com.cc.tbd.ui.utils.ProgressUtil
import com.cc.tbd.ui.utils.ToastUtils
import com.cc.tbd.ui.utils.ToastUtils.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        auth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()


        binding.textView4.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.button.setOnClickListener {
            if (checkValidation()) {
                ProgressUtil.showProgress(requireContext(), msg = getString(R.string.registered))
                //   showToast(requireContext(),"Image Uploaded Successfully!")
                auth.createUserWithEmailAndPassword(
                    binding.editTextTextEmailAddress.text.toString(),
                    binding.editTextTextPassword.text.toString()
                ).addOnSuccessListener { task ->
                                       val user = User(
                                           id = task.user!!.uid,
                                           uname = binding.editTextUserName.text.toString(),
                                           email = binding.editTextTextEmailAddress.text.toString(),
                                           password = binding.editTextTextPassword.text.toString(),
                                       )
                                    mFirestore.collection("user").document(user.id)
                                        .set(user)
                                        .addOnSuccessListener {
                                            ToastUtils.showToast(
                                                requireContext(),
                                                "Registered successfully!"
                                            )
                                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                                            Log.d(
                                                "TAG",
                                                "DocumentSnapshot successfully written!"
                                            )
                                            ProgressUtil.hideProgress()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w("TAG", "Error writing document", e)
                                            ProgressUtil.hideProgress()
                                        }
                                }
                    .addOnFailureListener{
                        showToast(
                            requireContext(),
                            "${it.localizedMessage}"
                        )
                        ProgressUtil.hideProgress()

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
        if (binding.editTextUserName.text.toString().isEmpty()) {
            binding.editTextUserName.error = "Username Required!"
            result = false
        }


        return result
    }

}