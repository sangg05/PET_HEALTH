package com.example.pet_health.data.local.datasources

import com.example.pet_health.data.entity.VaccineEntity
import pet_health.data.local.dao.VaccineDao

class VaccineLocalDataSource(private val dao: VaccineDao) {

    suspend fun insert(v: VaccineEntity) = dao.insertVaccine(v)

    suspend fun getByPet(petId: String) = dao.getVaccinesForPet(petId)

    suspend fun delete(v: VaccineEntity) = dao.deleteVaccine(v)
}
