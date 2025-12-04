package com.example.pet_health.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pet_health.data.local.datasources.VaccineLocalDataSource
import com.example.pet_health.data.remote.datasources.VaccineRemoteDataSource
import com.example.pet_health.repository.VaccineRepository
import kotlinx.coroutines.launch
import pet_health.data.local.AppDatabase

class VaccineViewModel(context: Context) : ViewModel() {

    private val repo: VaccineRepository

    init {
        val db = AppDatabase.getDatabase(context)
        val local = VaccineLocalDataSource(db.vaccineDao())
        val remote = VaccineRemoteDataSource()
        repo = VaccineRepository(local, remote)
    }

    var isLoading = mutableStateOf(false)
    var success = mutableStateOf(false)

    fun addVaccine(
        petId: String,
        name: String,
        date: Long,
        clinic: String? = null,
        dose: Int? = null,
        note: String? = null,
        imageUri: Uri? = null,
        nextDoseDate: Long? = null
    ) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val result = repo.addVaccine(
                    petId, name, date, clinic, dose, note, imageUri, nextDoseDate
                )
                success.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                success.value = false
            } finally {
                isLoading.value = false
            }
        }
    }
}
