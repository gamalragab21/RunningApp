package com.example.runningapp.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converts {
    @TypeConverter
    fun fromBitmap(value: Bitmap?): ByteArray? {
        val outputStream=ByteArrayOutputStream()
        value?.compress(Bitmap.CompressFormat.PNG,100,outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
    }
}