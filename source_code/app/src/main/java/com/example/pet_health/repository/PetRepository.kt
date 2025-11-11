package pet_health.repository

import pet_health.data.local.datasources.PetLocalDataSource
import com.example.pet_health.data.model.*

class PetRepository(
    private val local: PetLocalDataSource
) {

    // USER
    suspend fun createUser(user: User) = local.addUser(user)
    suspend fun getUser(id: String) = local.getUser(id)

    // PET
    suspend fun addPet(pet: Pet) = local.addPet(pet)
    suspend fun getPets(userId: String) = local.getPets(userId)

    // HEALTH
    suspend fun addHealthRecord(r: HealthRecord) = local.addHealthRecord(r)
    suspend fun getHealthRecords(petId: String) = local.getHealthRecords(petId)

    // REMINDER
    suspend fun addReminder(r: Reminder) = local.addReminder(r)
    suspend fun getReminders(petId: String) = local.getReminders(petId)

    // VACCINE
    suspend fun addVaccine(v: Vaccine) = local.addVaccine(v)
    suspend fun getVaccines(petId: String) = local.getVaccines(petId)

    // ACTIVITY LOG
    suspend fun addLog(log: UserActivityLog) = local.logAction(log)

    // IMAGE
    suspend fun addImage(img: PetImage) = local.addImage(img)
    suspend fun getImages(petId: String) = local.getImages(petId)
}
