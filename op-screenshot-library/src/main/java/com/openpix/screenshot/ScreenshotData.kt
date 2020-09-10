package com.openpix.screenshot

/**
 * Copyright (C), 2020-2020, openpix
 * Author: pix
 * Date: 2020/4/14 16:42
 * Version: 1.0.0
 * Description:
 * History:
 * <author> <time> <version> <desc>
 */
class ScreenshotData(val id: Long, val fileName: String, val path: String) {
    override fun toString(): String {
        return "ScreenshotData(id=$id, fileName='$fileName', path='$path')"
    }
}