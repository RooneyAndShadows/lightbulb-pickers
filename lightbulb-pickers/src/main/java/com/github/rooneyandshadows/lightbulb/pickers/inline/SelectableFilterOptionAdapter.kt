package com.github.rooneyandshadows.lightbulb.pickers.inline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.*
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.*
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterSelectableModes.*
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerFilterableAdapter
import java.util.*
import java.util.function.Predicate

@Suppress("UNCHECKED_CAST")
class SelectableFilterOptionAdapter<ItemType : EasyAdapterDataModel> :
    EasyRecyclerFilterableAdapter<ItemType>(SELECT_MULTIPLE) {

    @Override
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ctx = parent.context
        val layoutId = R.layout.inline_chips_picker_option
        val layoutInflater = LayoutInflater.from(ctx)
        val layout = layoutInflater.inflate(layoutId, parent, false) as RelativeLayout
        return ChipVH(layout)
    }

    @Override
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getFilteredItems()[position]
        val vHolder: ChipVH = holder as SelectableFilterOptionAdapter<ItemType>.ChipVH
        vHolder.setItem(item)
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
    override fun filterItem(item: ItemType, filterQuery: String): Boolean {
        val locale = Locale.getDefault()
        val filterString = filterQuery.lowercase(locale)
        val itemName = item.itemName.lowercase(locale)
        return itemName.contains(filterString)
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