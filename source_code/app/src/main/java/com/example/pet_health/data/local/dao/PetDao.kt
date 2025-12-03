package com.example.pet_health.data.local.dao

import androidx.room.*
import com.example.pet_health.data.entity.PetEntity



@Dao
interface PetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(petEntity: PetEntity)

    @Query("SELECT * FROM pets WHERE userId = :userId")
    suspend fun getPetsByUser(userId: String): List<PetEntity>

    @Query("SELECT * FROM pets WHERE petId = :id")
    suspend fun getPet(id: String): PetEntity?

    @Update
    suspend fun updatePet(petEntity: PetEntity)

    @Delete
    suspend fun deletePet(petEntity: PetEntity)
    @Query("SELECT * FROM pets")
    suspend fun getAllPets(): List<PetEntity>


    @Query("SELECT * FROM pets WHERE petId = :petId LIMIT 1")
    suspend fun getPetById(petId: String): PetEntity?
}
