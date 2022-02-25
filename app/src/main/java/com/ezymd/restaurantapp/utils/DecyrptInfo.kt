package com.ezymd.restaurantapp.utils

import android.util.Base64
import com.ezymd.restaurantapp.ServerConfig
import java.nio.charset.StandardCharsets

object DecyrptInfo {
    fun decrypt(info: String): String {
        SnapLog.print("========="+info)
        val data: ByteArray = Base64.decode(info, Base64.DEFAULT)
        var text = String(data, StandardCharsets.UTF_8)
        SnapLog.print("========="+text)
        text = text.replace(ServerConfig.APP_MD5, "")
        text = text.replace(ServerConfig.EZYMD_MD5, "")
        SnapLog.print("========="+text)
        return text
    }
}