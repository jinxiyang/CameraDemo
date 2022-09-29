package io.github.jinxiyang.camerademo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import io.github.jinxiyang.requestpermission.PermissionRequester
import io.github.jinxiyang.requestpermission.utils.PermissionUtils


class MainActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView


    private var initCamera = false
    private var requestCameraPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCamera2: Button = findViewById(R.id.btnCamera2)
        btnCamera2.setOnClickListener {
            navigateCamera2Page()
        }

    }


    private fun navigateCamera2Page(){
        startActivity(Intent(this, Camera2Activity::class.java))
    }

    override fun onResume() {
        super.onResume()
//        if (PermissionUtils.hasPermissions(this, PermissionUtils.CAMERA_PERMISSIONS)) {
//            initCamera(this, this, previewView)
//        } else if (!requestCameraPermission){
//            requestCameraPermission = true
//            PermissionRequester(this)
//                .addPermissions(PermissionUtils.CAMERA_PERMISSIONS)
//                .request {
//                    if (it.granted()) {
//                        initCamera(this, this, previewView)
//                    } else {
//                        Toast.makeText(this@MainActivity, "没有相机权限", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }
    }



    fun initCamera(context: Context, lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        if (initCamera) {
            return
        }
        initCamera = true

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
           val cameraProvider = cameraProviderFuture.get()


            val preview = Preview.Builder().build()

            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)

            preview.setSurfaceProvider(previewView.surfaceProvider)

            val controller = LifecycleCameraController(this)
            controller.bindToLifecycle(this)
            previewView.controller = controller
        }, ContextCompat.getMainExecutor(context))
    }
}