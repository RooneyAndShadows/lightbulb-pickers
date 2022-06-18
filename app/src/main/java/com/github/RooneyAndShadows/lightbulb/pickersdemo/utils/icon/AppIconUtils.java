package com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon;

import android.content.Context;

import com.github.rooneyandshadows.lightbulb.commons.utils.IconUtils;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerAdapter;
import com.github.rooneyandshadows.lightbulb.pickersdemo.R;
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons.DemoIcons;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;

import androidx.annotation.AttrRes;
import androidx.annotation.DimenRes;

public class AppIconUtils {

    public static IconicsDrawable getIconWithResolvedColor(Context context, IDemoIcon icon, int colorRef, @DimenRes int dimenId) {
        int sizeInPx = ResourceUtils.getDimenPxById(context, dimenId);
        return IconUtils.getIconWithResolvedColor(context, icon.getIcon(), colorRef, sizeInPx);
    }

    public static IconicsDrawable getIconWithResolvedColor(Context context, IDemoIcon icon, int colorRef) {
        int sizeInPx = ResourceUtils.getDimenPxById(context, R.dimen.ICON_SIZE_MEDIUM);
        return IconUtils.getIconWithResolvedColor(context, icon.getIcon(), colorRef, sizeInPx);
    }

    public static IconicsDrawable getIconWithAttributeColor(Context context, IDemoIcon icon, @AttrRes int colorRef, @DimenRes int dimenId) {
        int sizeInPx = ResourceUtils.getDimenPxById(context, dimenId);
        return IconUtils.getIconWithAttributeColor(context, icon.getIcon(), colorRef, sizeInPx);
    }

    public static IconicsDrawable getIconWithAttributeColor(Context context, IDemoIcon icon, @AttrRes int colorRef) {
        int sizeInPx = ResourceUtils.getDimenPxById(context, R.dimen.ICON_SIZE_MEDIUM);
        return IconUtils.getIconWithAttributeColor(context, icon.getIcon(), colorRef, sizeInPx);
    }

    public static IconicsDrawable getIconWithAttributeColor(Context context, IDemoIcon icon) {
        int sizeInPx = ResourceUtils.getDimenPxById(context, R.dimen.ICON_SIZE_MEDIUM);
        return IconUtils.getIconWithAttributeColor(context, icon.getIcon(), R.attr.colorPrimary, sizeInPx);
    }

    public static ArrayList<IconPickerAdapter.IconModel> getAllForPicker() {
        ArrayList<IconPickerAdapter.IconModel> result = new ArrayList<>();
        for (DemoIcons icon : DemoIcons.values())
            result.add(new IconPickerAdapter.IconModel(icon.getIcon().getName(), icon.getName(), IconPickerAdapter.IconSet.FONTAWESOME));
        return result;
    }
}
