package com.example.fooddataviewer_mvi.foodlist.widget

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddataviewer_mvi.R
import com.example.fooddataviewer_mvi.databinding.FoodListProductItemBinding
import com.example.fooddataviewer_mvi.model.Product
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.extensions.LayoutContainer

class FoodListAdapter : ListAdapter<Item, ItemViewHolder<Item>>(DiffUtilCallback()) {

    private val productClicksSubject = PublishSubject.create<Int>()
    val productClicks: Observable<String> = productClicksSubject
        .map { position -> (getItem(position) as FoodListProductItem).product.id }

    fun submitProductList(products: List<Product>) {
        val items = mutableListOf<Item>()
        for (product in products) {
            items.add(FoodListProductItem(product))
        }
        submitList(items)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<Item> {
//        val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.food_list_product_item -> FoodListProductViewHolder(
                FoodListProductItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                productClicksSubject
            ) as ItemViewHolder<Item>
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder<Item>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FoodListProductItem -> R.layout.food_list_product_item
        }
    }
}

private class DiffUtilCallback : DiffUtil.ItemCallback<Item>() {

    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem
}

sealed class Item

data class FoodListProductItem(val product: Product) : Item()

sealed class ItemViewHolder<T : Item>(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {
    open fun bind(item: T) {}
}

class FoodListProductViewHolder(
    val binding:FoodListProductItemBinding,
    val productClicksSubject: PublishSubject<Int>
) :
    ItemViewHolder<FoodListProductItem>(binding.root) {

    override fun bind(item: FoodListProductItem) {
        super.bind(item)
        val product = item.product
        val context = containerView.context
        with(binding.productViewDetail){
            productNameView.text = product.name
            brandNameView.text = product.brands
            energyValue.text = context.getString(
                R.string.food_details_energy_value,
                product.nutriments?.energy
            )
            carbsValueView.text = context.getString(
                R.string.food_details_macro_value,
                product.nutriments?.carbohydrates
            )
            fatValueView.text = context.getString(
                R.string.food_details_macro_value,
                product.nutriments?.fat
            )
            proteinValue.text = context.getString(
                R.string.food_details_macro_value,
                product.nutriments?.proteins
            )

            Glide.with(context)
                .load(product.imageUrl)
                .fitCenter()
                .into(productImageView)
        }
        binding.productView.clicks().map { adapterPosition }.subscribe(productClicksSubject)
    }
}
