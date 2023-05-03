package com.github.rooneyandshadows.lightbulb.pickers.inline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.*
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.*
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.collection.EasyRecyclerAdapterCollection
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.collection.ExtendedCollection
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.collection.ExtendedCollection.SelectableModes.SELECT_MULTIPLE
import java.util.*

@Suppress("UNCHECKED_CAST")
class SelectableFilterOptionAdapter<ItemType : EasyAdapterDataModel> :
    EasyRecyclerAdapter<ItemType>() {
    override val collection: ExtendedCollection<ItemType>
        get() = super.collection as ExtendedCollection<ItemType>

    override fun createCollection(): EasyRecyclerAdapterCollection<ItemType> {
        return object : ExtendedCollection<ItemType>(this@SelectableFilterOptionAdapter, SELECT_MULTIPLE) {
            @Override
            override fun filterItem(item: ItemType, filterQuery: String): Boolean {
                val locale = Locale.getDefault()
                val filterString = filterQuery.lowercase(locale)
                val itemName = item.itemName.lowercase(locale)
                return itemName.contains(filterString)
            }
        }
    }

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
        val item = collection.getFilteredItem(position) ?: return
        val vHolder: ChipVH = holder as SelectableFilterOptionAdapter<ItemType>.ChipVH
        vHolder.setItem(item)
    }

    fun hasItemWithName(name: String): Boolean {
        if (name.isBlank()) return false
        return collection.getItems().any { it.itemName == name }
    }

    inner class ChipVH internal constructor(itemBinding: RelativeLayout) : ViewHolder(itemBinding) {
        private val chipOptionTextView: TextView = itemBinding.findViewById(R.id.chipOptionTextView)

        fun setItem(item: ItemType) {
            itemView.apply {
                val isVisible = !collection.isItemSelected(item)
                if (!isVisible) {
                    visibility = View.GONE
                    layoutParams = ViewGroup.LayoutParams(0, 0)
                } else {
                    visibility = View.VISIBLE
                    layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                }
                chipOptionTextView.text = item.itemName
                chipOptionTextView.setOnClickListener {
                    collection.selectItem(item, true)
                }
            }
        }
    }
}