package com.example.fooddataviewer_mvi.scan

import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.Context.CAMERA_SERVICE
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.fooddataviewer_mvi.R
import com.example.fooddataviewer_mvi.databinding.ScanFragmentBinding
import com.example.fooddataviewer_mvi.getViewModel
import com.jakewharton.rxbinding3.view.clicks
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.preview.Frame
import io.fotoapparat.selector.continuousFocusPicture
import io.fotoapparat.selector.manualExposure
import io.fotoapparat.util.FrameProcessor
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.product_layout_small.*
import java.lang.IllegalStateException

/**
 * Use this website to scan barcode: https://www.barcodelookup.com/0041321006258
 */

class ScanFragment: Fragment(R.layout.scan_fragment) {

    private val orientations = SparseIntArray()

    private lateinit var binding: ScanFragmentBinding

    private lateinit var disposable: Disposable


    private lateinit var fotoapparat: Fotoapparat

    init {
        orientations.append(Surface.ROTATION_0, 90)
        orientations.append(Surface.ROTATION_90, 0)
        orientations.append(Surface.ROTATION_180, 270)
        orientations.append(Surface.ROTATION_270, 180)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ScanFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val frameProcessor = FrameProcessorOnSubscribe()
        fotoapparat = Fotoapparat(
            context = requireContext(),
            view = binding.cameraView,
            cameraConfiguration =  CameraConfiguration(
                frameProcessor = frameProcessor,
                exposureCompensation = manualExposure(4),
                focusMode = continuousFocusPicture()
            ),
        )

        val cameraId = findRearFacingCameraId()

        disposable = Observable.mergeArray(Observable.create(frameProcessor)
            .map { frame ->
                Captured(
                    frame.copy(
                        rotation = getRotationCompensation(
                            cameraId,
                            this.activity as Activity,
                            isFrontFacing = false
                        )
                    )
                )

            },
            binding.productView.productViewRoot.clicks().map { ProductInfoClicked }
        )
            .compose(getViewModel(ScanViewModel::class))
            .subscribe{ model ->
                binding.loadingIndicator.isVisible = model.activity
                binding.productView.productViewRoot.isVisible = model.processBarcodeResult is ProcessBarcodeResult.BarcodeLoaded
                binding.errorView.isVisible = model.processBarcodeResult is ProcessBarcodeResult.Error

                if (model.processBarcodeResult is ProcessBarcodeResult.BarcodeLoaded) {
                    productNameView.text = model.processBarcodeResult.product.name
                    brandNameView.text = model.processBarcodeResult.product.brands
                    energyValue.text = getString(
                        R.string.scan_energy_value,
                        model.processBarcodeResult.product.nutriments?.energy
                    )
                    carbsValueView.text = getString(
                        R.string.scan_macro_value,
                        model.processBarcodeResult.product.nutriments?.carbohydrates
                    )
                    fatValueView.text = getString(
                        R.string.scan_macro_value,
                        model.processBarcodeResult.product.nutriments?.fat
                    )
                    proteinValue.text = getString(
                        R.string.scan_macro_value,
                        model.processBarcodeResult.product.nutriments?.proteins
                    )

                    Glide.with(requireContext())
                        .load(model.processBarcodeResult.product.imageUrl)
                        .fitCenter()
                        .into(productImageView)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        handleCameraPermission(false)
    }

    override fun onStop() {
        fotoapparat.stop()
        super.onStop()
    }

    override fun onDestroyView() {
        disposable.dispose()
        super.onDestroyView()
    }

    private fun handleCameraPermission(permissionResult: Boolean) {
        if(hasCameraPermission()) {
            fotoapparat.start()
        } else if (!permissionResult || shouldShowRequestPermissionRationale(CAMERA)) {
            requestPermissions(arrayOf(CAMERA), 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        handleCameraPermission(true)
    }

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        requireContext(),
        CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun findRearFacingCameraId(): String {
        val cameraManager = activity?.getSystemService(CAMERA_SERVICE) as CameraManager
        val cameraIds = cameraManager.cameraIdList
        cameraIds.forEach { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            val orientation = characteristics?.get(CameraCharacteristics.LENS_FACING)
            if(orientation == CameraCharacteristics.LENS_FACING_BACK) return id
        }
        throw IllegalStateException("Unable to find camera id")
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(CameraAccessException::class)
    private fun getRotationCompensation(cameraId: String, activity: Activity, isFrontFacing: Boolean): Int {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        val deviceRotation = activity.windowManager.defaultDisplay.rotation
        var rotationCompensation = orientations.get(deviceRotation)

        // Get the device's sensor orientation.
        val cameraManager = activity.getSystemService(CAMERA_SERVICE) as CameraManager
        val sensorOrientation = cameraManager
            .getCameraCharacteristics(cameraId)
            .get(CameraCharacteristics.SENSOR_ORIENTATION)!!

        if (isFrontFacing) {
            rotationCompensation = (sensorOrientation + rotationCompensation) % 360
        } else { // back-facing
            rotationCompensation = (sensorOrientation - rotationCompensation + 360) % 360
        }
        return rotationCompensation
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Throws(CameraAccessException::class)
//    private fun getRotationCompensation(cameraId: String, activity: Activity, context: Context): Int {
//        // Get the device's current rotation relative to its "native" orientation.
//        // Then, from the ORIENTATIONS table, look up the angle the image must be
//        // rotated to compensate for the device's rotation.
//        val deviceRotation = activity.windowManager.defaultDisplay.rotation
//        var rotationCompensation = orientations.get(deviceRotation)
//
//        // On most devices, the sensor orientation is 90 degrees, but for some
//        // devices it is 270 degrees. For devices with a sensor orientation of
//        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
//        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
//        val sensorOrientation = cameraManager
//            .getCameraCharacteristics(cameraId)
//            .get(CameraCharacteristics.SENSOR_ORIENTATION)!!
//        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360
//
//        val result: Int
////        when (rotationCompensation) {
////            0 -> result = ROTATION_0
////            90 -> result = ROTATION_90
////            180 -> result = ROTATION_180
////            270 -> result = ROTATION_270
////            else -> {
////                result = ROTATION_0
////                Log.e("OrderCheckInFragment", "Bad rotation value: $rotationCompensation")
////            }
////        }
//
////        return result
//        return rotationCompensation
//    }

}

private class FrameProcessorOnSubscribe: ObservableOnSubscribe<Frame>, FrameProcessor {

    private var emitter: ObservableEmitter<Frame>? = null

    override fun subscribe(emitter: ObservableEmitter<Frame>) {
        emitter.setCancellable { this.emitter = null }
        this.emitter = emitter
    }

    override fun invoke(frame: Frame) {
        emitter?.onNext(frame)
    }
}