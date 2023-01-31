package com.cc.tbd.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cc.tbd.databinding.ItemSaveNotesBinding
import com.cc.tbd.models.Note
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Ali Ahmad on 9,September,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
class SaveNotesAdapter(
    var mList: List<Note>,
    val context: Context,
) : RecyclerView.Adapter<SaveNotesAdapter.ViewHolder>() {
    var pos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSaveNotesBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    try {
        pos = position
        val model = mList[position]

        holder.mBinding.textView13.text = model.title
        holder.mBinding.time.text = getDateCurrentTimeZone(model.timestamp!!)

        val index = model.sub_notes?.indexOfFirst {
            it.imageUri != null
        }
        Glide.with(context).load(model.sub_notes?.get(index!!)!!.imageUri).into( holder.mBinding.image);
        holder.mBinding.caption.text = model.sub_notes?.get(index!!)!!.caption
    }catch (e:Exception){}


        holder.mBinding.imageView14.setOnClickListener {
            holder.mBinding.layoutOption.visibility =
                if (holder.mBinding.layoutOption.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

    }

    fun getDateCurrentTimeZone(timestamp: Timestamp): String {
        try {
//            val calendar: Calendar = Calendar.getInstance()
//            val tz: TimeZone = TimeZone.getDefault()
//            calendar.setTimeInMillis(timestamp * 1000)
//            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()))
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//            val currenTimeZone: Date = calendar.getTime() as Date

            val date: Date = timestamp.toDate()
//            val df =
//                SimpleDateFormat("hh:mm a", Locale.getDefault())
       //     return df.format(date)
            return sdf.format(date)
        } catch (e: Exception) {
        }
        return ""
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(val mBinding: ItemSaveNotesBinding) :
        RecyclerView.ViewHolder(mBinding.root)


}



