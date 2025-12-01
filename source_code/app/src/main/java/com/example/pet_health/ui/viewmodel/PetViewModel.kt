//package com.example.pet_health.ui.viewmodel
//
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.example.pet_health.data.entity.PetEntity
//import com.example.pet_health.data.entity.SymptomLogEntity
//import com.example.pet_health.data.entity.UserEntity
//import com.example.pet_health.repository.PetRepository
//import kotlinx.coroutines.launch
//
//class PetViewModel(private val repository: PetRepository) : ViewModel() {
//
//    var pets = mutableStateOf(listOf<PetEntity>())
//        private set
//
//    var selectedPet = mutableStateOf<PetEntity?>(null)
//        private set
//
//    var isExpanded = mutableStateOf(false)
//        private set
//    var symptomMap = mutableStateOf<Map<String, List<SymptomLogEntity>>>(emptyMap())
//        private set
//
//
//    init {
//        prepopulateData()
//        prepopulateSymptoms()
//
//    }
//
//    private fun prepopulateData() {
//        // Tạo User giả (chỉ để tham khảo, không dùng DB)
//        val userId = "u1"
//        val user = UserEntity(
//            userId = userId,
//            name = "Nguyen Van A",
//            email = "test@example.com",
//            passwordHash = "123456"
//        )
//
//        // Tạo Pet mẫu
//        val petList = listOf(
//            PetEntity(
//                petId = "p1",
//                userId = user.userId,
//                name = "Cún con",
//                species = "Chó",
//                breed = "Phốc sóc",
//                birthDate = System.currentTimeMillis(),
//                weightKg = 2.5f,
//                sizeCm =3.5f,
//                healthStatus = "Khỏe"
//            ),
//            PetEntity(
//                petId = "p2",
//                userId = user.userId,
//                name = "Cún con",
//                species = "Chó",
//                breed = "Phốc sóc",
//                birthDate = System.currentTimeMillis(),
//                weightKg = 2.5f,
//                sizeCm =2.5f,
//                healthStatus = "Khỏe"
//            ),
//            PetEntity(
//                petId = "p3",
//                userId = user.userId,
//                name = "Cún con",
//                species = "Chó",
//                breed = "Phốc sóc",
//                birthDate = System.currentTimeMillis(),
//                weightKg = 2.5f,
//                sizeCm =4.5f,
//                healthStatus = "Khỏe"
//            ),
//            PetEntity(
//                petId = "p4",
//                userId = user.userId,
//                name = "Cún con",
//                species = "Chó",
//                breed = "Phốc sóc",
//                birthDate = System.currentTimeMillis(),
//                weightKg = 2.5f,
//                sizeCm =3.5f,
//                healthStatus = "Khỏe"
//            ),
//            PetEntity(
//                petId = "p5",
//                userId = user.userId,
//                name = "Cún con",
//                species = "Chó",
//                breed = "Phốc sóc",
//                birthDate = System.currentTimeMillis(),
//                weightKg = 2.5f,
//                sizeCm =3.5f,
//                healthStatus = "Khỏe"
//            ),
//            PetEntity(
//                petId = "p6",
//                userId = user.userId,
//                name = "Mèo con",
//                species = "Mèo",
//                breed = "Anh lông ngắn",
//                birthDate = System.currentTimeMillis(),
//                weightKg = 1.2f,
//                sizeCm =3.5f,
//                healthStatus = "Khỏe"
//            )
//
//        )
//        pets.value = petList
//    }
//
//    // Tạo triệu chứng mẫu cho từng pet
//    private fun prepopulateSymptoms() {
//        val map = mutableMapOf<String, List<SymptomLogEntity>>()
//
//        pets.value.forEach { pet ->
//            val list = when (pet.petId) {
//                "p1" -> listOf(
//                    SymptomLogEntity("s1", pet.petId, "Ho nhẹ", "Ho vào buổi sáng 2 lần", System.currentTimeMillis() - 86_400_000),
//                    SymptomLogEntity("s2", pet.petId, "Biếng ăn", "Ăn ít hơn bình thường", System.currentTimeMillis() - 43_200_000)
//                )
//                "p2" -> listOf(
//                    SymptomLogEntity("s3", pet.petId, "Nôn nhẹ", "Nôn 1 lần sau khi ăn", System.currentTimeMillis() - 7_200_000)
//                )
//                "p6" -> listOf(
//                    SymptomLogEntity("s4", pet.petId, "Mệt mỏi", "Ít vận động, nằm nhiều", System.currentTimeMillis() - 3_600_000)
//                )
//                else -> emptyList()
//            }
//            map[pet.petId] = list
//        }
//
//        symptomMap.value = map
//    }
//
//        fun selectPet(pet: PetEntity?) {
//        selectedPet.value = pet
//    }
//    fun toggleExpand() {
//        isExpanded.value = !isExpanded.value
//    }
//
//    fun updatePet(updatedPet: PetEntity) {
//        pets.value = pets.value.map { if (it.petId == updatedPet.petId) updatedPet else it }
//    }
//
//    fun loadPetById(petId: String) {
//        viewModelScope.launch {
//            selectedPet.value = repository.getPetById(petId)
//                ?: pets.value.find { it.petId == petId } // fallback nếu không có repo
//        }
//    }
//    // Lấy danh sách triệu chứng của pet hiện tại
//    fun getSymptomsOfSelectedPet(): List<SymptomLogEntity> {
//        val petId = selectedPet.value?.petId ?: return emptyList()
//        return symptomMap.value[petId] ?: emptyList()
//    }
//
//    // Thêm triệu chứng mới cho pet hiện tại
//    fun addSymptomForSelectedPet(name: String, desc: String) {
//        val petId = selectedPet.value?.petId ?: return
//        val current = symptomMap.value[petId] ?: emptyList()
//        val nextId = "s${current.size + 1}"  // tạo id cố định tăng dần
//        val newSymptom = SymptomLogEntity(nextId, petId, name, desc, System.currentTimeMillis())
//        symptomMap.value = symptomMap.value.toMutableMap().apply { put(petId, current + newSymptom) }
//    }
//
//}
//class PetViewModelFactory(
//    private val repository: PetRepository
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(PetViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return PetViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
//
//
//
//
//
//
