package com.programmersbox.loged

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.FloatRange
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

fun Activity.setBrightness(@FloatRange(from = 0.0, to = 1.0) brightness: Float) {
    val params = window.attributes
    params.screenBrightness = brightness // range from 0 - 1 as per docs
    window.attributes = params
    window.addFlags(WindowManager.LayoutParams.FLAGS_CHANGED)
}

@FloatRange(from = 0.0, to = 1.0)
fun Activity.getBrightness(): Float = window.attributes.screenBrightness

fun <T> recyclerDeleteItemWithUndo(adapterList: MutableList<T>, adapter: RecyclerView.Adapter<*>, positionForDeletion: Int, rootLayout: View) {
    val deleted = adapterList.removeAt(positionForDeletion)
    adapter.notifyItemRemoved(positionForDeletion)
    adapter.notifyItemRangeChanged(positionForDeletion, adapter.itemCount)
    Snackbar.make(rootLayout, "Item removed!", Snackbar.LENGTH_LONG)
        .setAction("Undo") {
            adapterList.add(positionForDeletion, deleted)
            adapter.notifyItemInserted(positionForDeletion)
        }.show()
}

var TextView.startDrawable: Drawable?
    get() = compoundDrawables[0]
    set(value) {
        setCompoundDrawablesWithIntrinsicBounds(value, topDrawable, endDrawable, bottomDrawable)
    }
var TextView.endDrawable: Drawable?
    get() = compoundDrawables[2]
    set(value) {
        setCompoundDrawablesWithIntrinsicBounds(startDrawable, topDrawable, value, bottomDrawable)
    }
var TextView.topDrawable: Drawable?
    get() = compoundDrawables[1]
    set(value) {
        setCompoundDrawablesWithIntrinsicBounds(startDrawable, value, endDrawable, bottomDrawable)
    }
var TextView.bottomDrawable: Drawable?
    get() = compoundDrawables[3]
    set(value) {
        setCompoundDrawablesWithIntrinsicBounds(startDrawable, topDrawable, endDrawable, value)
    }
