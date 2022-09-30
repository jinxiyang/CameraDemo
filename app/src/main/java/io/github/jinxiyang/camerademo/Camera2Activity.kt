package io.github.jinxiyang.camerademo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.params.StreamConfigurationMap
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import io.github.jinxiyang.camerademo.common.*
import io.github.jinxiyang.requestpermission.PermissionRequester
import io.github.jinxiyang.requestpermission.utils.PermissionUtils
import java.util.*
import kotlin.math.max
import kotlin.math.min

class Camera2Activity : AppCompatActivity() {

    private lateinit var cameraView: CameraView

    private var requestCameraPermission = false

    private lateinit var mCameraManager: CameraManager

    private var mCameraInfo: CameraInfo? = null
    private var mCameraDevice: CameraDevice? = null
    private var mCaptureSession: CameraCaptureSession? = null

    private val mOpenCameraCallback: CameraDevice.StateCallback by lazy {
        getCameraDeviceStateCallback()
    }

    private val mOutputSizeMap: OutputSizeMap = OutputSizeMap()

//    private val mPreviewViewSize: Point = Point(0, 0)
//
//    private var mOutputSize: OutputSize? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)
        cameraView = findViewById(R.id.cameraView)


        openCameraIfHasPermission()
        cameraView.getPreviewView().setCallback(object : PreviewView.Callback {

            override fun onSurfaceChanged() {
                createCaptureSession()
            }

        })
    }

    private fun openCameraIfHasPermission() {
        if (PermissionUtils.hasPermissions(this, PermissionUtils.CAMERA_PERMISSIONS)) {
            openCamera()
        } else if (!requestCameraPermission) {
            requestCameraPermission = true
            PermissionRequester(this)
                .addPermissions(PermissionUtils.CAMERA_PERMISSIONS)
                .request {
                    if (it.granted()) {
                        openCamera()
                    } else {
                        Log.i(TAG, "没有相机权限")
                    }
                }
        }
    }

    private fun openCamera() {
        Log.i(TAG, "openCamera: ")
        mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraInfo = chooseCameraIdByFacing(CameraMetadata.LENS_FACING_BACK)
        if (cameraInfo == null) {
            Log.i(TAG, "没有找到合适的相机")
            return
        }
        mCameraInfo = cameraInfo
        openCameraInternal(cameraInfo)
    }

    private fun chooseCameraIdByFacing(targetFacing: Int): CameraInfo? {
        if (targetFacing != CameraMetadata.LENS_FACING_FRONT
            && targetFacing != CameraMetadata.LENS_FACING_BACK
            && targetFacing != CameraMetadata.LENS_FACING_EXTERNAL){
            return null
        }

        val cameraIdList = mCameraManager.cameraIdList
        if (cameraIdList.isEmpty()) {
            Log.i(TAG, "openCamera: 没有相机")
            return null
        }

        for (id in cameraIdList) {
            val characteristics = mCameraManager.getCameraCharacteristics(id)
            val level: Int? = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
            if (level == null || level == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                continue
            }
            val facing: Int = characteristics.get(CameraCharacteristics.LENS_FACING) ?: continue

            if (facing == targetFacing) {
                return CameraInfo(id, facing, characteristics)
            }
        }
        return null
    }


    @SuppressLint("MissingPermission")
    private fun openCameraInternal(cameraInfo: CameraInfo) {
        try {
            mCameraManager.openCamera(cameraInfo.cameraId, mOpenCameraCallback, null)
        } catch (e: Exception) {
        }
    }

    private fun getCameraDeviceStateCallback(): CameraDevice.StateCallback {
        return object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                Log.i(TAG, "onOpened: ")
                mCameraDevice = camera
                createCaptureSession()
            }

            override fun onDisconnected(camera: CameraDevice) {
                Log.i(TAG, "onDisconnected: ")
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.i(TAG, "onError: $error")
            }
        }
    }


    private fun isCameraOpened() = mCameraDevice != null

    private fun createCaptureSession() {
        if (!isCameraOpened() || !isPreviewViewReady()) {
            return
        }
        chooseOptimalSize()
        createCaptureSessionInternal()
    }

    private fun createCaptureSessionInternal() {
        if (mPreviewViewSize.y == 640) {
            val surfaceTexture = mTextureView.surfaceTexture!!
            val surface = Surface(surfaceTexture)
            val request = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            request.addTarget(surface)

            val captureSessionStateCallback = object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    session.setRepeatingRequest(request.build(), null, null)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {

                }

            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                val outputConfigurationList = mutableListOf<OutputConfiguration>()
//                outputConfigurationList.add(OutputConfiguration(surface))
//                val sessionConfiguration =  SessionConfiguration(SessionConfiguration.SESSION_REGULAR, outputConfigurationList, null, captureSessionStateCallback)
//                mCameraDevice?.createCaptureSession(sessionConfiguration)
//            } else {
                mCameraDevice?.createCaptureSession(Arrays.asList(surface), captureSessionStateCallback, null)
//            }
        }
    }

    private fun chooseOptimalSize() {
        val characteristics = mCameraInfo?.characteristics ?: return
        val map: StreamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: return

        mOutputSizeMap.clear()
        val sizes = map.getOutputSizes(SurfaceTexture::class.java)
        sizes.forEach {
            mOutputSizeMap.add(OutputSize(it.width, it.height))
        }

        val bigger = max(mPreviewViewSize.x, mPreviewViewSize.y)
        val smaller = min(mPreviewViewSize.x, mPreviewViewSize.y)

        val aspectRatio = AspectRatio.of(4, 3)
        val sortedSet = mOutputSizeMap.sizes(aspectRatio)

//        var bestSize: OutputSize? = null
//        sortedSet.forEach {
//            Log.i(TAG, "chooseOptimalSize: $it")
//            if (it.width >= bigger && it.height >= smaller) {
//                bestSize = it
//                return@forEach
//            }
//        }

//        val outputSize = bestSize ?: sortedSet.last()
        val outputSize = OutputSize(640, 480)

        Log.i(TAG, "chooseOptimalSize: bestSize --- $outputSize")

        if (!outputSize.equals(OutputSize(bigger, smaller))) {
            Log.i(TAG, "chooseOptimalSize:setDefaultBufferSize ")
            val layoutParams = mTextureView.layoutParams
            layoutParams.height = outputSize.width
            layoutParams.width = outputSize.height
            mTextureView.layoutParams = layoutParams
            mTextureView.surfaceTexture?.setDefaultBufferSize(outputSize.height, outputSize.width)
        }
        mOutputSize = outputSize
    }


    companion object {
        const val TAG = "camera-demo"
    }
}