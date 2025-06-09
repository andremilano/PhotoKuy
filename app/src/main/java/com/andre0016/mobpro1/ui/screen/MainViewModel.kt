package com.andre0016.mobpro1.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andre0016.mobpro1.model.GalleryItem
import com.andre0016.mobpro1.network.ApiStatus
import com.andre0016.mobpro1.network.GalleryApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {

    var data = mutableStateOf(emptyList<GalleryItem>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var deleteStatus = mutableStateOf<String?>(null)
        private set


    fun retrieveData() {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                val result = GalleryApi.service.getGallery()

                withContext(Dispatchers.Main) {
                    data.value = listOf() // Trik: kosongkan dulu agar benar-benar dianggap berubah
                    data.value = result
                    status.value = ApiStatus.SUCCESS
                }

            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                withContext(Dispatchers.Main) {
                    status.value = ApiStatus.ERROR
                }
            }
        }
    }



    fun saveData(title: String, description: String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = GalleryApi.service.postGallery(
                    title.toRequestBody("text/plain".toMediaTypeOrNull()),
                    description.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )
                // ⏱️ Tambahkan delay 300ms sebelum fetch ulang
                delay(300)
                retrieveData()
                Log.d("MainViewModel", "Save result: $result")

            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }


    fun deleteData(galleryId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = GalleryApi.service.deleteGallery(galleryId)

                val isSuccess = result.status == "success" ||
                        result.message?.contains("deleted", ignoreCase = true) == true

                if (isSuccess) {
                    deleteStatus.value = "Data berhasil dihapus"
                    // ⏱️ Tambahkan delay 300ms sebelum fetch ulang
                    delay(300)
                    retrieveData()
                } else {
                    deleteStatus.value = result.message ?: "Gagal menghapus data"
                }

            } catch (e: Exception) {
                Log.d("MainViewModel", "Delete failure: ${e.message}")
                deleteStatus.value = "Terjadi kesalahan: ${e.message}"
            }
        }
    }


    fun clearDeleteStatus() {
        deleteStatus.value = null
    }

    fun clearMessage() {
        errorMessage.value = null
    }

    fun updateData(id: String, title: String, description: String, bitmap: Bitmap?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imagePart = bitmap?.toMultipartBody()

                val updatedItem = GalleryApi.service.updateGallery(
                    id = id,
                    title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
                    description = description.toRequestBody("text/plain".toMediaTypeOrNull()),
                    image = imagePart
                )
                // ⏱️ Tambahkan delay 300ms sebelum fetch ulang
                Log.d("MainViewModel", "Update result: $updatedItem")
                delay(300)
                retrieveData() // refresh data
            } catch (e: Exception) {
                Log.d("MainViewModel", "Update failure: ${e.message}")
                errorMessage.value = "Gagal memperbarui data: ${e.message}"
            }
        }
    }


    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody("image/jpg".toMediaTypeOrNull(), 0, byteArray.size)
        return MultipartBody.Part.createFormData("image", "image.jpg", requestBody)
    }
}

