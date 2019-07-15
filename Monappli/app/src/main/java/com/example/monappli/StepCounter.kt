package com.example.monappli

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class StepCounter : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gravity: Sensor? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_counter)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (gravity == null){
            findViewById<TextView>(R.id.steps).apply {
                text = "Not Avaialable"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        findViewById<TextView>(R.id.steps).apply {
            text = event.values[0].toString()
        }

    }

    override fun onResume() {
        super.onResume()
        gravity?.also { g ->
            sensorManager.registerListener(this, g, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}
