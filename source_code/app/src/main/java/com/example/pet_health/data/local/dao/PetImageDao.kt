package pet_health.data.local.dao

import androidx.room.*
import com.example.pet_health.data.entity.PetImageEntity

@Dao
interface PetImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: PetImageEntity)

    @Query("SELECT * FROM pet_images WHERE petId = :petId")
    suspend fun getImagesByPet(petId: String): List<PetImageEntity>

    @Delete
    suspend fun deleteImage(image: PetImageEntity)
}
