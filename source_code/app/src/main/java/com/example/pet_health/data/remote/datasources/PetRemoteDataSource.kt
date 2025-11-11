package com.example.pet_health.data.remote.datasources

import com.example.pet_health.data.remote.api.PetHealthApiService
import com.example.pet_health.data.remote.model.*
import retrofit2.HttpException
import java.io.IOException

class PetRemoteDataSource(private val apiService: PetHealthApiService) {

    // ---------------- USER ----------------
    suspend fun loginUser(request: LoginRequest): UserDto =
        safeApiCall { apiService.login(request) }

    suspend fun getUser(userId: String): UserDto =
        safeApiCall { apiService.getUser(userId) }

    // ---------------- PET ----------------
    suspend fun fetchPets(userId: String): List<PetDto> =
        safeApiCall { apiService.getPetsByUserId(userId) }

    suspend fun createPet(pet: PetDto): PetDto =
        safeApiCall { apiService.createPet(pet).body()!! }

    // ---------------- HEALTH RECORD ----------------
    suspend fun fetchHealthRecords(petId: String): List<HealthRecordDto> =
        safeApiCall { apiService.getHealthRecords(petId) }

    suspend fun createHealthRecord(record: HealthRecordDto): HealthRecordDto =
        safeApiCall { apiService.createHealthRecord(record).body()!! }

    // ---------------- REMINDER ----------------
    suspend fun fetchReminders(petId: String): List<ReminderDto> =
        safeApiCall { apiService.getReminders(petId) }

    suspend fun createReminder(reminder: ReminderDto): ReminderDto =
        safeApiCall { apiService.createReminder(reminder).body()!! }

    // ---------------- VACCINE ----------------
    suspend fun fetchVaccines(petId: String): List<VaccineDto> =
        safeApiCall { apiService.getVaccines(petId) }

    suspend fun createVaccine(vaccine: VaccineDto): VaccineDto =
        safeApiCall { apiService.createVaccine(vaccine).body()!! }

    // ---------------- PET IMAGE ----------------
    suspend fun fetchPetImages(petId: String): List<PetImageDto> =
        safeApiCall { apiService.getPetImages(petId) }

    suspend fun uploadPetImage(image: PetImageDto): PetImageDto =
        safeApiCall { apiService.uploadPetImage(image).body()!! }

    // ---------------- UTILS ----------------
    private suspend fun <T> safeApiCall(call: suspend () -> T): T {
        return try {
            call.invoke()
        } catch (e: IOException) {
            throw NetworkException("Lỗi kết nối mạng.", e)
        } catch (e: HttpException) {
            val code = e.code()
            val message = e.response()?.errorBody()?.string() ?: "Lỗi HTTP $code"
            if (code == 401) throw AuthenticationException("Phiên đăng nhập hết hạn.", e)
            throw RemoteDataSourceException(message, e)
        } catch (e: Exception) {
            throw RemoteDataSourceException("Lỗi không xác định: ${e.message}", e)
        }
    }
}

// ---------------- Custom Exceptions ----------------
class NetworkException(message: String, cause: Throwable? = null) : IOException(message, cause)
class RemoteDataSourceException(message: String, cause: Throwable? = null) : Exception(message, cause)
class AuthenticationException(message: String, cause: Throwable? = null) : Exception(message, cause)