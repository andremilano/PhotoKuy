package com.andre0016.mobpro1.network

import com.andre0016.mobpro1.model.GalleryItem
import com.andre0016.mobpro1.model.OpStatus
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.http.GET
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

private const val BASE_URL = "https://my-galery.zero-dev.my.id/"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface GalleryApiService {
    // Ambil semua data gallery
    @GET("api/gallery")
    suspend fun getGallery(): List<GalleryItem>

    // Tambah item gallery (title, description, image)
    @Multipart
    @POST("api/gallery")
    suspend fun postGallery(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part
    ): GalleryItem

    // Update item gallery (opsional: bisa tanpa ganti gambar)
    @Multipart
    @PUT("api/gallery/{id}")
    suspend fun updateGallery(
        @Path("id") id: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): GalleryItem

    // Hapus item gallery
    @DELETE("api/gallery/{id}")
    suspend fun deleteGallery(
        @Path("id") id: String
    ): OpStatus
}

object GalleryApi {
    val service: GalleryApiService by lazy {
        retrofit.create(GalleryApiService::class.java)
    }

    fun getFullImageUrl(imagePath: String): String {
        return BASE_URL.trimEnd('/') + imagePath
    }
}
enum class ApiStatus { LOADING, SUCCESS, ERROR }