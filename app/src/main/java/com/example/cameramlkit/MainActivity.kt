package com.example.cameramlkit

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlin.math.round

class MainActivity : AppCompatActivity() {

    //request code for camera intent, authenticates for correct result
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //build button that launches camera app
        findViewById<Button>(R.id.camerabtn).setOnClickListener{
            //launch camera app with an implicit Intent (pass data around, any kind of camera app)
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                //display error state to user
            }
        }
    }

    //allows the app to do something with the photo we took (display it)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //grab Bitmap from image
            val imageBitmap = data?.extras?.get("data") as Bitmap

            //set bitmap as imageView image
            findViewById<ImageView>(R.id.image).setImageBitmap(imageBitmap)

            //prepare img for ML Kit API
            val imageForMLKit = InputImage.fromBitmap(imageBitmap, 0)

            //utilize image lableing API
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            var output = ""

            //pass image
            labeler.process(imageForMLKit)
                .addOnSuccessListener { labels ->
                   Log.i("Robin", "Did it!")
                    for (label in labels) {
                        val text = label.text
                        val confidence = label.confidence
                        val confidencepercent = round(confidence * 100)
                        output += "Detected: $text. Confidence: $confidencepercent%.\n "

                        //update UI to reflect changes
                        findViewById<TextView>(R.id.boxone).text = output
                        Log.i("Robin", "detected: " + text + " with confidence % of: " + round(confidence * 100))
                    }
                }
                .addOnFailureListener { e ->
                    Log.i("Robin", "Failed!!!")
                }
        }
    }
}

