package com.xingin.capa.memory.ui

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import noahzu.io.library.R


/**
 * Author: jzu
 * Date: 2018/10/22
 * Function:
 */
class FloatMemoryInfoView  : RelativeLayout{


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var previousLevel : Int = 1


    var tvCurrMemoryInfo : TextView
    var chartView : ChartView


    init {
        LayoutInflater.from(context).inflate(R.layout.capa_layout_memory_info,this)

        tvCurrMemoryInfo = findViewById(R.id.tvCurrMemoryInfo)
        chartView = findViewById(R.id.chartView)
    }



    fun update(double: Double){
        val currLevel = getMemoryLevel(double.toInt())
        if (currLevel != previousLevel){
            setBackByLevel(currLevel)
            previousLevel = currLevel
        }

        tvCurrMemoryInfo.text = String.format("%.2f Mb",double)
        chartView.addData(double)
    }

    private fun setBackByLevel(currLevel: Int) {
        when(currLevel){
            0 -> setBackgroundColor(Color.parseColor("#1000FF00"))
            1 -> setBackgroundColor(Color.parseColor("#3000FF00"))
            2 -> setBackgroundColor(Color.parseColor("#5000FF00"))
            3 -> setBackgroundColor(Color.parseColor("#6000FF00"))
            4 -> setBackgroundColor(Color.parseColor("#7000FF00"))
            5 -> setBackgroundColor(Color.parseColor("#8000FF00"))
        }
    }

    private fun getMemoryLevel(value : Int) : Int{
        if (value < 0){
            return 0
        }
        if (value > 500){
            return 5
        }
        return (value / 100)
    }
}