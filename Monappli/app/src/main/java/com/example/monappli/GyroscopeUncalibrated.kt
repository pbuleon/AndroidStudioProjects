package com.example.monappli

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class GyroscopeUncalibrated : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gravity: Sensor? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gyroscope_uncalibrated)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED)
        if (gravity == null){
            findViewById<TextView>(R.id.gyroscopeUncalibratedX).apply {
                text = "Not Avaialable"
            }
            findViewById<TextView>(R.id.gyroscopeUncalibratedY).apply {
                text ="Not Avaialable"
            }
            findViewById<TextView>(R.id.gyroscopeUncalibratedZ).apply {
                text = "Not Avaialable"
            }
            findViewById<TextView>(R.id.gyroscopeUncalibratedDriftX).apply {
                text = "Not Avaialable"
            }
            findViewById<TextView>(R.id.gyroscopeUncalibratedDriftY).apply {
                text = "Not Avaialable"
            }
            findViewById<TextView>(R.id.gyroscopeUncalibratedDriftZ).apply {
                text = "Not Avaialable"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        findViewById<TextView>(R.id.gyroscopeUncalibratedX).apply {
            text = event.values[0].toString()
        }
        findViewById<TextView>(R.id.gyroscopeUncalibratedY).apply {
            text = event.values[1].toString()
        }
        findViewById<TextView>(R.id.gyroscopeUncalibratedZ).apply {
            text = event.values[2].toString()
        }
        findViewById<TextView>(R.id.gyroscopeUncalibratedDriftX).apply {
            text = event.values[3].toString()
        }
        findViewById<TextView>(R.id.gyroscopeUncalibratedDriftY).apply {
            text = event.values[4].toString()
        }
        findViewById<TextView>(R.id.gyroscopeUncalibratedDriftZ).apply {
            text = event.values[5].toString()
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
