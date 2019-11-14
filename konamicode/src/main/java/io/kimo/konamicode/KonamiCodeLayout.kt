package io.kimo.konamicode

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_buttons.view.*
import java.util.ArrayList
import kotlin.math.abs

class KonamiCodeLayout : FrameLayout, KonamiSequenceListener {
    private var mCallback: Callback? = null
    private var done = false

    private var buttonDialog: AlertDialog? = null
    private val buttonsClickListener = OnClickListener { v ->
        when (v.id) {
            R.id.konami_button_a -> mLastPressedButton = Button.A
            R.id.konami_button_b -> mLastPressedButton = Button.B
            R.id.konami_button_start -> mLastPressedButton = Button.START
        }
        registerPress()
    }

    private var mLastSwipedDirection = Direction.NONE
    private var mLastPressedButton = Button.NONE
    private var mSwipeThreshold: Int = 0

    private var mLastX: Float = 0f
    private var mLastY: Float = 0f

    private var mKonamiCodeDirectionsOrder = KonamiCode.KonamiSequence.defaultSequence.sequence.toList()
    private var mKonamiCodeButtonsOrder = KonamiCode.KonamiSequence.defaultSequence.buttons.toList()

    private val mSwipes = ArrayList<Direction>()
    private val mPressedButtons = ArrayList<Button>()

    /**
     * Callback - Interface that's executed when the code finishes
     */
    interface Callback {
        fun onFinish()
    }

    /**
     * Enumeration of swipe directions
     */
    enum class Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }

    /**
     * Enumeration of the buttons
     */
    enum class Button {
        A, B, START, NONE
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun setSequence(vararg sequence: Direction) {
        mKonamiCodeDirectionsOrder = listOf(*sequence)
    }

    fun setButtonOrder(vararg buttons: Button) {
        mKonamiCodeButtonsOrder = listOf(*buttons)
    }

    private fun init() {
        val viewConfiguration = ViewConfiguration.get(context)
        mSwipeThreshold = viewConfiguration.scaledTouchSlop

        buttonDialog = AlertDialog.Builder(context)
            .setView(LayoutInflater.from(context).inflate(R.layout.dialog_buttons, this, false)
                .apply {
                    konami_button_a.setOnClickListener(buttonsClickListener)
                    konami_button_b.setOnClickListener(buttonsClickListener)
                    konami_button_start.setOnClickListener(buttonsClickListener)
                })
            .create()
    }

    fun setCallback(callback: Callback) {
        mCallback = callback
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val child = getChildAt(0)
        return child != null && child.dispatchTouchEvent(event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!done)
            processTouches(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun processTouches(ev: MotionEvent) {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastX = ev.x
                mLastY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                val diffY = ev.y - mLastY
                val diffX = ev.x - mLastX

                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > mSwipeThreshold) {
                        mLastSwipedDirection = if (diffX > 0) {
                            Direction.RIGHT
                        } else {
                            Direction.LEFT
                        }
                    }
                } else if (abs(diffY) > mSwipeThreshold) {
                    mLastSwipedDirection = if (diffY > 0) {
                        Direction.DOWN
                    } else {
                        Direction.UP
                    }
                }
            }
            MotionEvent.ACTION_UP -> registerSwipe()
        }
    }

    fun manuallyInputSwipe(d: Direction) {
        mLastSwipedDirection = d
        registerSwipe()
    }

    fun manuallyInputButton(b: Button) {
        mLastPressedButton = b
        registerPress()
    }

    override fun onSwipeSequenceAchieved(): Boolean = mSwipes == mKonamiCodeDirectionsOrder

    override fun validSwipeSequence(): Boolean {
        val index = mSwipes.size - 1
        val correctDirection = mKonamiCodeDirectionsOrder[index]
        val lastDirection = mSwipes[index]
        return correctDirection == lastDirection
    }

    override fun resetSwipeSequence() = mSwipes.clear()

    override fun onPressedSequenceAchieved(): Boolean {
        return mPressedButtons == mKonamiCodeButtonsOrder
    }

    override fun validPressedSequence(): Boolean {
        val index = mPressedButtons.size - 1
        val currentPressedButton = mPressedButtons[index]
        val correctPressedButton = mKonamiCodeButtonsOrder[index]
        return currentPressedButton == correctPressedButton
    }

    override fun resetPressedSequence() = mPressedButtons.clear()

    private fun showDialog() {
        if (mKonamiCodeButtonsOrder[0] != Button.NONE) {
            buttonDialog!!.show()
        }
    }

    private fun registerSwipe() {
        if (mLastSwipedDirection != Direction.NONE) {
            mSwipes.add(mLastSwipedDirection)
            if (!validSwipeSequence()) {
                resetSwipeSequence()
            } else {
                if (onSwipeSequenceAchieved()) {
                    if (mKonamiCodeButtonsOrder[0] == Button.NONE) {
                        triggerFinalCallback()
                    } else {
                        showDialog()
                    }
                    resetSwipeSequence()
                }
            }
        }
    }

    private fun registerPress() {
        if (mLastPressedButton != Button.NONE) {
            mPressedButtons.add(mLastPressedButton)
            if (!validPressedSequence()) {
                resetPressedSequence()
                buttonDialog!!.dismiss()
            } else {
                if (onPressedSequenceAchieved()) {
                    triggerFinalCallback()
                    resetPressedSequence()
                }
            }
        }
    }

    private fun triggerFinalCallback() {
        buttonDialog!!.dismiss()
        if (mCallback == null) {
            Toast.makeText(context, "Konami Code", Toast.LENGTH_LONG).show()
        } else {
            mCallback!!.onFinish()
            this.removeView(this)
            this.mCallback = null
            done = true
        }
    }
}
