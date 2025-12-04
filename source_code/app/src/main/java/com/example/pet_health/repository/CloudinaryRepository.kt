package com.example.pet_health.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class CloudinaryRepository {

    private val CLOUD_NAME = "dtyhvuctq"  // Paste Cloud Name của bạn
    private val UPLOAD_PRESET = "pet_health_upload"

    private val uploadUrl = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()


    suspend fun uploadImage(
        context: Context,
        imageUri: Uri,
        userId: String
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {

                // 1. Convert Uri to File
                val file = uriToFile(context, imageUri)

                // 2. Optimize image nếu quá lớn
                val optimizedFile = optimizeImage(file)

                // 3. Tạo tên file unique
                val publicId = "pet_${userId}_${System.currentTimeMillis()}"

                // 4. Tạo multipart request
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        optimizedFile.name,
                        optimizedFile.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                    .addFormDataPart("upload_preset", UPLOAD_PRESET)
                    .addFormDataPart("public_id", publicId)
                    .addFormDataPart("folder", "pet_health/pets")
                    .build()

                val request = Request.Builder()
                    .url(uploadUrl)
                    .post(requestBody)
                    .build()


                // 5. Execute request
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                // 6. Cleanup temp files
                file.delete()
                if (optimizedFile != file) {
                    optimizedFile.delete()
                }

                // 7. Parse response
                if (response.isSuccessful && responseBody != null) {
                    val json = JSONObject(responseBody)
                    val imageUrl = json.getString("secure_url")


                    Result.success(imageUrl)
                } else {
                    Result.failure(Exception("Upload failed: ${response.code}"))
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    suspend fun deleteImage(imageUrl: String): Result<Boolean> {
        // Cloudinary free plan cho phép giữ ảnh vô thời hạn
        return Result.success(true)
    }


    private fun uriToFile(context: Context, uri: Uri): File {
        val contentResolver = context.contentResolver
        val tempFile = File(
            context.cacheDir,
            "upload_${System.currentTimeMillis()}.jpg"
        )

        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }

    /**
     * Optimize image nếu quá lớn (> 1MB thì resize)
     */
    private fun optimizeImage(file: File): File {
        val maxSizeBytes = 1 * 1024 * 1024 // 1MB

        if (file.length() <= maxSizeBytes) {
            return file // Ảnh đã đủ nhỏ
        }


        // Decode bitmap
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)

        // Calculate scale
        val maxDimension = 1920
        val scale = if (bitmap.width > bitmap.height) {
            maxDimension.toFloat() / bitmap.width
        } else {
            maxDimension.toFloat() / bitmap.height
        }

        val newWidth = (bitmap.width * scale).toInt()
        val newHeight = (bitmap.height * scale).toInt()

        // Resize
        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            newWidth,
            newHeight,
            true
        )

        // Save to new file
        val optimizedFile = File(
            file.parent,
            "optimized_${file.name}"
        )

        FileOutputStream(optimizedFile).use { out ->
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }

        // Cleanup
        bitmap.recycle()
        resizedBitmap.recycle()


        return optimizedFile
    }
}