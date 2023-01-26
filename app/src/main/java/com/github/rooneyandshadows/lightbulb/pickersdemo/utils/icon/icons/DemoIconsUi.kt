package com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons

import com.mikepenz.iconics.typeface.IIcon
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.IDemoIcon
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import java.util.HashMap

@Suppress("unused")
enum class DemoIconsUi(val value: Int, override val icon: IIcon) : IDemoIcon {
    ICON_MENU_ADAPTER_PICKER(1, FontAwesome.Icon.faw_shapes),
    ICON_MENU_COLOR_PICKER(2, FontAwesome.Icon.faw_palette),
    ICON_MENU_ICON_PICKER(3, FontAwesome.Icon.faw_icons),
    ICON_MENU_MONTH_PICKER(4, FontAwesome.Icon.faw_calendar_check1),
    ICON_MENU_DATE_PICKER(5, FontAwesome.Icon.faw_calendar_day),
    ICON_MENU_DATE_RANGE_PICKER(6, FontAwesome.Icon.faw_calendar_week),
    ICON_ADAPTER_PICKER_INDICATOR(7, FontAwesome.Icon.faw_shapes),
    ICON_MONTH_PICKER_INDICATOR(8, FontAwesome.Icon.faw_calendar_check1),
    ICON_DATE_PICKER_INDICATOR(9, FontAwesome.Icon.faw_calendar_day),
    ICON_DATE_RANGE_PICKER_INDICATOR(9, FontAwesome.Icon.faw_calendar_day);
}