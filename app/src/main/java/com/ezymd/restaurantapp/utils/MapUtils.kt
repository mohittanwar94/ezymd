package com.ezymd.restaurantapp.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.VectorDrawable
import androidx.core.content.ContextCompat
import com.ezymd.restaurantapp.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs
import kotlin.math.atan

object MapUtils {


     fun getCarBitmap(
        context: Context
    ): BitmapDescriptor? {
        val vectorDrawable =
            ContextCompat.getDrawable(context, R.drawable.ic_delivery_man) as VectorDrawable?

        val h = vectorDrawable!!.intrinsicHeight
        val w = vectorDrawable.intrinsicWidth

        vectorDrawable.setBounds(0, 0, w, h)

        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bm)

    }

    fun getOriginDestinationMarkerBitmap(): Bitmap {
        val height = 20
        val width = 20
        val bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }

    fun getRotation(start: LatLng, end: LatLng): Float {
        val latDifference: Double = abs(start.latitude - end.latitude)
        val lngDifference: Double = abs(start.longitude - end.longitude)
        var rotation = -1F
        when {
            start.latitude < end.latitude && start.longitude < end.longitude -> {
                rotation = Math.toDegrees(atan(lngDifference / latDifference)).toFloat()
            }
            start.latitude >= end.latitude && start.longitude < end.longitude -> {
                rotation = (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 90).toFloat()
            }
            start.latitude >= end.latitude && start.longitude >= end.longitude -> {
                rotation = (Math.toDegrees(atan(lngDifference / latDifference)) + 180).toFloat()
            }
            start.latitude < end.latitude && start.longitude >= end.longitude -> {
                rotation =
                    (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 270).toFloat()
            }
        }
        return rotation
    }

    /**
     * This function returns the list of locations of Car during the trip i.e. from Origin to Destination
     */
    fun getListOfLocations(): ArrayList<LatLng> {
        val locationList = ArrayList<LatLng>()
        locationList.add(LatLng(28.436970000000002, 77.11272000000001))
        locationList.add(LatLng(28.43635, 77.11289000000001))
        locationList.add(LatLng(28.4353, 77.11317000000001))
        locationList.add(LatLng(28.435280000000002, 77.11332))
        locationList.add(LatLng(28.435350000000003, 77.11368))
        locationList.add(LatLng(28.4356, 77.11498))
        locationList.add(LatLng(28.435660000000002, 77.11519000000001))
        locationList.add(LatLng(28.43568, 77.11521))
        locationList.add(LatLng(28.436580000000003, 77.11499))
        locationList.add(LatLng(28.436590000000002, 77.11507))
        return locationList
    }

}