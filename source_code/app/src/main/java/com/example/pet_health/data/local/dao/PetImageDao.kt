package pet_health.data.local.dao

import androidx.room.*
import com.example.pet_health.data.model.PetImage

@Dao
interface PetImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: PetImage)

    @Query("SELECT * FROM pet_images WHERE petId = :petId")
    suspend fun getImagesByPet(petId: String): List<PetImage>

    @Delete
    suspend fun deleteImage(image: PetImage)
}
