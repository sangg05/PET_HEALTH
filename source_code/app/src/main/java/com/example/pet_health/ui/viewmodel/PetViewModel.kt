package com.example.pet_health.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pet_health.data.entity.PetEntity
import com.example.pet_health.data.entity.UserEntity
import com.example.pet_health.repository.PetRepository
import kotlinx.coroutines.launch


class PetViewModel(private val repository: PetRepository) : ViewModel() {

    var pets = mutableStateOf(listOf<PetEntity>())
        private set

    var selectedPet = mutableStateOf<PetEntity?>(null)
        private set

    var isExpanded = mutableStateOf(false)
        private set

    init {
        prepopulateData()
    }

    private fun prepopulateData() {
        // Tạo User giả (chỉ để tham khảo, không dùng DB)
        val userId = "u1"
        val user = UserEntity(
            userId = userId,
            name = "Nguyen Van A",
            email = "test@example.com",
            passwordHash = "123456"
        )

        // Tạo Pet mẫu
        val petList = listOf(
            PetEntity(
                petId = "p1",
                userId = user.userId,
                name = "Cún con",
                species = "Chó",
                breed = "Phốc sóc",
                birthDate = System.currentTimeMillis(),
                weightKg = 2.5f,
                sizeCm =3.5f,
                healthStatus = "Khỏe"
            ),
            PetEntity(
                petId = "p2",
                userId = user.userId,
                name = "Cún con",
                species = "Chó",
                breed = "Phốc sóc",
                birthDate = System.currentTimeMillis(),
                weightKg = 2.5f,
                sizeCm =2.5f,
                healthStatus = "Khỏe"
            ),
            PetEntity(
                petId = "p3",
                userId = user.userId,
                name = "Cún con",
                species = "Chó",
                breed = "Phốc sóc",
                birthDate = System.currentTimeMillis(),
                weightKg = 2.5f,
                sizeCm =4.5f,
                healthStatus = "Khỏe"
            ),
            PetEntity(
                petId = "p4",
                userId = user.userId,
                name = "Cún con",
                species = "Chó",
                breed = "Phốc sóc",
                birthDate = System.currentTimeMillis(),
                weightKg = 2.5f,
                sizeCm =3.5f,
                healthStatus = "Khỏe"
            ),
            PetEntity(
                petId = "p5",
                userId = user.userId,
                name = "Cún con",
                species = "Chó",
                breed = "Phốc sóc",
                birthDate = System.currentTimeMillis(),
                weightKg = 2.5f,
                sizeCm =3.5f,
                healthStatus = "Khỏe"
            ),
            PetEntity(
                petId = "p6",
                userId = user.userId,
                name = "Mèo con",
                species = "Mèo",
                breed = "Anh lông ngắn",
                birthDate = System.currentTimeMillis(),
                weightKg = 1.2f,
                sizeCm =3.5f,
                healthStatus = "Khỏe"
            )

        )
        pets.value = petList
    }

    fun toggleExpand() {
        isExpanded.value = !isExpanded.value
    }

    fun updatePet(updatedPet: PetEntity) {
        pets.value = pets.value.map { if (it.petId == updatedPet.petId) updatedPet else it }
    }

    fun loadPetById(petId: String) {
        viewModelScope.launch {
            selectedPet.value = repository.getPetById(petId)
                ?: pets.value.find { it.petId == petId } // fallback nếu không có repo
        }
    }
}

