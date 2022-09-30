package io.github.jinxiyang.camerademo.common

import android.content.Context
import android.util.AttributeSet
import android.util.Size
import android.view.Surface
import android.widget.FrameLayout

class CameraView : FrameLayout, PreviewView {

    private val mPreviewView: PreviewView

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {

        mPreviewView = createPreview(context)
    }


    private fun createPreview(context: Context): PreviewView {
        return PreviewViewTextureViewImpl.create(context, this)
    }

    fun getPreviewView() = mPreviewView

    override fun isReady(): Boolean {
        return mPreviewView.isReady()
    }

    override fun setOutputSize(width: Int, height: Int) {
        return mPreviewView.setOutputSize(width, height)
    }

    override fun getOutputClass(): Class<*> {
        return mPreviewView.getOutputClass()
    }

    override fun getSurface(): Surface {
        return mPreviewView.getSurface()
    }

    override fun setCallback(callback: PreviewView.Callback) {
        return mPreviewView.setCallback(callback)
    }

    override fun getSize(): Size {
        return mPreviewView.getSize()
    }
}