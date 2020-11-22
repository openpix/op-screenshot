package com.openpix.screenshot

import android.app.Activity
import android.content.ContentResolver
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.provider.MediaStore
import android.view.PixelCopy
import android.view.View
import android.view.View.MeasureSpec
import android.view.Window


/**
 * Copyright (C), 2020-2020, openpix
 * Author: pix
 * Date: 2020/4/14 16:42
 * Version: 1.0.0
 * Description:
 * History:
 * <author> <time> <version> <desc>
 */
class ShotWatch(contentResolver: ContentResolver, listener: (ScreenshotData)->Unit) {
    private val mHandlerThread: HandlerThread = HandlerThread("ShotWatch")
    private val mHandler: Handler
    private val mContentResolver: ContentResolver
    private val mContentObserver: ContentObserver

    init {
        mHandlerThread.start()
        mHandler = Handler(mHandlerThread.looper)
        mContentResolver = contentResolver
        mContentObserver = ScreenShotObserver(mHandler, contentResolver, listener)
    }
    fun register() {
        mContentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            mContentObserver
        )
    }

    fun unregister() {
        mContentResolver.unregisterContentObserver(mContentObserver)
    }

    companion object {
        /**
         * shot the View to Bitmap, Visible and Invisible
         */
        fun shotView(v: View?, window: Window, bitmapCallback:(bitmap:Bitmap?)->Unit){
            if (null == v) {
                return
            }
            var b: Bitmap?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888, true);
                // 获取layout的位置
                val location = IntArray(2)
                v.getLocationInWindow(location)
                PixelCopy.request(window,
                    Rect(location[0], location[1], location[0] + v.width, location[1] + v.height),
                    b, { copyResult ->
                        try {
                            if (copyResult == PixelCopy.SUCCESS) {
                                bitmapCallback?.let {
                                    it.invoke(b)
                                }
                            } else {
                            }
                        } catch (t: Throwable) {
                        }
                    }, Handler(Looper.getMainLooper()))
            }
            else {
                v.isDrawingCacheEnabled = true
                v.buildDrawingCache()
                if (Build.VERSION.SDK_INT >= 11) {
                    v.measure(
                        MeasureSpec.makeMeasureSpec(
                            v.width,
                            MeasureSpec.EXACTLY
                        ), MeasureSpec.makeMeasureSpec(
                            v.height, MeasureSpec.EXACTLY
                        )
                    )
                    v.layout(
                        v.x.toInt(), v.y.toInt(),
                        v.x.toInt() + v.measuredWidth,
                        v.y.toInt() + v.measuredHeight
                    )
                } else {
                    v.measure(
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    )

                    v.layout(0, 0, v.measuredWidth, v.measuredHeight)
                }

                var width = if (v.measuredWidth > 0 ) v.measuredWidth else 720
                val height = if (v.measuredHeight > 0) v.measuredHeight else 1080
                b = Bitmap.createBitmap(
                    v.drawingCache,
                    0,
                    0,
                    width,
                    height
                )

                v.isDrawingCacheEnabled = false
                v.destroyDrawingCache()
                bitmapCallback?.let { it.invoke(b) }
            }
        }

        /**
         * shot the current screen ,with the status but the status is trans *
         *
         * @param ctx current activity
         */
        fun shotActivity(activity: Activity):Bitmap {
            val view: View = activity.getWindow().getDecorView()
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()

            val bp = Bitmap.createBitmap(
                view.drawingCache, 0, 0, view.measuredWidth,
                view.measuredHeight
            )

            view.isDrawingCacheEnabled = false
            view.destroyDrawingCache()

            return bp
        }


    }

}