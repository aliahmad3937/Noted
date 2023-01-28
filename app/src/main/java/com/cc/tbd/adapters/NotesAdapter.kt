package com.cc.tbd.adapters


import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cc.tbd.R
import com.cc.tbd.databinding.*
import com.cc.tbd.models.Note
import com.cc.tbd.models.SubNote
import com.cc.tbd.ui.activities.MainActivity
import com.cc.tbd.ui.utils.FileUtil
import java.io.IOException


/**
 * Created by Ali Ahmad on 9,September,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
class NotesAdapter(
    var mList: List<SubNote>,
    val context: Context,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            isTextScan -> {
                return ViewHolder2(
                    ItemExtractedTextBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            isImage -> {
                return ViewHolder3(
                    ItemMultiImagesBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            isDocument -> {
                return ViewHolder4(
                    ItemPdfBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            isAudio -> {
                return ViewHolder5(
                    ItemAudioBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> {
                return ViewHolder1(
                    ItemNoteBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (mList[position].scanText != null) {
            return isTextScan
        } else if (mList[position].imageUri != null) {
            return isImage
        } else if (mList[position].fileUri != null) {
            return isDocument
        } else if (mList[position].audio != null) {
            return isAudio
        } else {
            return drawBitmap
        }
//        if (mList[it].fileUri == null) {
//            pos = position
//            holder.mBinding.etCaption.visibility =
//                if (holder.mBinding.etCaption.visibility == View.GONE) View.VISIBLE else View.GONE
//        } else {
//            //  val intent = Intent(Intent.ACTION_VIEW)
//        }

        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == isTextScan) {
            (holder as ViewHolder2).bind(position)
        } else if (holder.itemViewType == isImage) {
            (holder as ViewHolder3).bind(position)
        } else if (holder.itemViewType == isDocument) {
            (holder as ViewHolder4).bind(position)
        } else if (holder.itemViewType == isAudio) {
            (holder as ViewHolder5).bind(position)
        } else {
            (holder as ViewHolder1).bind(position)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder1(val mBinding: ItemNoteBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(position: Int) {
            mBinding.imageView22.setImageBitmap(mList[position].drawBitmap)
        }
    }

    inner class ViewHolder2(val mBinding: ItemExtractedTextBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(position: Int) {
            mBinding.etExtractedText.setText(mList[position].scanText!!.toString())
        }
    }

    inner class ViewHolder3(val mBinding: ItemMultiImagesBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        var pos = -1

        fun bind(position: Int) {
            pos = position
            val model = mList[position]
            mBinding.root.setOnClickListener {
               mBinding.etCaption.visibility =
                    if (mBinding.etCaption.visibility == View.GONE) View.VISIBLE else View.GONE

            }

           mBinding.etCaption.visibility = View.VISIBLE
           mBinding.imageView22.visibility = View.VISIBLE
           mBinding.imageView22.setImageURI(Uri.parse(model.imageUri))

            if (model.caption != null) {
                mBinding.etCaption.visibility = View.VISIBLE
                mBinding.etCaption.setText(model.caption)
            } else {
                mBinding.etCaption.visibility = View.GONE
            }

           mBinding.etCaption.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (s != null && s!!.isNotEmpty()) {
                        if (pos != -1) {
                            var note = model.copy(
                                caption = s.toString()
                            )
                            (context as MainActivity).mList.removeAt(pos)
                            (context as MainActivity).mList.add(pos, note)
                        }
                    }
                }
            })
        }
    }

    inner class ViewHolder4(val mBinding: ItemPdfBinding) : RecyclerView.ViewHolder(mBinding.root) {
        init {

        }

        fun bind(position: Int) {
            mBinding.title.text = FileUtil.getFileName(context, mList[position].fileUri)
            mBinding.layoutPdf.setOnClickListener {
                openFile(context, mList[position].fileUri)
            }

        }

        protected fun openFile(context: Context, localUri: Uri?) {
            val i = Intent(Intent.ACTION_VIEW)
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            i.setDataAndType(localUri, context.contentResolver.getType(localUri!!))
            context.startActivity(i)
        }
    }

    inner class ViewHolder5(val mBinding: ItemAudioBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        var mPlayer: MediaPlayer? = null
        var isPlaying = false

        init {

        }

        fun bind(position: Int) {
            mBinding.btnPlayPause.setOnClickListener {
                if(mPlayer == null){
                    playAudio(mList[position].audio!!)
                }
                isPlaying = !isPlaying
                if (isPlaying) {
                    mBinding.btnPlayPause.setImageDrawable(context.getDrawable(R.drawable.ic_pause))
                    mPlayer?.start()
                } else {
                    mBinding.btnPlayPause.setImageDrawable(context.getDrawable(R.drawable.ic_play))
                    mPlayer?.pause()
                }
            }

        }


        fun playAudio(audio: String) {
            // for playing our recorded audio
            // we are using media player class.
            mPlayer = MediaPlayer()
            mPlayer?.setOnCompletionListener {
                mPlayer?.release()
                mPlayer = null
                isPlaying = false
                mBinding.btnPlayPause.setImageDrawable(context.getDrawable(R.drawable.ic_play))
            }
            try {
                mPlayer?.let {
                    // below method is used to set the
                    // data source which will be our file name
                    it.setDataSource(context.getExternalFilesDir("/audio")!!.absolutePath + "/" + audio)

                    // below method will prepare our media player
                    it.prepare()

                    // below method will start our media player.
                }


            } catch (e: IOException) {
                Log.e("TAG", "prepare() failed")
            }
        }


    }

    companion object {
        const val drawBitmap = 1
        const val isTextScan = 2
        const val isImage = 3
        const val isDocument = 4
        const val isAudio = 5
    }


}


//holder.itemView.setOnClickListener {
//    if (model.fileUri == null) {
//        pos = position
//        holder.mBinding.etCaption.visibility =
//            if (holder.mBinding.etCaption.visibility == View.GONE) View.VISIBLE else View.GONE
//    } else {
//        //  val intent = Intent(Intent.ACTION_VIEW)
//    }
//}
//
//if (model.fileUri != null) {
//    holder.mBinding.etCaption.visibility = View.GONE
//    holder.mBinding.imageView22.visibility = View.GONE
//    holder.mBinding.layoutPdf.visibility = View.VISIBLE
//
//    holder.mBinding.title.text = FileUtil.getFileName(context, model.fileUri)
//
////            try {
////                val file: File = FileUtil.from(context, model.fileUri)
//////                Log.d("file",
//////                    "File...:::: uti - " + file.getPath()
//////                        .toString() + " file -" + file.toString() + " : " + file.exists()
//////                )
////            } catch (e: IOException) {
////                e.printStackTrace()
////            }
//}
//else {
//    holder.mBinding.etCaption.visibility = View.VISIBLE
//    holder.mBinding.imageView22.visibility = View.VISIBLE
//    holder.mBinding.layoutPdf.visibility = View.GONE
//
//
//    if (model.bitmap == null) {
//        holder.mBinding.imageView22.setImageURI(Uri.parse(model.uri))
//    } else {
//        holder.mBinding.imageView22.setImageBitmap(model.bitmap)
//    }
//
//    if (model.caption != null) {
//        holder.mBinding.etCaption.visibility = View.VISIBLE
//        holder.mBinding.etCaption.setText(model.caption)
//    } else {
//        holder.mBinding.etCaption.visibility = View.GONE
//    }
//
//    holder.mBinding.etCaption.addTextChangedListener(object : TextWatcher {
//        override fun beforeTextChanged(
//            s: CharSequence?,
//            start: Int,
//            count: Int,
//            after: Int
//        ) {
//
//        }
//
//        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//        }
//
//        override fun afterTextChanged(s: Editable?) {
//            if (s != null && s!!.isNotEmpty()) {
//                if (pos != -1) {
//                    var note = Note(
//                        bitmap = model.bitmap,
//                        uri = model.uri,
//                        caption = s.toString()
//                    )
//                    (context as MainActivity).mList.removeAt(pos)
//                    (context as MainActivity).mList.add(pos, note)
//                }
//            }
//        }
//    })
//
//}



