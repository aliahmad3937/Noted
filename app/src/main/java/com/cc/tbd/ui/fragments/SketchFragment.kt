package com.cc.tbd.ui.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cc.tbd.R
import com.cc.tbd.databinding.FragmentNotesBinding
import com.cc.tbd.databinding.FragmentSketchBinding
import com.cc.tbd.ui.activities.MainActivity
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch


class SketchFragment : Fragment() {
    private lateinit var binding: FragmentSketchBinding
    private lateinit var mContext:MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sketch, container, false)



        binding.eraser.setOnClickListener {
            binding.paintView.enableEraser()
        }


        binding.imageView19.setOnClickListener {
            binding.paintView.undoDrawing()
        }
        binding.imageView20.setOnClickListener {
            binding.paintView.redoDrawing()
        }

        binding.pencil1.setOnClickListener {
            binding.paintView.disableEraser()
            binding.paintView.brushSize = 20

        }
        binding.pencil2.setOnClickListener {
            binding.paintView.disableEraser()
            binding.paintView.brushSize = 15

        }
        binding.pencil3.setOnClickListener {
            binding.paintView.disableEraser()
            binding.paintView.brushSize = 10

        }


        binding.colorPiker.setOnClickListener {
            // Kotlin Code
            ColorPickerDialog
                .Builder(requireContext())        				// Pass Activity Instance
                .setTitle("Pick Theme")           	// Default "Choose Color"
                .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
                .setDefaultColor("#FF000000")     // Pass Default Color
                .setColorListener { color, colorHex ->
                    // Handle Color Selection

                    binding.paintView.brushColor = Color.parseColor(colorHex)
                }
                .show()

        }

        binding.textView28.setOnClickListener {
           mContext.bitmap = binding.paintView.canvasBitmap
            findNavController().navigate(R.id.action_sketchFragment_to_notesFragment)
        }


        binding.imageView21.setOnClickListener {
          revertOptionVisibility()
            }

        binding.changeBackground.setOnClickListener {
            revertOptionVisibility()
            // Kotlin Code
            ColorPickerDialog
                .Builder(requireContext())        				// Pass Activity Instance
                .setTitle("Pick Theme")           	// Default "Choose Color"
                .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
                .setDefaultColor("#FF000000")     // Pass Default Color
                .setColorListener { color, colorHex ->
                    // Handle Color Selection

                    binding.paintView.backgroundColor = Color.parseColor(colorHex)
                    binding.paintView.startTouch(0F,0F)


                }
                .show()
        }




        return binding.root
    }

    private fun revertOptionVisibility() {
        binding.layoutOption.visibility =  if(binding.layoutOption.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }


}