package com.github.rooneyandshadows.lightbulb.pickersdemo

import android.content.Context
import android.graphics.drawable.Drawable
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.drawable.ShowMenuDrawable


fun getShowMenuDrawable(context: Context): Drawable {
    return ShowMenuDrawable(context).apply {
        setEnabled(false)
        progress = 1F
    }
}

