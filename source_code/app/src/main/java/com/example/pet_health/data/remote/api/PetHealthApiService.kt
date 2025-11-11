package com.example.pet_health.data.remote.api

import com.example.pet_health.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface PetHealthApiService {

    // -------- USER --------
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): UserDto

    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: String): UserDto

    // -------- PETS --------
    @GET("users/{userId}/pets")
    suspend fun getPetsByUserId(@Path("userId") userId: String): List<PetDto>

    @POST("pets")
    suspend fun createPet(@Body pet: PetDto): Response<PetDto>

    // -------- HEALTH RECORDS --------
    @GET("pets/{petId}/health_records")
    suspend fun getHealthRecords(@Path("petId") petId: String): List<HealthRecordDto>

    @POST("health_records")
    suspend fun createHealthRecord(@Body record: HealthRecordDto): Response<HealthRecordDto>

    // -------- REMINDERS --------
    @GET("pets/{petId}/reminders")
    suspend fun getReminders(@Path("petId") petId: String): List<ReminderDto>

    @POST("reminders")
    suspend fun createReminder(@Body reminder: ReminderDto): Response<ReminderDto>

    // -------- VACCINES --------
    @GET("pets/{petId}/vaccines")
    suspend fun getVaccines(@Path("petId") petId: String): List<VaccineDto>

    @POST("vaccines")
    suspend fun createVaccine(@Body vaccine: VaccineDto): Response<VaccineDto>

    // -------- PET IMAGES --------
    @GET("pets/{petId}/images")
    suspend fun getPetImages(@Path("petId") petId: String): List<PetImageDto>

    @POST("pet_images")
    suspend fun uploadPetImage(@Body image: PetImageDto): Response<PetImageDto>
}