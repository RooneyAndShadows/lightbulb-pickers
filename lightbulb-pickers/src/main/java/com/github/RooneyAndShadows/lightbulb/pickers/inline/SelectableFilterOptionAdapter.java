package com.github.rooneyandshadows.lightbulb.pickers.inline;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterConfiguration;
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel;
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterSelectableModes;
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter;
import com.github.rooneyandshadows.java.commons.string.StringUtils;
import com.github.rooneyandshadows.lightbulb.pickers.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("unchecked")
public class SelectableFilterOptionAdapter<ItemType extends EasyAdapterDataModel> extends EasyRecyclerAdapter<ItemType> implements Filterable {
    private ArrayList<ItemType> filteredItems = new ArrayList<>();

    public SelectableFilterOptionAdapter() {
        super(new EasyAdapterConfiguration<ItemType>().withSelectMode(EasyAdapterSelectableModes.SELECT_MULTIPLE));
    }

    @Override
    public Bundle saveAdapterState() {
        Bundle state = super.saveAdapterState();
        state.putParcelableArrayList("ADAPTER_FILTERED_ITEMS", filteredItems);
        return state;
    }

    @Override
    public void restoreAdapterState(Bundle savedState) {
        filteredItems = savedState.getParcelableArrayList("ADAPTER_FILTERED_ITEMS");
        super.restoreAdapterState(savedState);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chips_picker_option, parent, false);
        return new ChipVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemType item = filteredItems.get(position);
        ChipVH vHolder = (ChipVH) holder;
        vHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    @Override
    public void setCollection(List<ItemType> collection) {
        filteredItems = new ArrayList<>(collection);
        super.setCollection(collection);
    }

    @Override
    public void addItem(ItemType newItem) {
        if (getItems(item -> newItem.getItemName().equals(item.getItemName())).size() > 0)
            return;
        filteredItems.add(newItem);
        super.addItem(newItem);
        selectItem(newItem, true);
    }

    public boolean hasItemWithName(String name) {
        if (StringUtils.isNullOrEmptyString(name))
            return false;
        for (ItemType item : items) {
            if (item.getItemName().equals(name))
                return true;
        }
        return false;
    }

    @Override
    public String getItemName(ItemType item) {
        return item.getItemName();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (StringUtils.isNullOrEmptyString(charString)) {
                    filteredItems = items;
                } else {
                    List<ItemType> filteredList = new ArrayList<>();
                    for (ItemType row : items)
                        if (row.getItemName().toLowerCase(Locale.getDefault())
                                .contains(charString.toLowerCase(Locale.getDefault())))
                            filteredList.add(row);
                    filteredItems = new ArrayList<>(filteredList);
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredItems;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredItems = (ArrayList<ItemType>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ChipVH extends RecyclerView.ViewHolder {
        protected ItemType item;
        private final TextView chipOptionTextView;

        ChipVH(RelativeLayout itemBinding) {
            super(itemBinding);
            chipOptionTextView = itemBinding.findViewWithTag("chipOptionTextView");
        }

        public void setItem(ItemType item) {
            boolean isVisible = !isItemSelected(item);
            if (!isVisible) {
                itemView.setVisibility(View.GONE);
                itemView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            } else {
                itemView.setVisibility(View.VISIBLE);
                itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            this.item = item;
            chipOptionTextView.setText(item.getItemName());
            chipOptionTextView.setOnClickListener(v -> selectItem(item, true));
        }
    }
}