package com.example.fooddataviewer_mvi.scan

import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.Context.CAMERA_SERVICE
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.fooddataviewer_mvi.R
import com.example.fooddataviewer_mvi.getViewModel
import com.google.android.gms.common.data.DataBufferObserver
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
import io.reactivex.internal.disposables.DisposableContainer
import io.reactivex.internal.disposables.EmptyDisposable
import kotlinx.android.synthetic.main.scan_fragment.*
import java.lang.IllegalStateException
import java.util.jar.Manifest

class ScanFragment: Fragment(R.layout.scan_fragment) {

    private val ORIENTATIONS = SparseIntArray()

    private lateinit var disposable: Disposable

    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 0)
        ORIENTATIONS.append(Surface.ROTATION_90, 90)
        ORIENTATIONS.append(Surface.ROTATION_180, 180)
        ORIENTATIONS.append(Surface.ROTATION_270, 270)
    }


    private lateinit var fotoapparat: Fotoapparat

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val frameProcessor = FrameProcessorOnSubscribe()
        fotoapparat = Fotoapparat(
            context = requireContext(),
            view = cameraView,
            cameraConfiguration =  CameraConfiguration(
                frameProcessor = frameProcessor,
                exposureCompensation = manualExposure(4),
                focusMode = continuousFocusPicture()
            )
        )

        val cameraId = findRearFacingCameraId()

        disposable = Observable.create(frameProcessor)
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
//                Log.d("Frame", frame.toString())
//                Log.d("FrameCopy", frame.toString())

            }
            .compose(getViewModel(ScanViewModel::class))
            .subscribe()
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
        var rotationCompensation = ORIENTATIONS.get(deviceRotation)

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