package com.github.rooneyandshadows.lightbulb.pickersdemo.activity;

import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity;
import com.github.rooneyandshadows.lightbulb.application.activity.routing.BaseApplicationRouter;
import com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.adapter_picker.FragmentAdapterPickerDemo;
import com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.color_picker.FragmentColorPickerDemo;
import com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.date_picker.FragmentDatePickerDemo;
import com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.icon_picker.FragmentIconPickerDemo;
import com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.month_picker.FragmentMonthPickerDemo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class AppRouter extends BaseApplicationRouter {

    public AppRouter(BaseActivity contextActivity, int fragmentContainerId) {
        super(contextActivity, fragmentContainerId);
    }

    public void toAdapterPickerDemo(NavigationCommands command) {
        Screen screen = new Screens.AdapterPickerScreen();
        navigate(command, screen);
    }

    public void toColorPickerDemo(NavigationCommands command) {
        Screen screen = new Screens.ColorPickerScreen();
        navigate(command, screen);
    }

    public void toIconPickerDemo(NavigationCommands command) {
        Screen screen = new Screens.IconPickerScreen();
        navigate(command, screen);
    }

    public void toMonthPickerDemo(NavigationCommands command) {
        Screen screen = new Screens.MonthPickerScreen();
        navigate(command, screen);
    }

    public void toDatePickerDemo(NavigationCommands command) {
        Screen screen = new Screens.DatePickerScreen();
        navigate(command, screen);
    }


    //SCREENS...
    public static final class Screens {

        public static final class AdapterPickerScreen extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return FragmentAdapterPickerDemo.getNewInstance();
            }
        }

        public static final class ColorPickerScreen extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return FragmentColorPickerDemo.getNewInstance();
            }
        }

        public static final class IconPickerScreen extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return FragmentIconPickerDemo.getNewInstance();
            }
        }

        public static final class MonthPickerScreen extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return FragmentMonthPickerDemo.getNewInstance();
            }
        }

        public static final class DatePickerScreen extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return FragmentDatePickerDemo.getNewInstance();
            }
        }
    }
}