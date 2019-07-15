package com.example.monappli

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt


class Accelerometer : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accel: Sensor? = null
    private var maxValue: Int = 0
    private var lineGraph: GraphView? = null
    private var chartSize: Int = 500
    val seriesT = LineGraphSeries<DataPoint>()
    val seriesX = LineGraphSeries<DataPoint>()
    val seriesY = LineGraphSeries<DataPoint>()
    val seriesZ = LineGraphSeries<DataPoint>()
    private var valueId: Double = 0.toDouble()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accelerometer)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


        seriesT.color= Color.BLACK
        seriesX.color= Color.BLUE
        seriesY.color= Color.RED
        seriesZ.color= Color.GREEN
        lineGraph = findViewById<GraphView>(R.id.lineGraph)
        lineGraph?.getViewport()?.setXAxisBoundsManual(true)
        lineGraph?.getViewport()?.setMaxX(chartSize.toDouble())
        lineGraph?.getViewport()?.setMinX(0.toDouble())
        lineGraph?.addSeries(seriesT)
        lineGraph?.addSeries(seriesX)
        lineGraph?.addSeries(seriesY)
        lineGraph?.addSeries(seriesZ)


    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = (event.values[0]*1000).toInt().absoluteValue
        val y = (event.values[1]*1000).toInt().absoluteValue
        val z = (event.values[2]*1000).toInt().absoluteValue

        val total = (sqrt(event.values[0].pow(2) + event.values[1].pow(2) + event.values[2].pow(2))*1000).toInt().absoluteValue

        if (total>maxValue){
            maxValue=total
        }

        var silent = true
        if ((valueId%10) == 0.toDouble()){
            silent = false
        }

        seriesT.appendData(DataPoint(valueId,total.toDouble()),true,chartSize,true)
        seriesX.appendData(DataPoint(valueId,x.toDouble()),true,chartSize,true)
        seriesY.appendData(DataPoint(valueId,y.toDouble()),true,chartSize,true)
        seriesZ.appendData(DataPoint(valueId,z.toDouble()),true,chartSize,silent)
        lineGraph?.getViewport()?.setMaxX(valueId.toDouble())
        lineGraph?.getViewport()?.setMinX((valueId-chartSize).toDouble())

        valueId++


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
