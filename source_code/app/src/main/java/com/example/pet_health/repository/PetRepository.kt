package com.example.pet_health.repository

import com.example.pet_health.data.local.dao.PetDao
import com.example.pet_health.data.entity.PetEntity


class PetRepository(private val dao: PetDao) {

    suspend fun getPetsByUser(userId: String, collapsed: Boolean = false): List<PetEntity> {
        val allPets = dao.getPetsByUser(userId)
        return if (collapsed) allPets.take(3) else allPets
    }

    suspend fun insertPet(pet: PetEntity) = dao.insertPet(pet)
    suspend fun updatePet(pet: PetEntity) = dao.updatePet(pet)
    suspend fun deletePet(pet: PetEntity) = dao.deletePet(pet)
    suspend fun getPetById(petId: String): PetEntity? {
        return dao.getPet(petId)
    }
}

