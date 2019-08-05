package com.rain.liba_customviewutil.widget

import android.graphics.*
import android.content.Context
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.graphics.Bitmap
import com.rain.liba_customviewutil.R
import com.rain.liba_customviewutil.util.DensityUtils
import com.rain.liba_customviewutil.util.ThreadPoolManage

/**
 *
 *  xfermode保证和图上的效果一样必须满足的条件
 * 1.canvas必须绘制bitmap
 * 2.绘制bitmap的paint和绘制创建bitmap使用的paint必须不一样
 * 3.透明度会影响xfermode的生成的透明效果
 *
 *
 * @author Rain
 * @Date on 2019/07/01
 * @Description 气泡炫酷效果
 */
class SuperBubbleCoolView : View {

    //控件的真实宽度
    private var mRealWidth = 0
    //控件的真实高度
    private var mRealHeight = 0
    //当控件没设死高度时的默认宽度
    private var mDefaultWidth = DensityUtils.dp2px(context, 200f)
    //当控件没设死高度时的默认高度
    private var mDefaultHeight = DensityUtils.dp2px(context, 200f)
    //内边距用于做阴影之类的
    private var mPadding = DensityUtils.dp2px(context, 12.5f)
    private var mPercent = 30f
    private var mSpeedUp = 0.1f
    private var mSpeedGo = 2f
    private var mCurrentHeight = 0f
    private var mWaveCount = 0
    private var mStartWidth = 0f
    private val mWaveWidth = DensityUtils.dp2px(context, 100f).toFloat()
    private val mWaveHeight = DensityUtils.dp2px(context, 20f).toFloat()
    private var mIsUp = true

    /**
     * 控制贝塞尔曲线控制点偏移的值
     */
    private var mControlValue = 0f
    private var mRadialGradient: RadialGradient? = null
    private val mPorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    /**
     * 画布id
     */
    private var mLayerId = 0
    private var mBgBitmap: Bitmap
    private var mSrcBitmap: Bitmap? = null
    private var mDstBitmap: Bitmap? = null
    private var mDst2Bitmap: Bitmap? = null
    private var mSrcCanvas = Canvas()
    private var mDstCanvas = Canvas()
    private var mDst2Canvas = Canvas()
    /**
     * view是居中正方形,width就边长
     */
    private var mSquareSize = 0f
    private var mCenterX = 0f
    private var mCenterY = 0f
    //气泡中的文字画笔
    private var mBubbleTextPaint = TextPaint()
    private var mPaint = Paint()
    private var mDrawPaint = Paint()
    private var mMatrix = Matrix()
    private var mPath = Path()
    private var mRectF = RectF()

    private var mBubbleFrontColor = Color.parseColor("#35BAC2")
    private var mBubbleLaterColor = Color.parseColor("#67EEFA")
    private var mBubbleShaderColor = Color.parseColor("#054000")

    private var mBubbleText = ""
    private var mBubbleSecondText = ""
    private var mIsOpen = true
    private var mRunnable = Runnable {
        startAnimator()
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context, attributeSet, defStyleAttr
    )

    init {
        //关闭渲染
        mPaint.isAntiAlias = true
        mDrawPaint.isAntiAlias = true
        mBubbleTextPaint.isAntiAlias = true
        mBubbleTextPaint.color = Color.WHITE
        mBubbleTextPaint.style = Paint.Style.FILL
        mBubbleTextPaint.textSize = DensityUtils.dp2px(context, 11f).toFloat()
        mBgBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bubble_bg)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> {
                mRealWidth = MeasureSpec.getSize(widthMeasureSpec)
            }
            MeasureSpec.AT_MOST -> {
                mRealWidth = mDefaultWidth
            }
            MeasureSpec.UNSPECIFIED -> {
                mRealWidth = mDefaultWidth
            }
        }
        when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> {
                mRealHeight = MeasureSpec.getSize(heightMeasureSpec)
            }
            MeasureSpec.AT_MOST -> {
                mRealHeight = mDefaultHeight
            }
            MeasureSpec.UNSPECIFIED -> {
                mRealHeight = mDefaultHeight
            }
        }
        initAndCountSize()
        setMeasuredDimension(mRealWidth, mRealHeight)
    }

    private fun initAndCountSize() {
        mSquareSize = Math.min(mRealWidth, mRealHeight).toFloat()
        mSquareSize -= 2 * mPadding
        mCenterX = mRealWidth / 2f
        mCenterY = mRealHeight / 2f
        mWaveCount = Math.ceil((mSquareSize / mWaveWidth).toDouble()).toInt()
        mControlValue = mWaveWidth / 5f * 2f
        mRadialGradient = RadialGradient(
            mCenterX, mCenterY, mSquareSize / 3f * 2f, mBubbleLaterColor, mBubbleFrontColor, Shader.TileMode.CLAMP
        )
        mMatrix.reset()
        mMatrix.setScale(
            (mSquareSize + mPadding * 2) / mBgBitmap.width.toFloat(),
            (mSquareSize + mPadding * 2) / mBgBitmap.height.toFloat()
        )
        mRectF.set(0f, 0f, mRealWidth.toFloat(), mRealHeight.toFloat())
        //画混合需要的背景圆
        mSrcBitmap = Bitmap.createBitmap(mRealWidth, mRealHeight, Bitmap.Config.ARGB_8888)
        mSrcCanvas.setBitmap(mSrcBitmap)
        mPaint.color = Color.WHITE
        mSrcCanvas.drawCircle(mCenterX, mCenterY, mSquareSize / 2, mPaint)
        mDstBitmap = Bitmap.createBitmap(mRealWidth, mRealHeight, Bitmap.Config.ARGB_8888)
        mDstCanvas.setBitmap(mDstBitmap)
        mDst2Bitmap = Bitmap.createBitmap(mRealWidth, mRealHeight, Bitmap.Config.ARGB_8888)
        mDst2Canvas.setBitmap(mDst2Bitmap)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ThreadPoolManage.getInstance().execute(mRunnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ThreadPoolManage.getInstance().remove(mRunnable)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            mCurrentHeight = (1 - mPercent / 100f) * mSquareSize + mPadding
            canvas.drawBitmap(mBgBitmap, mMatrix, null)

            mLayerId = canvas.saveLayer(mRectF, mDrawPaint, Canvas.ALL_SAVE_FLAG)
            canvas.drawBitmap(mSrcBitmap, 0f, 0f, mDrawPaint)
            mDrawPaint.xfermode = mPorterDuffXfermode
            drawDstBitmap()
            canvas.drawBitmap(mDstBitmap, 0f, 0f, mDrawPaint)
            mDrawPaint.xfermode = null
            canvas.restoreToCount(mLayerId)

            mLayerId = canvas.saveLayer(mRectF, mDrawPaint, Canvas.ALL_SAVE_FLAG)
            canvas.drawBitmap(mSrcBitmap, 0f, 0f, mDrawPaint)
            mDrawPaint.xfermode = mPorterDuffXfermode
            drawDst2Bitmap()
            canvas.drawBitmap(mDst2Bitmap, 0f, 0f, mDrawPaint)
            mDrawPaint.xfermode = null
            canvas.restoreToCount(mLayerId)

            drawText(canvas)
        }
    }

    /**
     * 画里层的贝塞尔曲线Bitmap
     */
    private fun drawDstBitmap() {
        //清除掉图像 不然图像会重叠
        mDstBitmap?.eraseColor(Color.TRANSPARENT)
        mPaint.color = mBubbleFrontColor
        mPath.reset()
        mPath.moveTo(mStartWidth, mCurrentHeight)
        for (i in 0 until mWaveCount * 2) {
            mPath.cubicTo(
                mStartWidth + mPadding.toFloat() + mWaveWidth * i + mControlValue, mCurrentHeight - mWaveHeight,
                mStartWidth + mPadding.toFloat() + mWaveWidth * (i + 1) - mControlValue, mCurrentHeight + mWaveHeight,
                mStartWidth + mPadding.toFloat() + mWaveWidth * (i + 1), mCurrentHeight
            )
        }
        mPath.lineTo(mRealWidth.toFloat(), mRealHeight.toFloat())
        mPath.lineTo(0f, mRealHeight.toFloat())
        mPath.close()
        mDstCanvas.drawPath(mPath, mPaint)
    }

    /**
     * 画外层的贝塞尔曲线Bitmap
     */
    private fun drawDst2Bitmap() {
        //清除掉图像 不然图像会重叠
        mDst2Bitmap?.eraseColor(Color.TRANSPARENT)
        mPaint.color = mBubbleLaterColor
//        mPaint.setShadowLayer(10f, 5f, 5f, mBubbleShaderColor)
        mPaint.shader = mRadialGradient
        mPath.reset()
        mPath.moveTo(mStartWidth, mCurrentHeight)
        for (i in 0 until mWaveCount * 2) {
            mPath.cubicTo(
                mStartWidth + mPadding.toFloat() + mWaveWidth * i + mControlValue, mCurrentHeight + mWaveHeight,
                mStartWidth + mPadding.toFloat() + mWaveWidth * (i + 1) - mControlValue, mCurrentHeight - mWaveHeight,
                mStartWidth + mPadding.toFloat() + mWaveWidth * (i + 1), mCurrentHeight
            )
        }
        mPath.lineTo(mRealWidth.toFloat(), mRealHeight.toFloat())
        mPath.lineTo(0f, mRealHeight.toFloat())
        mPath.close()
        mDst2Canvas.drawPath(mPath, mPaint)
//        mPaint.clearShadowLayer()
        mPaint.shader = null
    }

    /**
     * 画文字
     */
    private fun drawText(canvas: Canvas) {
        if (mBubbleText.isNotEmpty()) {
            val staticLayout = StaticLayout(
                mBubbleText, mBubbleTextPaint, (mSquareSize / 5 * 3).toInt(),
                Layout.Alignment.ALIGN_CENTER, 1.0f, 0f, true
            )
            canvas.save()
            canvas.translate(
                mCenterX - (mSquareSize / 5 * 3) / 2,
                mCenterY - mSquareSize / 3 + mPadding
            )
            staticLayout.draw(canvas)
            canvas.restore()
        }
        if (mBubbleSecondText.isNotEmpty()) {
            val staticLayout2 = StaticLayout(
                mBubbleSecondText, mBubbleTextPaint, (mSquareSize / 5 * 3).toInt(),
                Layout.Alignment.ALIGN_CENTER, 1.0f, 0f, true
            )
            canvas.save()
            canvas.translate(mCenterX - (mSquareSize / 5 * 3) / 2, mCenterY + mSquareSize / 5)
            staticLayout2.draw(canvas)
            canvas.restore()
        }
    }

    /**
     * 开启动画
     */
    fun startAnimator() {
        while (mIsOpen) {
            Thread.sleep(10)
            startGo()
            startUpDown()
            postInvalidate()
        }
    }

    /**
     * 加入大波浪效果
     */
    private fun startGo() {
        if (mStartWidth >= 0) {
            mStartWidth = -mWaveCount * mWaveWidth
        }
        mStartWidth += mSpeedGo
    }

    /**
     * 加入上升下降效果
     */
    private fun startUpDown() {
        if (mIsUp) {
            if (mPercent >= 100) {
                mIsUp = false
                mPercent -= mSpeedUp
            } else {
                mPercent += mSpeedUp
            }
        } else {
            if (mPercent <= 0f) {
                mIsUp = true
                mPercent += mSpeedUp
            } else {
                mPercent -= mSpeedUp
            }
        }
    }

    /**
     * 设置数据
     */
    fun setBubbleText(upText: String, downText: String?, isNotify: Boolean = false) {
        mBubbleText = upText
        downText?.let {
            mBubbleSecondText = downText
        }
        if (isNotify) {
            invalidate()
        }
    }

    fun setBubbleBg(mBubbleBitmap: Bitmap, isNotify: Boolean = false) {
        this.mBgBitmap = mBubbleBitmap
        if (isNotify) {
            invalidate()
        }
    }

    /**
     * 设置水的比例
     */
    fun setRatio(curScore: Int, totalScore: Int, isNotify: Boolean = false) {
        mPercent = (curScore / totalScore.toDouble() * 100.0).toFloat()
        if (isNotify) {
            invalidate()
        }
    }

}