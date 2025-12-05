package pet_health.data.local.dao

import androidx.room.*
import com.example.pet_health.data.entity.VaccineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaccineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccine(vaccineEntity: VaccineEntity)

    @Query("SELECT * FROM vaccines ORDER BY date DESC")
    fun getAllVaccines(): Flow<List<VaccineEntity>> // <-- trả về Flow luôn

    @Query("SELECT * FROM vaccines WHERE petId = :petId ORDER BY date DESC")
    fun getVaccinesForPet(petId: String): Flow<List<VaccineEntity>>

    @Delete
    suspend fun deleteVaccine(vaccineEntity: VaccineEntity)

    @Update
    suspend fun updateVaccine(vaccineEntity: VaccineEntity)
}
