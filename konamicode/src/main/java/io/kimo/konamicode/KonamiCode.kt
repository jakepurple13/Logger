package io.kimo.konamicode

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

class KonamiCode private constructor(installer: Installer) {

    val context: Context = installer.context
    val rootView: ViewGroup = installer.rootView
    val callback: KonamiCodeLayout.Callback = installer.callback
    val konamiCodeLayout: KonamiCodeLayout = installer.layout

    /**
     * Konami Code's builder
     * @param context
     */
    class Installer(var context: Context) {
        lateinit var rootView: ViewGroup
        lateinit var callback: KonamiCodeLayout.Callback
        lateinit var layout: KonamiCodeLayout

        /**
         * on - installs into an activity
         * @param activity
         */
        fun on(activity: Activity): Installer {
            rootView = activity.findViewById<View>(android.R.id.content) as ViewGroup
            return this
        }

        /**
         * into - installs into a fragment
         * @param fragment
         */
        fun on(fragment: Fragment): Installer {
            rootView = fragment.view!!.rootView as ViewGroup
            return this
        }

        /**
         * into - installs into a view
         * @param view
         */
        fun on(view: View): Installer {
            rootView = view.rootView as ViewGroup
            return this
        }

        /**
         * withCallback - interface executed after the whole code is executed
         * @param callback
         */
        fun callback(callback: KonamiCodeLayout.Callback): Installer {
            this.callback = callback
            return this
        }

        /**
         * withCallback - interface executed after the whole code is executed
         * @param callback
         */
        fun callback(callback: () -> Unit): Installer {
            this.callback = object : KonamiCodeLayout.Callback {
                override fun onFinish() {
                    callback()
                }
            }
            return this
        }

        @SuppressLint("ClickableViewAccessibility")
        private fun setUp(): Installer {
            val currentView = rootView.getChildAt(0)
            rootView.removeView(currentView)

            //match parent params
            val layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            val gestureDelegate = FrameLayout(context)
            gestureDelegate.addView(currentView, layoutParams)

            //necessary view that passes all touch events up to the parent viewgroup
            gestureDelegate.setOnTouchListener { _, _ -> true }

            layout = KonamiCodeLayout(context)
            layout.addView(gestureDelegate)

            rootView.addView(layout, layoutParams)

            layout.setCallback(callback)

            return this
        }

        /**
         * install - installs all Konami Code components into the target
         */
        fun install(): KonamiCode = KonamiCode(setUp())

        /**
         * install - installs all Konami Code components into the target
         */
        fun install(sequence: KonamiSequence): KonamiCode = install {
            +sequence.sequence
            +sequence.buttons
        }

        /**
         * install - installs all Konami Code components into the target
         */
        fun install(block: KonamiSequence.() -> Unit): KonamiCode = KonamiCode(setUp().apply {
            val konamiSequence = KonamiSequence().apply(block)
            layout.setSequence(*konamiSequence.sequence.toTypedArray())
            layout.setButtonOrder(*konamiSequence.buttons.toTypedArray())
        })

    }

    class KonamiSequence {
        internal val sequence = mutableListOf<KonamiCodeLayout.Direction>()
        internal val buttons = mutableListOf<KonamiCodeLayout.Button>()
        operator fun KonamiCodeLayout.Direction.unaryPlus() = sequence.add(this)
        operator fun KonamiCodeLayout.Button.unaryPlus() = buttons.add(this)
        fun KonamiCodeLayout.Direction.add() = sequence.add(this)
        fun KonamiCodeLayout.Button.add() = buttons.add(this)
        operator fun Collection<KonamiCodeLayout.Direction>.unaryPlus() = sequence.addAll(this)
        operator fun List<KonamiCodeLayout.Button>.unaryPlus() = buttons.addAll(this)

        companion object {
            val defaultSequence = KonamiSequence().apply {
                +listOf(
                    KonamiCodeLayout.Direction.UP, KonamiCodeLayout.Direction.UP,
                    KonamiCodeLayout.Direction.DOWN, KonamiCodeLayout.Direction.DOWN,
                    KonamiCodeLayout.Direction.LEFT, KonamiCodeLayout.Direction.RIGHT,
                    KonamiCodeLayout.Direction.LEFT, KonamiCodeLayout.Direction.RIGHT
                )
                +listOf(KonamiCodeLayout.Button.B, KonamiCodeLayout.Button.A, KonamiCodeLayout.Button.START)
            }

            fun builder(block: KonamiSequence.() -> Unit) = KonamiSequence().apply(block)

            fun defaultDirections() = listOf(
                KonamiCodeLayout.Direction.UP, KonamiCodeLayout.Direction.UP,
                KonamiCodeLayout.Direction.DOWN, KonamiCodeLayout.Direction.DOWN,
                KonamiCodeLayout.Direction.LEFT, KonamiCodeLayout.Direction.RIGHT,
                KonamiCodeLayout.Direction.LEFT, KonamiCodeLayout.Direction.RIGHT
            )

            fun defaultButtons() = listOf(KonamiCodeLayout.Button.B, KonamiCodeLayout.Button.A, KonamiCodeLayout.Button.START)
        }
    }
}
