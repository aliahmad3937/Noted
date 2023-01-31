package com.cc.tbd.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.cc.tbd.R
import com.cc.tbd.models.Note
import com.cc.tbd.models.SubNote
import com.cc.tbd.models.User
import com.github.dhaval2404.imagepicker.ImagePicker

class MainActivity : AppCompatActivity() {

    var bitmap:Bitmap? = null
    var mList: ArrayList<SubNote> = arrayListOf()
    var myNote:MutableList<Bitmap> = mutableListOf()
    var sUri: Uri? = null


     var isRecording = false

    private lateinit var  resultLauncher: ActivityResultLauncher<Intent>
    var plan = ""
    var price = ""
    var days =""

    var currentUser:User = User()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        resultLauncher =  registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                var data =  result.data
                if (data != null) {
                    // When data is not equal to empty
                    // Get PDf uri
                    sUri = data.data
                    // set Uri on text view
//                    tvUri.setText(
//                        Html.fromHtml(
//                            "<big><b>PDF Uri</b></big><br>"
//                                    + sUri
//                        )
//                    )
//
//                    // Get PDF path
//                    val sPath: String = sUri.getPath()
//                    // Set path on text view
//                    tvPath.setText(
//                        Html.fromHtml(
//                            (
//                                    "<big><b>PDF Path</b></big><br>"
//                                            + sPath)
//                        )
//                    )


                    sendBroadcast()
                }
            } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun sendBroadcast() {
        // inside on click we are calling the intent with the action.
        val intent = Intent("com.cc.tbd.filePick");
        // on below line we are passing data to our broad cast receiver with key and value pair.
        intent.putExtra("uri", "$sUri");
        // on below line we are sending our broad cast with intent using broad cast manager.
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    fun getPdf() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission
                    .READ_EXTERNAL_STORAGE)
            != PackageManager
                .PERMISSION_GRANTED) {
            // When permission is not granted
            // Result permission
            ActivityCompat.requestPermissions(
               this,
                arrayOf(
                    Manifest.permission
                        .READ_EXTERNAL_STORAGE ),
                1);
        }
        else {
            // When permission is granted
            // Create method
            selectPDF();
        }
    }

    private fun selectPDF() {
        // Initialize intent
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        // set type
       intent.type = "application/*"
     //   intent.type = "*/*"
        // Launch intent
        resultLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // check condition
        if (requestCode == 1 && grantResults.size > 0
            && grantResults[0]
            == PackageManager.PERMISSION_GRANTED) {
            // When permission is granted
            // Call method
            selectPDF();
        }
        else {
            // When permission is denied
            // Display toast
            Toast
                .makeText(this,
                    "Permission Denied",
                    Toast.LENGTH_SHORT)
                .show();
        }
    }


}