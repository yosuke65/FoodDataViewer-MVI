package com.example.fooddataviewer_mvi.foodlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fooddataviewer_mvi.R
import com.example.fooddataviewer_mvi.databinding.FoodListFragmentBinding
import com.example.fooddataviewer_mvi.foodlist.widget.FoodListAdapter
import com.example.fooddataviewer_mvi.getViewModel
import com.example.fooddataviewer_mvi.model.Product
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.internal.observers.BasicIntQueueDisposable

class FoodListFragment: Fragment(R.layout.food_list_fragment) {

    private lateinit var binding: FoodListFragmentBinding
    private lateinit var disposable: Disposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FoodListFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("CheckResult")
    override fun onStart() {
        super.onStart()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = FoodListAdapter()
        binding.recyclerView.adapter = adapter
       disposable = Observable
           .mergeArray(
               binding.addButton.clicks().map { AddButtonClicked },
               adapter.productClicks.map {ProductClicked(it)}
           )
            .compose(getViewModel(FoodListViewModel::class).init(Initial))
            .subscribe { model ->
                adapter.submitProductList(model.products)
            }
    }
}