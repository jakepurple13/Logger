package com.programmersbox.loged

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

private class RVDynamicItem<T>(var data: T, private var itemRenderer: View.(T) -> Unit = {}) {
    internal fun renderItem(view: View) = view.itemRenderer(data!!)
    override fun toString(): String = data.toString()
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class DynamicAdapter(private var context: Context) : RecyclerView.Adapter<ViewHolder>() {
    private var list: MutableList<Pair<Int, RVDynamicItem<*>>> = mutableListOf()
    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = list[position].second.renderItem(holder.itemView)
    override fun getItemViewType(position: Int): Int = list[position].first
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(context).inflate(viewType, parent, false))

    fun addItem(@LayoutRes layoutRes: Int, block: View.(Unit) -> Unit) = addItem(layoutRes to RVDynamicItem(Unit, block))
    fun <T> T.addItem(@LayoutRes layoutRes: Int, block: View.(T) -> Unit) = addItem(layoutRes to RVDynamicItem(this, block))
    fun <T> Collection<T>.addItems(@LayoutRes layoutRes: Int, block: View.(T) -> Unit) = addItems(layoutRes, this, block)
    fun <T> Array<T>.addItems(@LayoutRes layoutRes: Int, block: View.(T) -> Unit) = addItems(layoutRes, this, block)

    operator fun get(index: Int) = list[index].second.data

    inline fun <reified T : Enum<T>> addEnums(@LayoutRes layoutRes: Int, noinline block: View.(T) -> Unit) = T::class.java.enumConstants?.map { it }?.addItems(layoutRes, block)
    fun <T> addItems(@LayoutRes layoutRes: Int, list: Collection<T>, block: View.(T) -> Unit) = addItems(list.map { layoutRes to RVDynamicItem(it, block) })
    fun <T> addItems(@LayoutRes layoutRes: Int, list: Array<T>, block: View.(T) -> Unit) = addItems(list.map { layoutRes to RVDynamicItem(it, block) })

    private fun <T> addItem(item: Pair<Int, RVDynamicItem<T>>): T {
        val position = list.size
        list.add(item)
        notifyItemInserted(position)
        return item.second.data
    }

    private fun <T> addItems(items: Collection<Pair<Int, RVDynamicItem<T>>>): List<T> {
        list.addAll(items)
        notifyDataSetChanged()
        return items.map { it.second.data }
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun <T> removeItem(item: T) {
        list.removeAll { item == it.second.data }
        notifyDataSetChanged()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> removeItem(predicate: (T) -> Boolean) {
        list.removeAll { (it.second.data as? T)?.let(predicate) ?: false }
        notifyDataSetChanged()
    }
}