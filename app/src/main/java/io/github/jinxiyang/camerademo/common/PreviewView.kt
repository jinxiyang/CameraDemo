package io.github.jinxiyang.camerademo.common

import android.util.Size
import android.view.Surface

public interface PreviewView {

    fun isReady(): Boolean

    fun setOutputSize(width: Int, height: Int)

    fun getOutputClass(): Class<*>

    fun getSurface(): Surface

    fun setCallback(callback: Callback)

    fun getSize(): Size

    interface Callback {
        fun onSurfaceChanged()
    }
}