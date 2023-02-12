package com.github.rooneyandshadows.lightbulb.pickersdemo

import android.content.Context
import android.graphics.drawable.Drawable
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.drawable.ShowMenuDrawable
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.ChipsPickerAdapter
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.ChipsPickerAdapter.*


fun getShowMenuDrawable(context: Context): Drawable {
    return ShowMenuDrawable(context).apply {
        setEnabled(false)
    }
}

fun getMenuBackDrawable(context: Context): Drawable {
    return ShowMenuDrawable(context).apply {
        setEnabled(false)
        progress = 1F
    }
}


fun generateChips(): List<ChipModel> {
    val models: MutableList<ChipModel> = mutableListOf()
    models.add(ChipModel("Star"))
    models.add(ChipModel("Tag"))
    models.add(ChipModel("Search"))
    models.add(ChipModel("Block"))
    models.add(ChipModel("Center"))
    models.add(ChipModel("Right"))
    models.add(ChipModel("Cat"))
    models.add(ChipModel("Tree"))
    models.add(ChipModel("Person"))
    models.add(ChipModel("Generation"))
    models.add(ChipModel("Utility"))
    models.add(ChipModel("Category"))
    models.add(ChipModel("Label"))
    models.add(ChipModel("Side"))
    models.add(ChipModel("Section"))
    models.add(ChipModel("Page"))
    models.add(ChipModel("Class"))
    models.add(ChipModel("Type"))
    models.add(ChipModel("Performance"))
    models.add(ChipModel("Object"))
    models.add(ChipModel("Count"))
    models.add(ChipModel("Letter"))
    models.add(ChipModel("Subtitle"))
    models.add(ChipModel("Height"))
    models.add(ChipModel("Strenght"))
    return models
}

