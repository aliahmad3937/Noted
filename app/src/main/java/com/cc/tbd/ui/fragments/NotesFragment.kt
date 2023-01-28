package com.cc.tbd.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cc.tbd.R
import com.cc.tbd.adapters.NotesAdapter
import com.cc.tbd.databinding.FragmentNotesBinding
import com.cc.tbd.models.Note
import com.cc.tbd.models.SaveNotes
import com.cc.tbd.models.SubNote
import com.cc.tbd.ui.activities.MainActivity
import com.cc.tbd.ui.utils.ProgressUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pspdfkit.document.processor.*
import com.pspdfkit.utils.Size
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class NotesFragment : Fragment() {
    private lateinit var binding: FragmentNotesBinding

    //    private val args:NotesFragmentArgs  by navArgs()
    private var uri1: Uri? = null
    private var isOpen = false
    private var mAdapter: NotesAdapter? = null
    private lateinit var mContext: MainActivity

    var mediaRecorder: MediaRecorder? = null

    var recordFile = ""

    private fun imageToPDF(image: Bitmap) {
        val recordPath: String = requireContext().getExternalFilesDir("/pdf")!!.absolutePath
        val outputFile: File = File(recordPath, "${System.currentTimeMillis()}.pdf")
        val imageSize = Size(image.width.toFloat(), image.height.toFloat())
        val pageImage = PageImage(image, PagePosition.CENTER)
        pageImage.setJpegQuality(70)
        val newPage = NewPage
            .emptyPage(imageSize)
            .withPageItem(pageImage)
            .build()

        Log.v("imageToPDF", "output file :${outputFile.absolutePath}")
        val task = PdfProcessorTask.newPage(newPage)
        CoroutineScope(Dispatchers.Main).launch {
            val disposable = withContext(Dispatchers.IO) {
                PdfProcessor.processDocumentAsync(task, outputFile)
                    .subscribe { progress ->
                        Log.v("imageToPDF", "progress :$progress")
                    }
            }

            sharePdf(outputFile)
        }

    }

    fun sharePdf(path: File) {
        val photoURI: Uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().applicationContext.packageName + ".provider",
            path
        )
        val shareToneIntent = Intent(Intent.ACTION_SEND)
        shareToneIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareToneIntent.putExtra(Intent.EXTRA_STREAM, photoURI)
        shareToneIntent.type = "application/pdf"
        requireActivity().startActivity(shareToneIntent)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes, container, false)
        setUpRecyclerView()

        binding.imageView21.setOnClickListener {
            binding.layoutOption.visibility =
                if (binding.layoutOption.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        binding.tvGeneratePdf.setOnClickListener {
            if (mContext.mList.isNotEmpty()) {
                val bitmap = getBitmapFromView(binding.layoutNote)
                imageToPDF(bitmap)
            }
        }

//        args.uri?.let {
//                mContext.mList.add(Note(
//                    uri = args.uri
//                ))
//                mAdapter?.notifyDataSetChanged()
//        }
        if (mContext.bitmap != null) {
            mContext.mList.add(
                SubNote(
                    drawBitmap = mContext.bitmap
                )
            )
            mContext.bitmap = null
            mAdapter?.notifyDataSetChanged()
        }


        binding.imageView12.setOnClickListener {
            Dexter.withContext(requireContext())
                .withPermissions(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        startRecording()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        list: List<PermissionRequest?>?,
                        permissionToken: PermissionToken?
                    ) {
                    }
                }).check()
        }


        binding.start.setOnClickListener {

        }
        binding.close.setOnClickListener {
            stopRecording()
            mContext.mList.add(
                SubNote(
                    audio = recordFile
                )
            )
            mAdapter?.notifyDataSetChanged()
            binding.layoutRecording.visibility = View.GONE

        }

        if (mContext.isRecording) {
            mContext.isRecording = false
            binding.layoutRecording.visibility = View.VISIBLE
            startRecording()
        } else {
            binding.layoutRecording.visibility = View.GONE
        }


        binding.addNotes.setOnClickListener {
            if (binding.layoutFloating.visibility == View.VISIBLE) {
                binding.addNotes.setImageDrawable(requireContext().getDrawable(R.drawable.ic_add_24_white))
                binding.layoutFloating.visibility = View.GONE
            } else {
                binding.addNotes.setImageDrawable(requireContext().getDrawable(R.drawable.ic_close_24_white))
                binding.layoutFloating.visibility = View.VISIBLE
            }
        }

//        binding.imageView9.setOnClickListener {
//            val bottomSheet = BottomSheetDialog() {
//                it?.let {
//                    mContext.mList.add(
//                        SubNote(
//                            uri = it.toString()
//                        )
//                    )
//                    mAdapter?.notifyDataSetChanged()
//                }
//            }
//            bottomSheet.show(
//                requireActivity().supportFragmentManager,
//                "ModalBottomSheet"
//            )
//
//        }

        binding.imageView10.setOnClickListener {
            findNavController().navigate(R.id.action_notesFragment_to_sketchFragment)
        }

        binding.textView28.setOnClickListener {
            if (binding.etTitle.text.toString().isNotEmpty()) {
                if (binding.note1.text.toString().isEmpty()) {
                    binding.note1.visibility = View.GONE
                } else {
                    binding.note1.visibility = View.VISIBLE
                }
                if (mContext.mList.isEmpty()) {
                    ToastUtil.showToast("No Note to save")
                } else {
                    saveNote(binding.etTitle.text.toString(), binding.note1.text.toString())
//                    findNavController().navigate(R.id.action_notesFragment_to_homeFragment)
                }
            } else {
                binding.etTitle.error = "Enter Title!"
            }
        }


        return binding.root
    }

    private fun saveNote(title: String, subTitle: String) {
        ProgressUtil.showProgress(requireContext())
        val ref = FirebaseFirestore.getInstance()
            .collection("Notes")
            .document(FirebaseAuth.getInstance().uid.toString())
            .collection("Notes")
            .document()
        val date = Date()
        val timestamp = Timestamp(date)
        val note = Note()
        var mList: ArrayList<SaveNotes> = arrayListOf()
        val size = mContext.mList.size
        var count = 0

        lifecycleScope.launch {
            uploadFireStore().collect {
                mList.add(it)
                count++
                if (count == size) {
                    note.apply {
                        id = ref.id
                        this.title = title
                        this.sub_title = subTitle
                        this.sub_notes = mList
                        this.timestamp = timestamp
                    }

                    ref.set(note, SetOptions.merge())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                ProgressUtil.hideProgress()
                                ToastUtil.showToast("Note Saved Successfully!")
                                findNavController().navigate(R.id.action_notesFragment_to_homeFragment)
                            }
                        }
                        .addOnFailureListener { e ->
                            ToastUtil.showToast("Error :${e.localizedMessage}")
                        }
                }
            }

        }


    }

    private suspend fun uploadFireStore() = callbackFlow<SaveNotes> {
        val imageRef = FirebaseStorage.getInstance().reference.child("Notes/image")
        val pdfRef = FirebaseStorage.getInstance().reference.child("Notes/pdf")
        mContext.mList.forEach {
            val time = System.currentTimeMillis()
            if (it.imageUri != null) {
                // upload image

                imageRef.child(time.toString()).putFile(Uri.parse(it.imageUri!!))
                    .addOnSuccessListener { snapshot ->
                        // Get the download URL
                        imageRef.child(time.toString()).downloadUrl.addOnSuccessListener { uri ->
                            launch {
                                send(
                                    SaveNotes(
                                        id = time.toString(),
                                        imageUri = uri.toString(),
                                        caption = it.caption
                                    )
                                )
                            }
                        }
                    }
            } else if (it.drawBitmap != null) {
                // upload paint image
                val baos = ByteArrayOutputStream()
                it.drawBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                imageRef.child(time.toString()).putBytes(data).addOnSuccessListener { snapshot ->
                    // Get the download URL
                    imageRef.child(time.toString()).downloadUrl.addOnSuccessListener { uri ->
                        launch {

                            send(
                                SaveNotes(
                                    id = time.toString(),
                                    imageUri = uri.toString(),
                                    caption = it.caption
                                )
                            )
                        }
                    }
                }

            } else if (it.fileUri != null) {
                // upload pdf

                pdfRef.child(time.toString()).putFile(Uri.parse(it.imageUri!!))
                    .addOnSuccessListener { snapshot ->
                        // Get the download URL
                        pdfRef.child(time.toString()).downloadUrl.addOnSuccessListener {
                            launch {
                                send(
                                    SaveNotes(
                                        id = time.toString(),
                                        fileUri = it.toString()
                                    )
                                )
                            }
                        }
                    }
            } else if (it.audio != null) {
                launch {
                    send(
                        SaveNotes(
                            id = time.toString(),
                            audio = it.audio
                        )
                    )
                }
            } else {
                launch {
                    send(
                        SaveNotes(
                            id = time.toString(),
                            scanText = it.scanText
                        )
                    )
                }
            }

        }
        awaitClose { mContext.mList.clear() }
    }.flowOn(Dispatchers.IO)

    private fun startRecording() {
        Log.i(
            "recording",
            "Recording: " + "externalCacheDir ::${requireContext().externalCacheDir}"
        )
        Log.i("recording", "Recording: " + "cacheDir ::${requireContext().cacheDir}")
        Toast.makeText(requireContext(), "Recording Started...", Toast.LENGTH_SHORT).show()
        binding.layoutRecording.visibility = View.VISIBLE
        binding.cmTime.setBase(SystemClock.elapsedRealtime())
        binding.cmTime.start()
        val recordPath: String = requireContext().getExternalFilesDir("/audio")!!.absolutePath
        val dateFormatter = SimpleDateFormat("dd_MM_yyyy_hh_mm_ss")
        val newFile = Date()
        recordFile = ""
        recordFile = "${System.currentTimeMillis()}.3gp"


        if (mediaRecorder == null) {
            mediaRecorder = MediaRecorder()
            mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder!!.setOutputFile("$recordPath/$recordFile")
            mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        }
        try {
            mediaRecorder!!.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mediaRecorder!!.start()
        Log.i("recording", "Recording: " + "Recording Started ::${recordPath}/${recordFile}")
    }

    private fun stopRecording() {
        binding.layoutRecording.visibility = View.GONE
        if (mediaRecorder != null) {
            binding.cmTime.stop()
            Toast.makeText(requireContext(), "Recording Finish!", Toast.LENGTH_SHORT).show()

            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            // Log.i(MotionEffect.TAG, "Recording: " + "Stopped Recording")
        }

    }

    private fun setUpRecyclerView() {
        mAdapter = NotesAdapter(mContext.mList, requireActivity())
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recycler.layoutManager = linearLayoutManager
        binding.recycler.adapter = mAdapter
    }


    private fun getBitmapFromView(view: View): Bitmap {

        view.buildDrawingCache()

        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bg = view.background
        if (bg != null) {
            bg.draw(canvas)
        } else {
            canvas.drawColor(Color.parseColor("#FFEBE0"))
        }

        view.draw(canvas)


        val drawable: Drawable = BitmapDrawable(resources, bitmap)

        //   Log.i(TAG, "signUp --> BitmapImage: $drawable")

        return bitmap
    }

    private fun encodeBitmapImage(bitmap: Bitmap?): String {

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

        val bytesOfImage: ByteArray = byteArrayOutputStream.toByteArray()

//        Log.i(
//            TAG,
//            "signUp --> encodeBitmapImage: ${Base64.encodeToString(bytesOfImage, Base64.DEFAULT)}"
//        )

        return Base64.encodeToString(bytesOfImage, Base64.DEFAULT)

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }

}