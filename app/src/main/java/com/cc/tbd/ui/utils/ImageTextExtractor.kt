package com.cc.tbd.ui.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class ImageTextExtractor(val context: Context) :TextExtractor {
    private val TAG = "ImageTextExtractor"

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    override suspend fun image(uri: Uri): Flow<String> = callbackFlow {
        val image = InputImage.fromFilePath(context,uri)

        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                 val resultText = visionText.text
                launch {  send(resultText) }

                Log.v(TAG,"extractedText :$resultText")
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // handle exception or show toast message
               val resultText = e.localizedMessage.toString()
                launch {  send(resultText) }
                Log.v(TAG,"extractedText :$resultText")
            }

        awaitClose()
    }.flowOn(Dispatchers.IO)
}