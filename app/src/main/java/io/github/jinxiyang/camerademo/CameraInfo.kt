package io.github.jinxiyang.camerademo

import android.hardware.camera2.CameraCharacteristics

class CameraInfo(val cameraId: String, val facing: Int, val characteristics: CameraCharacteristics) {
}