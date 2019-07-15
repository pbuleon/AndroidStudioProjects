package com.example.monappli

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class AccelerometerUncalibrated : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accel: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accelerometer_uncalibrated)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED)
        if (accel == null){
            findViewById<TextView>(R.id.accelerometerUncalibratedX).apply {
                text = "Not Avaialable"
            }
            findViewById<TextView>(R.id.accelerometerUncalibratedY).apply {
                text ="Not Avaialable"
            }
            findViewById<TextView>(R.id.accelerometerUncalibratedZ).apply {
                text = "Not Avaialable"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        findViewById<TextView>(R.id.accelerometerUncalibratedX).apply {
            text = event.values[0].toString()
        }
        findViewById<TextView>(R.id.accelerometerUncalibratedY).apply {
            text = event.values[1].toString()
        }
        findViewById<TextView>(R.id.accelerometerUncalibratedZ).apply {
            text = event.values[2].toString()
        }

    }

    override fun onResume() {
        super.onResume()
        accel?.also { a ->
            sensorManager.registerListener(this, a, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}
