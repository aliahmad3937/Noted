package com.cc.tbd.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cc.tbd.R
import com.cc.tbd.databinding.FragmentRegisterBinding
import com.cc.tbd.models.User
import com.cc.tbd.ui.utils.ProgressUtil
import com.cc.tbd.ui.utils.ToastUtils
import com.cc.tbd.ui.utils.ToastUtils.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private var referalCode = ""

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

        binding.editTextReferalCode.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                Log.v("TAG","referal :${p0.toString()}")
                if(p0 != null && p0!!.contains(":")){
                    referalCode = p0?.split(":")!!.last().trim()
                    binding.editTextReferalCode.setText(referalCode)
                }else{

                }

                Log.v("TAG","referal :${binding.editTextReferalCode.text.toString()}")
            }

        })


        binding.button.setOnClickListener {
            if (binding.editTextReferalCode.text.toString().isNotEmpty()) {
                mFirestore.collection("user").document(binding.editTextReferalCode.text.toString())
                    .get().addOnSuccessListener { document ->
                        var user = document.toObject(User::class.java)
                        if (document.exists()) {
                            login(user)
                        } else {
                            ToastUtil.showToast("Referal Code not match!")
                        }

                    }
                    .addOnFailureListener {
                        ToastUtil.showToast("Referal Code not match!")
                    }
            } else {
                login(null)
            }

        }


        return binding.root
    }

    private fun login(user: User?) {
        if (checkValidation()) {
            ProgressUtil.showProgress(requireContext(), msg = getString(R.string.registered))
            //   showToast(requireContext(),"Image Uploaded Successfully!")
            auth.createUserWithEmailAndPassword(
                binding.editTextTextEmailAddress.text.toString(),
                binding.editTextTextPassword.text.toString()
            ).addOnSuccessListener { task ->
                lifecycleScope.launch {
                    launch {
                        user?.let {
                            if (it.referral != null) {
                                it.referral!!.add(task.user!!.uid)
                                updateReferal(it)
                            } else {
                                    it.referral = arrayListOf(task.user!!.uid)
                                    updateReferal(it)
                            }
                        }
                    }
                    launch {
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

                            .addOnFailureListener {
                                showToast(
                                    requireContext(),
                                    "${it.localizedMessage}"
                                )
                                ProgressUtil.hideProgress()

                            }
                    }
                }
            }


        }

    }

    private fun updateReferal(user: User) {
        mFirestore.collection("user").document(user.id)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(
                    "TAG",
                    "Referal updated!"
                )
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error writing document", e)
                //   ProgressUtil.hideProgress()
            }
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