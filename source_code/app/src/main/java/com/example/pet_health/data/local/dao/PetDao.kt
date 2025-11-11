package pet_health.data.local.dao

import androidx.room.*
import pet_health.data.model.Pet

@Dao
interface PetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: Pet)

    @Query("SELECT * FROM pets WHERE userId = :userId")
    suspend fun getPetsByUser(userId: String): List<Pet>

    @Query("SELECT * FROM pets WHERE petId = :id")
    suspend fun getPet(id: String): Pet?

    @Update
    suspend fun updatePet(pet: Pet)

    @Delete
    suspend fun deletePet(pet: Pet)
}
