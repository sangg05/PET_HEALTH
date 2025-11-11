package pet_health.data.local.datasources

import pet_health.data.local.dao.*
import com.example.pet_health.data.model.*

class PetLocalDataSource(
    private val userDao: UserDao,
    private val petDao: PetDao,
    private val healthDao: HealthRecordDao,
    private val reminderDao: ReminderDao,
    private val vaccineDao: VaccineDao,
    private val activityDao: UserActivityLogDao,
    private val petImageDao: PetImageDao
) {

    // USER
    suspend fun addUser(user: User) = userDao.insertUser(user)
    suspend fun getUser(id: String) = userDao.getUser(id)

    // PET
    suspend fun addPet(pet: Pet) = petDao.insertPet(pet)
    suspend fun getPets(userId: String) = petDao.getPetsByUser(userId)

    // HEALTH
    suspend fun addHealthRecord(r: HealthRecord) = healthDao.insertHealthRecord(r)
    suspend fun getHealthRecords(petId: String) = healthDao.getRecordsForPet(petId)

    // REMINDER
    suspend fun addReminder(r: Reminder) = reminderDao.insertReminder(r)
    suspend fun getReminders(petId: String) = reminderDao.getReminders(petId)

    // VACCINE
    suspend fun addVaccine(v: Vaccine) = vaccineDao.insertVaccine(v)
    suspend fun getVaccines(petId: String) = vaccineDao.getVaccinesForPet(petId)

    // ACTIVITY LOG
    suspend fun logAction(log: UserActivityLog) = activityDao.insertLog(log)

    // IMAGE
    suspend fun addImage(img: PetImage) = petImageDao.insertImage(img)
    suspend fun getImages(petId: String) = petImageDao.getImagesByPet(petId)
}
