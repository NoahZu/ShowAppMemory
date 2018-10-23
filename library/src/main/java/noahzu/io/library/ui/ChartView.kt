package com.xingin.capa.memory.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.collections.ArrayList

/**
 * Author: jzu
 * Date: 2018/10/20
 * Function:
 */
class ChartView : View{

    var mWidth = 0//高度
    var mHeight = 0//宽度

    val anixWidth = 3f//画笔宽度

    val mYSumCount = 500 //Y轴最大表示数量
    val mSpanCount = 50 //Y轴刻度之间数量表示
    var mYPerCountSpan : Float = 0f//y轴上一个单位多少个像素，根据控件高度计算

    val anixPaint : Paint = Paint() //画轴与刻度
    val linePaint : Paint = Paint() //画线
    val textPaint : Paint = Paint() //画文字

    val tableStartXPos = 30f//表格左侧距离

    val extMinWidth = 20f //当X刻度之间大于这个宽度的时候不用重新计算刻度

    val dataPoolMax = 200 //数据池最大数量，超过了会移出1/3

    val dataList by lazy {
        ArrayList<Double>()
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        mYPerCountSpan = mHeight.toFloat() / mYSumCount
    }


    init {
        initPaint()
    }

    private fun initPaint() {
        anixPaint.setStyle(Paint.Style.STROKE)
        anixPaint.setAntiAlias(true)
        anixPaint.setColor(Color.BLUE)
        anixPaint.strokeWidth = anixWidth

        linePaint.setStyle(Paint.Style.STROKE)
        linePaint.setAntiAlias(true)
        linePaint.setColor(Color.RED)
        linePaint.strokeWidth = 2f


        textPaint.setStyle(Paint.Style.STROKE)
        textPaint.setAntiAlias(true)
        textPaint.setColor(Color.BLUE)

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null){
            return
        }

        drawAnix(canvas)
        drawLineByData(canvas)
    }

    private fun drawAnix(canvas: Canvas) {
        canvas.drawLine(tableStartXPos,0f,tableStartXPos,mHeight.toFloat(),anixPaint)//y
        canvas.drawLine(tableStartXPos,mHeight.toFloat(),mWidth.toFloat(),mHeight.toFloat(),anixPaint)//x

        //画Y刻度

        val anixCount = mYSumCount / mSpanCount

        for (i in 1 .. anixCount){

            val startP = PointF(tableStartXPos,(i * mYPerCountSpan * mSpanCount).toFloat())
            val endP = PointF(tableStartXPos+20f,(i * mYPerCountSpan * mSpanCount).toFloat())
            canvas.drawLine(startP.x,startP.y,endP.x,endP.y,anixPaint)

            val anxValue = (anixCount - i)  * mSpanCount
            canvas.drawText(anxValue.toString(),0f,i * mYPerCountSpan * mSpanCount,textPaint)
        }
    }



    private fun drawLineByData(canvas: Canvas) {
        if (dataList.size <= 1){
            return
        }
        var currSpan = 0f
        if (mWidth.toFloat() / dataList.size > extMinWidth){
            currSpan = extMinWidth;
        }else{
            currSpan = mWidth.toFloat() / dataList.size
        }

        val path = Path()
        dataList.forEachIndexed{ index: Int, value: Double ->
            val trueV = if (value > 500) 500 else value.toInt()
            if (path.isEmpty){
                path.rMoveTo(index * currSpan + tableStartXPos,(500 - trueV) * mYPerCountSpan )
            }
            path.lineTo(index * currSpan + tableStartXPos,(500 - trueV) * mYPerCountSpan )
        }

        canvas.drawPath(path,linePaint)

    }


    fun addData(value : Double){
        dataList.add(value)

        //删除1/3元素 保证曲线的可观测性
        if (dataList.size >= dataPoolMax){
            dataList.subList(0,dataList.size / 3).clear()
        }

        invalidate()
    }
}