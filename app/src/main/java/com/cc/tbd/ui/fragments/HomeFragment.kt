package com.cc.tbd.ui.fragments

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cc.tbd.R
import com.cc.tbd.adapters.SaveNotesAdapter
import com.cc.tbd.databinding.FragmentHomeBinding
import com.cc.tbd.models.Note
import com.cc.tbd.models.SaveNotes
import com.cc.tbd.models.SubNote
import com.cc.tbd.models.User
import com.cc.tbd.ui.activities.MainActivity
import com.cc.tbd.ui.utils.BottomSheetDialog
import com.cc.tbd.ui.utils.ImageTextExtractor
import com.cc.tbd.ui.utils.NoteApplication
import com.cc.tbd.ui.utils.TextExtractor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch
import java.io.File


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mContext: MainActivity
    private val TAG = "HomeFragment"
    var uri: Uri? = null
    private var adapter: SaveNotesAdapter? = null
    var notes: ArrayList<Note> = arrayListOf()


    // on below line we are creating a new broad cast manager.
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        // we will receive data updates in onReceive method.
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            uri = Uri.parse(intent.getStringExtra("uri"))
            // on below line we are updating the data in our text view.
//            mContext.mList.add(
//                Note(
//                    fileUri = uri
//                )
//            )
            findNavController().navigate(R.id.action_homeFragment_to_notesFragment)


            Toast.makeText(context, "$uri", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBroadcast()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        FirebaseFirestore.getInstance().collection("user").document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { value, error ->

                if(error == null){
                    mContext.currentUser = value?.toObject(User::class.java)!!
                }

                Log.v("TAG","current user referal size :${mContext.currentUser.referral?.size}")
            }


          val directory1 = mContext.cacheDir
          val directory2 = mContext.externalCacheDir
           getDirectorySize(File(directory1.absolutePath))

        binding.imageView11.setOnClickListener {
            // check condition
            mContext.getPdf()

        }
        binding.textView10.setOnClickListener {
            binding.view2.visibility = View.VISIBLE
            binding.view1.visibility = View.GONE
        }
        binding.textView11.setOnClickListener {
            binding.view2.visibility = View.GONE
            binding.view1.visibility = View.VISIBLE
        }

        binding.imageView12.setOnClickListener {
            Dexter.withContext(requireContext())
                .withPermissions(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        mContext.isRecording = true
                        findNavController().navigate(R.id.action_homeFragment_to_notesFragment)
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        list: List<PermissionRequest?>?,
                        permissionToken: PermissionToken?
                    ) {
                    }
                }).check()

        }
        if(adapter == null){
            setUpRecyclerView()
        }else{
            adapter?.notifyDataSetChanged()
        }
        loadNotes()




        binding.imageView7.setOnClickListener {
            toggleLeftDrawer()
        }
        binding.leftDrawerMenu.imageView15.setOnClickListener {
            toggleLeftDrawer()
        }
        binding.leftDrawerMenu.textView26.setOnClickListener {
            toggleLeftDrawer()
            shareReferalCode()
        }
        binding.leftDrawerMenu.logout.setOnClickListener {
            toggleLeftDrawer()
          logout()
        }

        binding.textView6.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notesFragment)
        }

        binding.textView7.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notesFragment)
        }
        binding.imageView10.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_sketchFragment)
        }

        binding.imageView9.setOnClickListener {
            val bottomSheet = BottomSheetDialog {
                it?.let {
                    if (NoteApplication.isTextScan) {
                        NoteApplication.isTextScan = false
                        // extract text from image
                        lifecycleScope.launch {
                            val textExtractor: TextExtractor = ImageTextExtractor(mContext)
                            textExtractor.image(it).collect {
                                Log.v(TAG, "extractedText :$it")
                                Toast.makeText(mContext, "text:$it", Toast.LENGTH_SHORT).show()
                                mContext.mList.add(
                                    SubNote(
                                        scanText = it
                                    )
                                )
                                findNavController().navigate(R.id.action_homeFragment_to_notesFragment)
                            }
                        }
                    } else {
                        mContext.mList.add(
                            SubNote(
                                imageUri = it.toString()
                            )
                        )
                        findNavController().navigate(R.id.action_homeFragment_to_notesFragment)
                    }
                } ?: kotlin.run {
                    TedImagePicker.with(mContext).showCameraTile(false)
                        .startMultiImage { uriList -> showMultiImage(uriList) }
                }
            }
            bottomSheet.show(
                requireActivity().supportFragmentManager,
                "ModalBottomSheet"
            )

        }



        binding.leftDrawerMenu.textView17.setOnClickListener {
            toggleLeftDrawer()
            findNavController().navigate(R.id.action_homeFragment_to_myProfile)
        }

        binding.imageView8.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_subscriptionPackages)
        }

        return binding.root

    }

    private fun loadNotes() {

        FirebaseFirestore.getInstance()
            .collection("Notes")
            .document(FirebaseAuth.getInstance().uid.toString())
            .collection("Notes")
            .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(EventListener<QuerySnapshot?> { value, e ->
                    if (e != null)
                    {
                        Log.i("TAGTAGTAG", "Listen failed.", e)

                     //   groupResponse.postValue(APIResponse.onTaskError(e.localizedMessage))
                        return@EventListener
                    }

                    notes.clear()
                    for (doc in value!!) {
                        val note = doc.toObject(Note::class.java)
                       notes.add(note)
                    }
                    adapter?.notifyDataSetChanged()

                })

    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }

    private fun shareReferalCode() {
        /*Create an ACTION_SEND Intent*/
        /*Create an ACTION_SEND Intent*/
        val intent = Intent(Intent.ACTION_SEND)
        /*This will be the actual content you wish you share.*/
        /*This will be the actual content you wish you share.*/
        val shareBody = "Your Referral Code is:\n${FirebaseAuth.getInstance().currentUser?.uid}"
        /*The type of the content is text, obviously.*/
        /*The type of the content is text, obviously.*/intent.type = "text/plain"
        /*Applying information Subject and Body.*/
        /*Applying information Subject and Body.*/intent.putExtra(
            Intent.EXTRA_SUBJECT,
            getString(com.cc.tbd.R.string.referral_code)
        )
        intent.putExtra(Intent.EXTRA_TEXT, shareBody)
        /*Fire!*/
        /*Fire!*/startActivity(Intent.createChooser(intent, getString(com.cc.tbd.R.string.share_using)))
    }

    fun setUpRecyclerView() {
            binding.recSaveNotes.layoutManager = GridLayoutManager(context, 2)

        // on below line we are initializing our adapter
        adapter = SaveNotesAdapter(notes, requireContext())

        // on below line we are setting
        // adapter to our recycler view.
         binding.recSaveNotes.adapter = adapter

        // on below line we are notifying adapter
        // that data has been updated.
        adapter?.notifyDataSetChanged()
    }

    private var cacheSize = 0L
    private fun getDirectorySize(file: File) {
        Log.v("TAG","directory path :${file.absolutePath}")
         if(file.isDirectory){
             Log.v("TAG","directory")
             val filelist = file.listFiles()
             filelist?.let {
                 it.forEach {
                     if(it.isDirectory){
                         getDirectorySize(it)
                     }else{

                             Log.v("TAG", "file :${it.absolutePath}  ::size :${it.length()}")
                             cacheSize += it.length()

                     }
                 }
             } ?:    Log.v("TAG","directory null")

         }else{
             Log.v("TAG","not directory")
         }

        Log.v("TAG", "total cache size :$cacheSize")
    }

    private fun showMultiImage(uriList: List<Uri>) {

        uriList.forEach {
            mContext.mList.add(SubNote(
                imageUri = it.toString()
            ))
        }

        findNavController().navigate(R.id.action_homeFragment_to_notesFragment)
    }


    private fun toggleLeftDrawer() {
        if (binding.drawerLayout.isDrawerOpen(binding.leftDrawerMenu.root))
            binding.drawerLayout.closeDrawer(binding.leftDrawerMenu.root)
        else binding.drawerLayout.openDrawer(binding.leftDrawerMenu.root)
    }


    private fun registerBroadcast() {
        // on below line we are registering our local broadcast manager.
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter("com.cc.tbd.filePick"));
    }

    private fun unRegisterBroadcast() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }


    override fun onDestroy() {
        super.onDestroy()
        unRegisterBroadcast()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }


}