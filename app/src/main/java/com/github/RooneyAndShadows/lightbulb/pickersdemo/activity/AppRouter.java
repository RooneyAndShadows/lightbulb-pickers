package com.github.rooneyandshadows.lightbulb.pickersdemo.activity;

import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity;
import com.github.rooneyandshadows.lightbulb.application.activity.routing.BaseApplicationRouter;
import com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.AdapterPickerDemoFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class AppRouter extends BaseApplicationRouter {

    public AppRouter(BaseActivity contextActivity, int fragmentContainerId) {
        super(contextActivity, fragmentContainerId);
    }

    public void toDemoRegular(NavigationCommands command) {
        Screen screen = new Screens.AdapterPickerScreen();
        navigate(command, screen);
    }

    //SCREENS...
    public static final class Screens {

        public static final class AdapterPickerScreen extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return AdapterPickerDemoFragment.getNewInstance();
            }
        }
    }
}