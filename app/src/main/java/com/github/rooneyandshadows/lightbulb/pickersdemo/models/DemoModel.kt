package com.github.rooneyandshadows.lightbulb.pickersdemo.models

import android.os.Parcel
import android.os.Parcelable.Creator
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.color.colors.DemoColors
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons.DemoIcons
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils.Companion.writeString
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import java.util.*

class DemoModel : EasyAdapterDataModel {
    val id: UUID
    override val itemName: String
    val icon: DemoIcons
    val iconBackgroundColor: DemoColors

    constructor(id: UUID, title: String, icon: DemoIcons, iconBackgroundColor: DemoColors) {
        this.id = id
        itemName = title
        this.icon = icon
        this.iconBackgroundColor = iconBackgroundColor
    }

    // Parcelling part
    constructor(`in`: Parcel?) {
        id = readUUID.readUUID(`in`)
        itemName = readString.readString(`in`)
        icon = DemoIcons.valueOf(readString.readString(`in`))
        iconBackgroundColor = DemoColors.valueOf(readString.readString(`in`))
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        writeString(parcel, iconBackgroundColor.getName())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Creator<DemoModel> = object : Creator<DemoModel?> {
            override fun createFromParcel(`in`: Parcel): DemoModel? {
                return DemoModel(`in`)
            }

            override fun newArray(size: Int): Array<DemoModel?> {
                return arrayOfNulls(size)
            }
        }

        fun generateDemoCollection(): List<DemoModel> {
            val models: MutableList<DemoModel> = ArrayList()
            for (i in 0..24) {
                val title = "Demo Model #" + (i + 1).toString()
                models.add(
                    DemoModel(
                        UUID.randomUUID(),
                        title,
                        DemoIcons.Companion.getRandom(),
                        DemoColors.Companion.getRandom()
                    )
                )
            }
            return models
        }
    }
}