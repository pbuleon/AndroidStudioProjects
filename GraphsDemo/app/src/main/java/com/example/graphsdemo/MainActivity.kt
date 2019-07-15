package com.example.graphsdemo

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val barGraph = findViewById<GraphView>(R.id.lbar_graph)
        val series = BarGraphSeries(arrayOf(DataPoint(0.toDouble(), 1.toDouble()), DataPoint(1.toDouble(), 5.toDouble()), DataPoint(2.toDouble(), 3.toDouble()), DataPoint(3.toDouble(), 2.toDouble()), DataPoint(4.toDouble(), 6.toDouble())))
        barGraph.addSeries(series)

        series.setColor(Color.GREEN)
        series.setTitle("titre")
        series.setSpacing(2)



    }
}

