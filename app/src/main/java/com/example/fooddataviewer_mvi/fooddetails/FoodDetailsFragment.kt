package com.example.fooddataviewer_mvi.fooddetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.fooddataviewer_mvi.R
import com.example.fooddataviewer_mvi.databinding.FoodDetailsFragmentBinding
import com.example.fooddataviewer_mvi.getViewModel
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.product_layout_small.*


class FoodDetailsFragment: Fragment(R.layout.food_details_fragment) {
    private val args: FoodDetailsFragmentArgs by navArgs()
    private lateinit var binding: FoodDetailsFragmentBinding
    private lateinit var disposable: Disposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FoodDetailsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        disposable = binding.actionButton.clicks().map { ActionButtonClicked }
            .compose(getViewModel(FoodDetailsViewModel::class).init(Initial(args.barcode)))
            .subscribe{ model ->
                binding.loadingIndicator.isVisible = model.activity
                binding.contentView.isVisible = model.product != null
                model.product?.let { product ->
                    binding.productNameView.text = product.name
                    binding.brandNameView.text = product.brands
                    binding.barcodeView.text = product.id
                    binding.energyValue.text = getString(
                        R.string.food_details_energy_value,
                        product.nutriments?.energy
                    )
                    binding.carbsValueView.text = getString(
                        R.string.food_details_macro_value,
                        product.nutriments?.carbohydrates
                    )
                    binding.fatValueView.text = getString(
                        R.string.food_details_macro_value,
                        product.nutriments?.fat
                    )
                    binding.proteinValue.text = getString(
                        R.string.food_details_macro_value,
                        product.nutriments?.proteins
                    )
                    binding.ingridientsText.text = getString(
                        R.string.food_details_ingridients,
                        product.ingredients
                    )
                    binding.actionButton.text = if(product.saved) {
                        getString(R.string.food_details_delete)
                    } else {
                        getString(R.string.food_details_save)
                    }

                    Glide.with(requireContext())
                        .load(product.imageUrl)
                        .fitCenter()
                        .into(binding.productImageView)
                }
            }
    }

    override fun onDestroyView() {
        disposable.dispose()
        super.onDestroyView()
    }
}