package com.example.fooddataviewer_mvi.foodlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fooddataviewer_mvi.R
import com.example.fooddataviewer_mvi.databinding.FoodListFragmentBinding
import com.example.fooddataviewer_mvi.getViewModel
import com.jakewharton.rxbinding3.view.clicks

class FoodListFragment: Fragment(R.layout.food_list_fragment) {

    private lateinit var binding: FoodListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FoodListFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.addButton.clicks().map { FoodListEvent.AddButtonClicked }
            .compose(getViewModel(FoodListViewModel::class))
            .subscribe()
    }
}