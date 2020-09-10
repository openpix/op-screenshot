package com.openpix.screenshot

import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore

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

}