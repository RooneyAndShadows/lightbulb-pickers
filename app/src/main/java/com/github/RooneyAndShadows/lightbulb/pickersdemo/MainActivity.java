package com.github.rooneyandshadows.lightbulb.pickersdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate;
import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogFragment;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_datetime.DateTimePickerDialogBuilder;
import com.github.rooneyandshadows.lightbulb.pickers.R;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public class MainActivity extends AppCompatActivity {

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String datestr = "2022-03-10 19:30:19+0300";
        OffsetDateTime date = DateUtilsOffsetDate.getDateFromString(DateUtilsOffsetDate.defaultFormatWithTimeZone, datestr);
        new DateTimePickerDialogBuilder(getSupportFragmentManager(), "saasfasf")
                .withSelection(date)
                .withCancelOnClickOutsude(true)
                .withPositiveButton(new BaseDialogFragment.DialogButtonConfiguration("test"), (view, dialogFragment) -> {
                })
                .withNegativeButton(new BaseDialogFragment.DialogButtonConfiguration("test"), (view, dialogFragment) -> {
                })
                .withOnCancelListener(dialogFragment -> {
                })
                .withOnDateSelectedEvent((oldValue, newValue) -> {
                    System.out.println(DateUtilsOffsetDate.getDateString(DateUtilsOffsetDate.defaultFormatWithTimeZone, newValue));
                })
                .buildDialog().show();
    }*/
}