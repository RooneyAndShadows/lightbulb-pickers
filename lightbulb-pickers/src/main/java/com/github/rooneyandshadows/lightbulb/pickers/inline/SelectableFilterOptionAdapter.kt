package com.github.rooneyandshadows.lightbulb.pickers.inline

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.*
import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.*
import com.github.rooneyandshadows.lightbulb.commons.utils.BundleUtils
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterSelectableModes.*
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import java.util.*
import java.util.function.Predicate

@Suppress("UNCHECKED_CAST")
class SelectableFilterOptionAdapter<ItemType : EasyAdapterDataModel> : EasyRecyclerAdapter<ItemType>(SELECT_MULTIPLE),
    Filterable {
    private var filteredItems: MutableList<ItemType> = mutableListOf()

    companion object {
        private const val ADAPTER_FILETERED_ITEMS_KEY = "ADAPTER_FILETERED_ITEMS_KEY"
    }

    @Override
    override fun onSaveInstanceState(outState: Bundle): Bundle {
        val state: Bundle = super.onSaveInstanceState(outState)
        outState.apply {
            BundleUtils.putParcelableArrayList(ADAPTER_FILETERED_ITEMS_KEY, this, filteredItems as ArrayList<ItemType>)
        }
        return state
    }

    @Override
    override fun onRestoreInstanceState(savedState: Bundle) {
        savedState.apply {
            BundleUtils.apply {
                val obj = Object()
                val clz = obj::class.java as Class<ItemType>
                filteredItems = getParcelableArrayList(ADAPTER_FILETERED_ITEMS_KEY, savedState, clz) as MutableList<ItemType>
            }
        }
        super.onRestoreInstanceState(savedState)
    }

    @Override
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ctx = parent.context
        val layoutId = R.layout.chips_picker_option
        val layoutInflater = LayoutInflater.from(ctx)
        val layout = layoutInflater.inflate(layoutId, parent, false) as RelativeLayout
        return ChipVH(layout)
    }

    @Override
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredItems[position]
        val vHolder: ChipVH = holder as SelectableFilterOptionAdapter<ItemType>.ChipVH
        vHolder.setItem(item)
    }

    @Override
    override fun getItemCount(): Int {
        return filteredItems.size
    }

    @Override
    override fun setCollection(collection: List<ItemType>) {
        filteredItems = ArrayList(collection)
        super.setCollection(collection)
    }

    @Override
    override fun addItem(item: ItemType) {
        if (hasItemWithName(item.itemName)) return
        filteredItems.add(item)
        super.addItem(item)
        selectItem(item, true)
    }

    fun hasItemWithName(name: String): Boolean {
        if (name.isBlank()) return false
        return getItems(Predicate { item -> return@Predicate item.itemName == name })
            .isNotEmpty()
    }

    @Override
    override fun getItemName(item: ItemType): String {
        return item.itemName
    }

    @Override
    override fun getFilter(): Filter {
        return object : Filter() {
            @Override
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val locale = Locale.getDefault()
                val filterString = charSequence.toString().lowercase(locale)
                val itemsInAdapter = getItems()
                val result: MutableList<ItemType> = mutableListOf()
                if (filterString.isBlank()) result.addAll(itemsInAdapter)
                else {
                    itemsInAdapter.forEach { item ->
                        val itemName = item.itemName.lowercase(locale)
                        if (itemName.contains(filterString)) result.add(item)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = result
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredItems = filterResults.values as MutableList<ItemType>
                notifyDataSetChanged()
            }
        }
    }

    inner class ChipVH internal constructor(itemBinding: RelativeLayout) : ViewHolder(itemBinding) {
        private val chipOptionTextView: TextView = itemBinding.findViewById(R.id.chipOptionTextView)

        fun setItem(item: ItemType) {
            itemView.apply {
                val isVisible = !isItemSelected(item)
                if (!isVisible) {
                    visibility = View.GONE
                    layoutParams = ViewGroup.LayoutParams(0, 0)
                } else {
                    visibility = View.VISIBLE
                    layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                }
                chipOptionTextView.text = item.itemName
                chipOptionTextView.setOnClickListener {
                    selectItem(item, true)
                }
            }
        }
    }
}