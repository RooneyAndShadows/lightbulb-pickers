package com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons

import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.IDemoIcon
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome.Icon.*

@Suppress("unused")
enum class DemoIconsUi(val value: Int, override val icon: IIcon) : IDemoIcon {
    ICON_MENU_ADAPTER_PICKER(1, faw_shapes),
    ICON_MENU_COLOR_PICKER(2, faw_palette),
    ICON_MENU_ICON_PICKER(3, faw_icons),
    ICON_MENU_MONTH_PICKER(4, faw_calendar_check1),
    ICON_MENU_DATE_PICKER(5, faw_calendar_day),
    ICON_MENU_DATE_RANGE_PICKER(6, faw_calendar_week),
    ICON_MENU_TIME_PICKER(7, faw_clock1),
    ICON_ADAPTER_PICKER_INDICATOR(8, faw_shapes),
    ICON_MONTH_PICKER_INDICATOR(9, faw_calendar_check1),
    ICON_DATE_PICKER_INDICATOR(10, faw_calendar_day),
    ICON_DATE_RANGE_PICKER_INDICATOR(11, faw_calendar_week),
    ICON_DATE_TIME_PICKER_INDICATOR(12, faw_clock1);
}