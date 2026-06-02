package com.example.communityeventmanagement.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.core.graphics.createBitmap
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.abs

object ImageUtils {
    /**
     * "Bakes" the crop (scale and offset) into a new image file.
     */
    fun cropAndSaveImage(
        context: Context,
        uri: Uri,
        scale: Float,
        offset: Offset,
        containerWidthPx: Int,
        containerHeightPx: Int,
        isProfile: Boolean
    ): Uri? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val sourceBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (sourceBitmap == null) return null

            // 1. Calculate how the image is initially scaled to fit the container (ContentScale.Crop logic)
            val scaleX = containerWidthPx.toFloat() / sourceBitmap.width
            val scaleY = containerHeightPx.toFloat() / sourceBitmap.height
            val baseScale = maxOf(scaleX, scaleY)
            
            val finalScale = baseScale * scale
            
            // 2. Create the result bitmap (the size of the container)
            val resultBitmap = createBitmap(containerWidthPx, containerHeightPx)
            val canvas = Canvas(resultBitmap)
            
            // 3. Apply transformations
            val matrix = Matrix()
            // Center the image in the container first
            val dx = (containerWidthPx - sourceBitmap.width * finalScale) / 2f
            val dy = (containerHeightPx - sourceBitmap.height * finalScale) / 2f
            
            matrix.postScale(finalScale, finalScale)
            matrix.postTranslate(dx + offset.x, dy + offset.y)
            
            canvas.drawBitmap(sourceBitmap, matrix, null)
            
            // 4. Save the result
            val croppedFile = File(context.cacheDir, "images/cropped_${System.currentTimeMillis()}.jpg")
            croppedFile.parentFile?.mkdirs()
            
            val outputStream = FileOutputStream(croppedFile)
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()
            
            Uri.fromFile(croppedFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generates a deterministic color based on a string.
     */
    fun getColorFromName(name: String): androidx.compose.ui.graphics.Color {
        if (name.isEmpty()) return androidx.compose.ui.graphics.Color(0xFF9E9E9E)
        val colors = listOf(
            androidx.compose.ui.graphics.Color(0xFFEF5350), 
            androidx.compose.ui.graphics.Color(0xFFEC407A), 
            androidx.compose.ui.graphics.Color(0xFFAB47BC), 
            androidx.compose.ui.graphics.Color(0xFF7E57C2), 
            androidx.compose.ui.graphics.Color(0xFF5C6BC0), 
            androidx.compose.ui.graphics.Color(0xFF42A5F5), 
            androidx.compose.ui.graphics.Color(0xFF26A69A), 
            androidx.compose.ui.graphics.Color(0xFF66BB6A), 
            androidx.compose.ui.graphics.Color(0xFFFFA726), 
            androidx.compose.ui.graphics.Color(0xFF8D6E63)
        )
        val index = abs(name.hashCode()) % colors.size
        return colors[index]
    }
}
