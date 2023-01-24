package com.github.rooneyandshadows.lightbulb.pickersdemo.activity

import androidx.fragment.app.Fragment
import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity

class AppRouter(contextActivity: BaseActivity?, fragmentContainerId: Int) :
    BaseApplicationRouter(contextActivity, fragmentContainerId) {
    fun toAdapterPickerDemo(command: NavigationCommands?) {
        val screen: Screen = AdapterPickerScreen()
        navigate(command, screen)
    }

    fun toColorPickerDemo(command: BaseApplicationRouter.NavigationCommands?) {
        val screen: Screen = ColorPickerScreen()
        navigate(command, screen)
    }

    fun toIconPickerDemo(command: BaseApplicationRouter.NavigationCommands?) {
        val screen: Screen = IconPickerScreen()
        navigate(command, screen)
    }

    fun toMonthPickerDemo(command: BaseApplicationRouter.NavigationCommands?) {
        val screen: Screen = MonthPickerScreen()
        navigate(command, screen)
    }

    fun toDatePickerDemo(command: BaseApplicationRouter.NavigationCommands?) {
        val screen: Screen = DatePickerScreen()
        navigate(command, screen)
    }

    fun toDateRangePickerDemo(command: BaseApplicationRouter.NavigationCommands?) {
        val screen: Screen = DateRangePickerScreen()
        navigate(command, screen)
    }

    //SCREENS...
    class Screens {
        class AdapterPickerScreen : Screen() {
            val fragment: Fragment
                get() = FragmentAdapterPickerDemo.Companion.getNewInstance()
        }

        class ColorPickerScreen : Screen() {
            val fragment: Fragment
                get() = FragmentColorPickerDemo.Companion.getNewInstance()
        }

        class IconPickerScreen : Screen() {
            val fragment: Fragment
                get() = FragmentIconPickerDemo.Companion.getNewInstance()
        }

        class MonthPickerScreen : Screen() {
            val fragment: Fragment
                get() = FragmentMonthPickerDemo.Companion.getNewInstance()
        }

        class DatePickerScreen : Screen() {
            val fragment: Fragment
                get() = FragmentDatePickerDemo.Companion.getNewInstance()
        }

        class DateRangePickerScreen : Screen() {
            val fragment: Fragment
                get() = FragmentDateRangePickerDemo.Companion.getNewInstance()
        }
    }
}