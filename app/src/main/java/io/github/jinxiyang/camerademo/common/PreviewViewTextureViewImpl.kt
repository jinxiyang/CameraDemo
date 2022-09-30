package io.github.jinxiyang.camerademo.common

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import io.github.jinxiyang.camerademo.Camera2Activity

class PreviewViewTextureViewImpl: PreviewView {

    private lateinit var mTextureView: TextureView

    private var mCallback: PreviewView.Callback? = null

    private var mPreviewSize: Size = Size(0, 0)

    override fun isReady(): Boolean {
        return mTextureView.surfaceTexture != null
    }

    override fun setOutputSize(width: Int, height: Int) {
        val layoutParams = mTextureView.layoutParams
        layoutParams.height = width
        layoutParams.width = height
        mTextureView.layoutParams = layoutParams
        mTextureView.surfaceTexture?.setDefaultBufferSize(height , width)
    }

    override fun getOutputClass(): Class<*> {
        return SurfaceTexture::class.java
    }

    override fun getSurface(): Surface {
        return Surface(mTextureView.surfaceTexture)
    }

    override fun setCallback(callback: PreviewView.Callback) {
        mCallback = callback
    }

    override fun getSize(): Size {
        return mPreviewSize
    }

    private fun createTextureView(context: Context, parent: ViewGroup) {
        val textureView = TextureView(context)
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                Log.i(Camera2Activity.TAG, "onSurfaceTextureAvailable: $width  $height")
                mPreviewSize = Size(width, height)
                mCallback?.onSurfaceChanged()
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                mPreviewSize = Size(width, height)
                Log.i(Camera2Activity.TAG, "onSurfaceTextureSizeChanged:  $width  $height")
                mCallback?.onSurfaceChanged()
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                mPreviewSize = Size(0, 0)
                Log.i(Camera2Activity.TAG, "onSurfaceTextureDestroyed: ")
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            }

        }
        val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.gravity = Gravity.CENTER
        parent.addView(textureView, layoutParams)
        mTextureView = textureView
//        textureView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
//
//        }
    }

    companion object {

        fun create(context: Context, parent: ViewGroup): PreviewViewTextureViewImpl {
            val previewView = PreviewViewTextureViewImpl()
            previewView.createTextureView(context, parent)
            return previewView
        }
    }
}