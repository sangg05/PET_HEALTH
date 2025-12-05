package com.example.pet_health.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pet_health.data.entity.VaccineEntity
import com.example.pet_health.data.entity.PetEntity
import com.example.pet_health.data.repository.VaccineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VaccineViewModel(context: Context) : ViewModel() {

    private val repository = VaccineRepository(context)

    // Danh sách tất cả vaccine (Flow tự động update)
    val vaccines: StateFlow<List<VaccineEntity>> = repository.getAllVaccines()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Danh sách tất cả pet
    private val _pets = mutableStateOf<List<PetEntity>>(emptyList())
    val pets: State<List<PetEntity>> = _pets

    init {
        refreshPets()
        // ✅ Sync data từ Firestore khi khởi tạo ViewModel
        syncFromFirestore()
    }

    // ✅ Sync vaccines từ Firestore về Room DB
    fun syncFromFirestore() {
        viewModelScope.launch {
            repository.syncVaccinesFromFirestore()
        }
    }

    // Refresh danh sách pet
    fun refreshPets() {
        viewModelScope.launch {
            _pets.value = repository.getAllPets()
        }
    }

    // Lấy vaccine theo ID
    fun getVaccineById(vaccineId: String): VaccineEntity? {
        return vaccines.value.find { it.vaccineId == vaccineId }
    }

    // Thêm vaccine mới
    fun addVaccine(vaccine: VaccineEntity) = viewModelScope.launch {
        repository.addVaccine(vaccine)
        refreshPets()
    }

    // Xóa vaccine
    fun deleteVaccine(vaccine: VaccineEntity) = viewModelScope.launch {
        repository.deleteVaccine(vaccine)
        refreshPets()
    }

    // Cập nhật vaccine
    fun updateVaccine(vaccine: VaccineEntity) = viewModelScope.launch {
        repository.updateVaccine(vaccine)
        refreshPets()
    }

    // Lấy danh sách vaccine theo petId
    fun getVaccinesForPet(petId: String): List<VaccineEntity> {
        return vaccines.value.filter { it.petId == petId }
    }

    // Upload ảnh vaccine
    fun uploadVaccineImage(context: Context, uri: Uri, userId: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val url = repository.uploadVaccineImage(context, uri, userId)
            onResult(url)
        }
    }
}

class VaccineViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VaccineViewModel(context.applicationContext) as T
    }
}