package com.example.pet_health.repository

import com.example.pet_health.data.local.dao.PetDao
import com.example.pet_health.data.entity.PetEntity
import com.example.pet_health.data.entity.SymptomLogEntity
import com.example.pet_health.data.local.dao.SymptomLogDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID


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
class SymptomRepository(private val dao: SymptomLogDao) {

    fun getSymptomLogs(petId: String): Flow<List<SymptomLogEntity>> {
        return dao.getLogsByPet(petId)
    }

    suspend fun addSymptom(petId: String, name: String, desc: String) {
        // Thêm UUID cho id
        val newSymptom = SymptomLogEntity(
            id = UUID.randomUUID().toString(),
            petId = petId,
            name = name,
            description = desc,
            timestamp = System.currentTimeMillis()
        )
        dao.insertLog(newSymptom)
    }
}


