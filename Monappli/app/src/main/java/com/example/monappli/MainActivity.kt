package com.example.monappli

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startGravityActivity(view: View){
        val intent = Intent(this, Gravity::class.java)
        startActivity(intent)
    }

    fun startAccelerometerUncalibratedActivity(view: View){
        val intent = Intent(this, AccelerometerUncalibrated::class.java)
        startActivity(intent)
    }

    fun startAccelerometerActivity(view: View){
        val intent = Intent(this, Accelerometer::class.java)
        startActivity(intent)
    }

    fun startGyroscopeActivity(view: View){
        val intent = Intent(this, Gyroscope::class.java)
        startActivity(intent)
    }

    fun startGyroscopeUcalibratedActivity(view: View){
        val intent = Intent(this, GyroscopeUncalibrated::class.java)
        startActivity(intent)
    }

    fun startLinearAccelerationActivity(view: View){
        val intent = Intent(this, LinearAcceleration::class.java)
        startActivity(intent)
    }

    fun startRotationVectorActivity(view: View){
        val intent = Intent(this, RotationVector::class.java)
        startActivity(intent)
    }

    fun startStepsActivity(view: View){
        val intent = Intent(this, StepCounter::class.java)
        startActivity(intent)
    }

}
