package com.openpix.screenshot

import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import java.util.*

/**
 * Copyright (C), 2020-2020, openpix
 * Author: pix
 * Date: 2020/4/14 16:42
 * Version: 1.0.0
 * Description:
 * History:
 * <author> <time> <version> <desc>
 */
class ScreenShotObserver(handler: Handler?, private val mContentResolver: ContentResolver,
    var listener: (ScreenshotData)->Unit) : ContentObserver(handler) {
    private val PROJECTION = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATA
    )
    private val MEDIA_EXTERNAL_URI_STRING = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()
    private val FILE_NAME_PREFIX =
        ArrayList(
            Arrays.asList(
                "screenshot", "screenshots",  //e.g. SamSung
                "capture", "capture+" //e.g. LG
                , "截屏" // vivo
            )
        )
    private val PATH_SCREENSHOT =
        ArrayList(
            Arrays.asList(
                "screenshot/", "screenshots/",  //e.g. SamSung
                "capture/", "capture+/" //e.g. LG
                , "截屏" // vivo
            )
        )
    override fun deliverSelfNotifications(): Boolean {
        return super.deliverSelfNotifications()
    }

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        if (isSingleImageFile(uri)) {
            handleItem(uri)
        }
    }

    private fun isSingleImageFile(uri: Uri?): Boolean {
        return uri.toString().matches("$MEDIA_EXTERNAL_URI_STRING/[0-9]+".toRegex())
    }

    private fun handleItem(uri: Uri?) {
        var cursor: Cursor? = null
        try {
            cursor = uri?.let {mContentResolver.query(it, PROJECTION, null, null, null)}
            if (cursor != null && cursor.moveToFirst()) {
                val screenshotData = generateScreenshotDataFromCursor(cursor)
                if (screenshotData != null) {
                    Handler(Looper.getMainLooper()).post {
                        listener(screenshotData)
                    }
                }
            }
        } finally {
            cursor?.close()
        }
    }

    private fun generateScreenshotDataFromCursor(cursor: Cursor): ScreenshotData? {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
        val fileName =
            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
        val path =
            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        return if (isPathScreenshot(path) && isFileScreenshot(fileName)) {
            ScreenshotData(id, fileName, path)
        } else {
            null
        }
    }

    private fun isFileScreenshot(fileName: String): Boolean {
        val fileNameLowerCase = fileName.toLowerCase()
        Log.d("fileNameLowerCase", fileNameLowerCase)
        for (str in FILE_NAME_PREFIX) {
            if (fileNameLowerCase.startsWith(str)) {
                return true
            }
        }
        return false
    }

    private fun isPathScreenshot(path: String): Boolean {
        val pathLowerCase = path.toLowerCase()
        Log.d("pathLowerCase", pathLowerCase)
        for (sPath in PATH_SCREENSHOT) {
            if (pathLowerCase.contains(sPath)) {
                return true
            }
        }
        return false
    }

}