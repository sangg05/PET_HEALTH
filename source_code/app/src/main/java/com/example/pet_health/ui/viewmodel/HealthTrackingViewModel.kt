package com.example.pet_health.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pet_health.data.entity.PetEntity
import com.example.pet_health.data.entity.SymptomLogEntity
import com.example.pet_health.data.repository.PetRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class HealthTrackingViewModel(
    private val repository: PetRepository,
    private val currentUserId: String
) : ViewModel() {

    // --- Pets ---
    var pets by mutableStateOf<List<PetEntity>>(emptyList())
        private set

    var selectedPet by mutableStateOf<PetEntity?>(null)
        private set

    // --- Symptoms ---
    private val _symptoms = mutableStateListOf<SymptomLogEntity>()
    val symptoms: List<SymptomLogEntity> get() = _symptoms

    var isExpanded by mutableStateOf(false)
        private set

    init {
        // Load pets và pet đầu tiên
        viewModelScope.launch {
            pets = repository.getPetsByUserId(currentUserId)
            selectedPet = pets.firstOrNull()
            selectedPet?.let { observeSymptoms(it.petId) }
        }
    }

    fun selectPet(pet: PetEntity?) {
        selectedPet = pet
        pet?.let { observeSymptoms(it.petId) }
    }

    fun toggleExpand() {
        isExpanded = !isExpanded
    }

    private fun observeSymptoms(petId: String) {
        viewModelScope.launch {
            repository.getSymptomsOfPet(petId).collectLatest { list ->
                _symptoms.clear()
                _symptoms.addAll(list)
            }
            // Đồng bộ Firebase
            try {
                repository.syncSymptomsFromFirebase(petId)
            } catch (e: Exception) {
                Log.e("HealthTrackingVM", "Sync thất bại", e)
            }
        }
    }

    fun addSymptom(name: String, desc: String) {
        selectedPet?.let { pet ->
            val symptom = SymptomLogEntity(
                id = UUID.randomUUID().toString(),
                petId = pet.petId,
                name = name,
                description = desc,
                timestamp = System.currentTimeMillis()
            )
            viewModelScope.launch {
                try {
                    repository.insertSymptom(symptom)
                    repository.uploadSymptomToFirebase(symptom)
                    // Không cần add thủ công, Flow từ Room sẽ emit
                } catch (e: Exception) {
                    Log.e("HealthTrackingVM", "Thêm symptom thất bại", e)
                }
            }
        }
    }

    fun deleteSymptom(symptom: SymptomLogEntity) {
        viewModelScope.launch {
            try {
                repository.deleteSymptom(symptom)
                repository.deleteSymptomFromFirebase(symptom.petId, symptom.id)
            } catch (e: Exception) {
                Log.e("HealthTrackingVM", "Xóa symptom thất bại", e)
            }
        }
    }

    fun updatePet(updatedPet: PetEntity) {
        viewModelScope.launch {
            try {
                repository.updatePet(updatedPet)
                pets = pets.map { if (it.petId == updatedPet.petId) updatedPet else it }
                if (selectedPet?.petId == updatedPet.petId) selectedPet = updatedPet
            } catch (e: Exception) {
                Log.e("HealthTrackingVM", "Cập nhật pet thất bại", e)
            }
        }
    }
    fun saveSymptom(symptom: SymptomLogEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.insertSymptom(symptom)
                repository.uploadSymptomToFirebase(symptom)
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun getSymptomsFlow(petId: String) = repository.getSymptomsOfPet(petId)
    fun syncSymptoms(petId: String) {
        viewModelScope.launch {
            try {
                repository.syncSymptomsFromFirebase(petId)
            } catch (e: Exception) {
                Log.e("HealthTrackingVM", "Sync thất bại", e)
            }
        }
    }
}
class HealthTrackingViewModelFactory(
    private val repository: PetRepository,
    private val currentUserId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthTrackingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HealthTrackingViewModel(repository, currentUserId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}