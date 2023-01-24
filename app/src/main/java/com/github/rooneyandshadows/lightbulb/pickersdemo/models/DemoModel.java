package com.github.rooneyandshadows.lightbulb.pickersdemo.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelableUtils;
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.color.colors.DemoColors;
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons.DemoIcons;
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DemoModel extends EasyAdapterDataModel {
    private final UUID id;
    private final String title;
    private final DemoIcons icon;
    private final DemoColors iconBackgroundColor;

    public DemoModel(UUID id, String title, DemoIcons icon, DemoColors iconBackgroundColor) {
        super(false);
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.iconBackgroundColor = iconBackgroundColor;
    }

    // Parcelling part
    public DemoModel(Parcel in) {
        super(in);
        this.id = ParcelableUtils.readUUID(in);
        this.title = ParcelableUtils.readString(in);
        this.icon = DemoIcons.valueOf(ParcelableUtils.readString(in));
        this.iconBackgroundColor = DemoColors.valueOf(ParcelableUtils.readString(in));
    }

    public static final Parcelable.Creator<DemoModel> CREATOR = new Parcelable.Creator<DemoModel>() {
        public DemoModel createFromParcel(Parcel in) {
            return new DemoModel(in);
        }

        public DemoModel[] newArray(int size) {
            return new DemoModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        ParcelableUtils
                .writeUUID(parcel, id)
                .writeString(parcel, title)
                .writeString(parcel, icon.getName())
                .writeString(parcel, iconBackgroundColor.getName());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String getItemName() {
        return title;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public DemoIcons getIcon() {
        return icon;
    }

    public DemoColors getIconBackgroundColor() {
        return iconBackgroundColor;
    }

    public static List<DemoModel> generateDemoCollection() {
        List<DemoModel> models = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            String title = "Demo Model #".concat(String.valueOf(i + 1));
            models.add(new DemoModel(UUID.randomUUID(), title, DemoIcons.getRandom(), DemoColors.getRandom()));
        }
        return models;
    }
}