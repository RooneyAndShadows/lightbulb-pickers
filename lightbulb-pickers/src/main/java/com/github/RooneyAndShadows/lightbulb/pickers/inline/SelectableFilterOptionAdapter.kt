package com.github.rooneyandshadows.lightbulb.pickers.inline

import android.view.View
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.java.commons.string.StringUtils
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterConfiguration
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterSelectableModes
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import java.util.*

class SelectableFilterOptionAdapter<ItemType : EasyAdapterDataModel?> :
    EasyRecyclerAdapter<ItemType>(EasyAdapterConfiguration<ItemType>().withSelectMode(
        EasyAdapterSelectableModes.SELECT_MULTIPLE)), Filterable {
    private var filteredItems = ArrayList<ItemType>()
    override fun saveAdapterState(): Bundle {
        val state: Bundle = super.saveAdapterState()
        state.putParcelableArrayList("ADAPTER_FILTERED_ITEMS", filteredItems)
        return state
    }

    override fun restoreAdapterState(savedState: Bundle) {
        filteredItems = savedState.getParcelableArrayList<ItemType>("ADAPTER_FILTERED_ITEMS")
        super.restoreAdapterState(savedState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: RelativeLayout =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.chips_picker_option, parent, false) as RelativeLayout
        return ChipVH(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = filteredItems[position]
        val vHolder: ChipVH = holder as ChipVH
        vHolder.setItem(item)
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    override fun setCollection(collection: List<ItemType>) {
        filteredItems = ArrayList(collection)
        super.setCollection(collection)
    }

    override fun addItem(newItem: ItemType) {
        if (getItems { item: ItemType -> newItem!!.itemName == item!!.itemName }.size > 0) return
        filteredItems.add(newItem)
        super.addItem(newItem)
        selectItem(newItem, true)
    }

    fun hasItemWithName(name: String): Boolean {
        if (StringUtils.isNullOrEmptyString(name)) return false
        for (item in items) {
            if (item.itemName == name) return true
        }
        return false
    }

    override fun getItemName(item: ItemType): String? {
        return item!!.itemName
    }

    val filter: Filter
        get() = object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (StringUtils.isNullOrEmptyString(charString)) {
                    filteredItems = items
                } else {
                    val filteredList: MutableList<ItemType> = ArrayList()
                    for (row in items) if (row.itemName.lowercase(Locale.getDefault())
                            .contains(charString.lowercase(Locale.getDefault()))
                    ) filteredList.add(row)
                    filteredItems = ArrayList(filteredList)
                }
                val filterResults = FilterResults()
                filterResults.values = filteredItems
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredItems = filterResults.values as ArrayList<ItemType>
                notifyDataSetChanged()
            }
        }

    inner class ChipVH internal constructor(itemBinding: RelativeLayout) : RecyclerView.ViewHolder(itemBinding) {
        protected var item: ItemType? = null
        private val chipOptionTextView: TextView

        init {
            chipOptionTextView = itemBinding.findViewWithTag<TextView>("chipOptionTextView")
        }

        fun setItem(item: ItemType) {
            val isVisible = !isItemSelected(item)
            if (!isVisible) {
                itemView.visibility = View.GONE
                itemView.layoutParams = ViewGroup.LayoutParams(0, 0)
            } else {
                itemView.visibility = View.VISIBLE
                itemView.layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            this.item = item
            chipOptionTextView.setText(item!!.itemName)
            chipOptionTextView.setOnClickListener(View.OnClickListener { v: View? -> selectItem(item, true) })
        }
    }
}