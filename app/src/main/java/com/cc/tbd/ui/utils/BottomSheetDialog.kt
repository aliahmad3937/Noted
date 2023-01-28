package com.cc.tbd.ui.utils


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import com.cc.tbd.R
import com.cc.tbd.databinding.BottomSheetLayoutBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import gun0912.tedimagepicker.builder.TedImagePicker


class BottomSheetDialog(val listener:(uri:Uri?) -> Unit) : BottomSheetDialogFragment() {
     var uri : Uri? = null
    private lateinit var binding: BottomSheetLayoutBinding

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
           dismiss()
            if (result.resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                uri = result.data?.data!!
                listener(uri)
            } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_layout, container, false)


        binding.imageView16.setOnClickListener {
//            ImagePicker.with(this)
//                .compress(1024)         //Final image size will be less than 1 MB(Optional)
//                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
//                .createIntent { intent ->
//                    startForProfileImageResult.launch(intent)
//                }

            NoteApplication.isTextScan = false

//            ImagePicker.with(this)
//                .galleryOnly()	//User can only select image from Gallery
//                .createIntent { intent ->
//                    startForProfileImageResult.launch(intent)
//                }

            listener(null)
            this.dismiss()



        }
        binding.imageView18.setOnClickListener {
//            ImagePicker.with(this)
//                .compress(1024)         //Final image size will be less than 1 MB(Optional)
//                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
//                .createIntent { intent ->
//                    startForProfileImageResult.launch(intent)
//                }

            NoteApplication.isTextScan = true

            ImagePicker.with(this)
                .galleryOnly()	//User can only select image from Gallery
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

//
//        binding.imageView17.setOnClickListener {
//
//            NoteApplication.isTextScan = false
//            ImagePicker.with(this)
//                .cameraOnly()	//User can only capture image using Camera
//                .createIntent { intent ->
//                    startForProfileImageResult.launch(intent)
//                }
//        }



        return binding.root
    }


}
