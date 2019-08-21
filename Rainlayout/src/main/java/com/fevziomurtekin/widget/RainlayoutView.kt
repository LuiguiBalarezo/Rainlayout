package com.fevziomurtekin.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.lib_layout.view.*
import kotlinx.coroutines.*

class RainlayoutView : ConstraintLayout {

    /** attributes of layout **/
    private var rainSrc = R.drawable.ic_umbrella
    private var dropPerSecond = 10
    private var dropTintColor = android.R.color.white
    private var fallToDropTime = 100
    private var isColorful = false

    private var animationJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by RainViewModel.
     * Since we pass viewModelJob, you can cancel all coroutines
     * launched by uiScope by calling viewModelJob.cancel()
     */

    private var uiScope = CoroutineScope(Dispatchers.Main + animationJob)

    constructor(context: Context?) : super(context) { init(context,null,0,0) }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { init(context,attrs,0,0) }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init(context,attrs,defStyleAttr,0) }

    private fun init(context: Context?,attrs:AttributeSet?,defStyleAttr: Int,defStyleRes: Int){
        context?.theme?.obtainStyledAttributes(attrs,R.styleable.rain,defStyleAttr,defStyleRes)?.let {
            rainSrc = it.getInt(R.styleable.rain_rainSrc,R.drawable.ic_umbrella)
            dropPerSecond = it.getInt(R.styleable.rain_dropPerSecond,10)
            dropTintColor = it.getInt(R.styleable.rain_dropTintColor,android.R.color.white)
            fallToDropTime = it.getInt(R.styleable.rain_fallToDropTime,100)
            isColorful = it.getBoolean(R.styleable.rain_isColorful,false)
            initViews()
        }
    }

    /** initViews for Rainlayout */
    private fun initViews(){
        val view = LayoutInflater.from(context!!).inflate(R.layout.lib_layout,null)
        this@RainlayoutView.apply {
            val clAnimation = view.findViewById<ConstraintLayout>(R.id.cl_lib_layout)
            val constraintSet = ConstraintSet()
            constraintSet.clone(this)
            addView(view)
            constraintSet.apply {
                connect(clAnimation.id,ConstraintSet.RIGHT,this@RainlayoutView.id,ConstraintSet.RIGHT,0)
                connect(clAnimation.id,ConstraintSet.LEFT,this@RainlayoutView.id,ConstraintSet.LEFT,0)
                connect(clAnimation.id,ConstraintSet.TOP,this@RainlayoutView.id,ConstraintSet.TOP,0)
                connect(clAnimation.id,ConstraintSet.BOTTOM,this@RainlayoutView.id,ConstraintSet.BOTTOM,0)
            }
            constraintSet.applyTo(this)
        }
        showAnimation()
    }


    fun showAnimation(){
        if(!uiScope.isActive){
            animationJob = SupervisorJob()
            uiScope = CoroutineScope(Dispatchers.Main + animationJob)
        }

        uiScope.launch {
            repeat(100000){
                RainAnimation.showAnimation(
                    context,
                    this@RainlayoutView.findViewById(R.id.rl_animation) as RelativeLayout,
                    fallToDropTime,
                    rainSrc,
                    dropTintColor,
                    isColorful)
                delay((1000/dropPerSecond).toLong())
            }
        }
    }

    fun animationClear() = animationJob.cancel()

}