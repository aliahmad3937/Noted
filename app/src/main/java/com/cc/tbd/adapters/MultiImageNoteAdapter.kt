package com.cc.tbd.adapters

import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cc.tbd.databinding.ItemMultiImagesChildBinding
import com.cc.tbd.models.Note
import com.cc.tbd.ui.activities.MainActivity


/**
 * Created by Ali Ahmad on 9,September,2022
 * CodeCoy,
 * Lahore, Pakistan.
// */
//class MultiImageNoteAdapter(
//    var mList: List<Note>,
//    val context: Context,
//) : RecyclerView.Adapter<MultiImageNoteAdapter.ViewHolder>() {
//    var pos = -1
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(
//            ItemMultiImagesChildBinding.inflate(
//                LayoutInflater.from(parent.context), parent, false
//            )
//        )
//
//    }
//
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        pos = position
//        val model = mList[position]
//        holder.itemView.setOnClickListener {
//            holder.mBinding.etCaption.visibility =
//                if (holder.mBinding.etCaption.visibility == View.GONE) View.VISIBLE else View.GONE
//
//        }
//
//        holder.mBinding.etCaption.visibility = View.VISIBLE
//        holder.mBinding.imageView22.visibility = View.VISIBLE
//        holder.mBinding.imageView22.setImageURI(Uri.parse(model.uri))
//
//        if (model.caption != null) {
//            holder.mBinding.etCaption.visibility = View.VISIBLE
//            holder.mBinding.etCaption.setText(model.caption)
//        } else {
//            holder.mBinding.etCaption.visibility = View.GONE
//        }
//
//        holder.mBinding.etCaption.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(
//                s: CharSequence?,
//                start: Int,
//                count: Int,
//                after: Int
//            ) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                if (s != null && s!!.isNotEmpty()) {
//                    if (pos != -1) {
//                        var note = Note(
//                            bitmap = model.bitmap,
//                            uri = model.uri,
//                            caption = s.toString()
//                        )
//                        (context as MainActivity).mList.removeAt(pos)
//                        (context as MainActivity).mList.add(pos, note)
//                    }
//                }
//            }
//        })
//
//    }
//
//    override fun getItemCount(): Int {
//        return mList.size
//    }
//
//
//    class ViewHolder(val mBinding: ItemMultiImagesChildBinding) :
//        RecyclerView.ViewHolder(mBinding.root)
//
//
//}
//
//
//
