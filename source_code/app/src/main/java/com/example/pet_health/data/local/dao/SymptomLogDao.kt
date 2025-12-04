package com.example.pet_health.data.local.dao

import androidx.room.*
import com.example.pet_health.data.entity.SymptomLogEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete


@Dao
interface SymptomLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SymptomLogEntity)

    @Query("SELECT * FROM symptom_logs WHERE petId = :petId ORDER BY timestamp DESC")
    fun getLogsByPet(petId: String): Flow<List<SymptomLogEntity>>

    @Delete
    suspend fun deleteLog(log: SymptomLogEntity)
}


