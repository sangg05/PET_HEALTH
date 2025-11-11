package pet_health.data.local.dao

import androidx.room.*
import pet_health.data.model.Vaccine

@Dao
interface VaccineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccine(vaccine: Vaccine)

    @Query("SELECT * FROM vaccines WHERE petId = :petId ORDER BY date DESC")
    suspend fun getVaccinesForPet(petId: String): List<Vaccine>

    @Delete
    suspend fun deleteVaccine(vaccine: Vaccine)
}
