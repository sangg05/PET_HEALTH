package pet_health.data.local.dao

import androidx.room.*
import com.example.pet_health.data.entity.VaccineEntity

@Dao
interface VaccineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccine(vaccineEntity: VaccineEntity)

    @Query("SELECT * FROM vaccines WHERE petId = :petId ORDER BY date DESC")
    suspend fun getVaccinesForPet(petId: String): List<VaccineEntity>

    @Delete
    suspend fun deleteVaccine(vaccineEntity: VaccineEntity)
}
