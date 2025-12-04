package com.example.pet_health.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pet_health.data.entity.PetEntity
import com.example.pet_health.data.repository.PetRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import java.io.File
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID


class PetViewModel(private val repository: PetRepository) : ViewModel() {


    var tempImageUri: Uri? = null
    private val _pets = mutableStateOf<List<PetEntity>>(emptyList())
    val pets: State<List<PetEntity>> = _pets

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        setupRealtimeListener()
        loadPets()
    }
    private fun setupRealtimeListener() {
        val userId = auth.currentUser?.uid ?: return

        _isLoading.value = true

        listenerRegistration = db.collection("users")
            .document(userId)
            .collection("pets")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PetViewModel", "Listen failed", error)
                    _isLoading.value = false
                    return@addSnapshotListener
                }

                val petsList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(PetEntity::class.java)
                } ?: emptyList()

                // ⭐ Tự động cập nhật StateFlow
                _pets.value = petsList
                _isLoading.value = false

                Log.d("PetViewModel", "Realtime update: ${petsList.size} pets")
            }
    }

    private fun fetchPets() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = auth.currentUser?.uid
            _pets.value = if (userId != null) {
                repository.getPetsByUserId(userId)
            } else {
                emptyList()
            }
            _isLoading.value = false
        }
    }

    fun addOrUpdatePet(
        context: Context,
        name: String,
        species: String,
        breed: String = "",
        birthDate: Long,
        color: String? = null,
        weightKg: Double,
        sizeCm: Double? = null,
        adoptionDate: Long? = null,
        imageUri: Uri? = null, // Ảnh mới chọn
        existingImageUrl: String? = null, // Ảnh cũ nếu edit
        editMode: Boolean = false,
        petId: String? = null, // Chỉ dùng khi edit
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

            // 1. Upload ảnh nếu có ảnh mới được chọn
            val finalImageUrl: String? = if (imageUri != null && imageUri.toString() != existingImageUrl) {
                // Có ảnh mới được chọn -> upload
                try {
                    val storage = FirebaseStorage.getInstance()
                    val ref = storage.reference.child("pets/${UUID.randomUUID()}.jpg")
                    ref.putFile(imageUri).await()
                    ref.downloadUrl.await().toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    existingImageUrl // fallback nếu upload fail
                }
            } else {
                // Không có ảnh mới -> giữ ảnh cũ
                existingImageUrl
            }

            // 2. Tạo PetEntity
            val pet = PetEntity(
                petId = petId ?: UUID.randomUUID().toString(), // Dùng petId cũ nếu edit mode
                userId = currentUserId,
                name = name,
                species = species,
                breed = breed,
                color = color,
                imageUrl = finalImageUrl,
                birthDate = birthDate,
                weightKg = weightKg.toFloat(),
                sizeCm = sizeCm?.toFloat(),
                healthStatus = "",
                adoptionDate = adoptionDate
            )

            // 3. Lưu Room
            if (editMode && petId != null) {
                repository.updatePet(pet)
                // Cập nhật state
                _pets.value = _pets.value.map {
                    if (it.petId == pet.petId) pet else it
                }
            } else {
                repository.insertPet(pet)
                // Thêm vào state
                _pets.value = _pets.value + pet
            }

            // 4. Lưu Firestore
            val petMap = hashMapOf(
                "petId" to pet.petId,
                "userId" to pet.userId,
                "name" to pet.name,
                "species" to pet.species,
                "breed" to pet.breed,
                "color" to pet.color,
                "imageUrl" to pet.imageUrl,
                "birthDate" to pet.birthDate,
                "weightKg" to pet.weightKg,
                "sizeCm" to pet.sizeCm,
                "healthStatus" to pet.healthStatus,
                "adoptionDate" to pet.adoptionDate
            )

            try {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUserId)
                    .collection("pets")
                    .document(pet.petId)
                    .set(petMap)
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _isLoading.value = false
            onDone()
        }
    }



    // Upload 1 pet lên Firebase
    private fun uploadPetToFirebase(pet: PetEntity) {
        val userId = auth.currentUser?.uid ?: return

        val petMap = hashMapOf(
            "petId" to pet.petId,
            "userId" to pet.userId,
            "name" to pet.name,
            "species" to pet.species,
            "breed" to pet.breed,
            "color" to pet.color,
            "imageUrl" to pet.imageUrl,
            "birthDate" to pet.birthDate,
            "weightKg" to pet.weightKg,
            "sizeCm" to pet.sizeCm,
            "healthStatus" to pet.healthStatus,
            "adoptionDate" to pet.adoptionDate
        )

        db.collection("users")
            .document(userId)
            .collection("pets")
            .document(pet.petId)
            .set(petMap)
    }
    // Lấy pet theo ID
    fun getPetById(petId: String, onResult: (PetEntity?) -> Unit) {
        viewModelScope.launch {
            val pet = repository.getPetById(petId)
            onResult(pet)
        }
    }

    // Sync toàn bộ pets Room → Firebase
    fun syncAllPets() {
        viewModelScope.launch {
            _isLoading.value = true
            val allPets = repository.getAllPets()
            val userId = auth.currentUser?.uid ?: return@launch

            allPets.forEach { pet ->
                val petMap = hashMapOf(
                    "petId" to pet.petId,
                    "userId" to pet.userId,
                    "name" to pet.name,
                    "species" to pet.species,
                    "breed" to pet.breed,
                    "color" to pet.color,
                    "imageUrl" to pet.imageUrl,
                    "birthDate" to pet.birthDate,
                    "weightKg" to pet.weightKg,
                    "sizeCm" to pet.sizeCm,
                    "healthStatus" to pet.healthStatus,
                    "adoptionDate" to pet.adoptionDate
                )
                db.collection("users")
                    .document(userId)
                    .collection("pets")
                    .document(pet.petId)
                    .set(petMap)
            }

            _isLoading.value = false
        }
    }

    // Fetch pets từ Firebase về Room (optional)
    fun fetchPetsFromFirebaseToRoom() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                _isLoading.value = true

                val result = db.collection("users")
                    .document(userId)
                    .collection("pets")
                    .get()
                    .await()

                val petsFromFirebase = result.documents.mapNotNull { doc ->
                    PetEntity(
                        petId = doc.getString("petId") ?: return@mapNotNull null,
                        userId = doc.getString("userId") ?: userId, // ✅ Đảm bảo userId đúng
                        name = doc.getString("name") ?: "",
                        species = doc.getString("species") ?: "",
                        breed = doc.getString("breed") ?: "",
                        color = doc.getString("color"),
                        imageUrl = doc.getString("imageUrl"),
                        birthDate = doc.getLong("birthDate") ?: 0L,
                        weightKg = doc.getDouble("weightKg")?.toFloat() ?: 0f,
                        sizeCm = doc.getDouble("sizeCm")?.toFloat(),
                        healthStatus = doc.getString("healthStatus") ?: "",
                        adoptionDate = doc.getLong("adoptionDate")
                    )
                }

                // Lưu vào Room
                petsFromFirebase.forEach { repository.insertPet(it) }

                // Cập nhật state - chỉ lấy pets của user này
                _pets.value = repository.getPetsByUserId(userId)

                Log.d("PetViewModel", "Loaded ${petsFromFirebase.size} pets for user $userId")
            } catch (e: Exception) {
                Log.e("PetViewModel", "Error loading pets from Firebase", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadImageToStorage(context: Context, imageUri: Uri, onComplete: (String?) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = "pet_images/${System.currentTimeMillis()}.jpg"
        val imageRef = storageRef.child(fileName)

        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            if (inputStream == null) {
                onComplete(null)
                return
            }

            val bytes = inputStream.readBytes()
            inputStream.close()

            imageRef.putBytes(bytes)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        onComplete(downloadUrl.toString())
                    }.addOnFailureListener { onComplete(null) }
                }
                .addOnFailureListener {
                    onComplete(null)
                }
        } catch (e: Exception) {
            onComplete(null)
        }
    }
    fun deletePet(pet: PetEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // 1. Xóa khỏi Room
                repository.deletePet(pet)

                // 2. Xóa khỏi Firebase
                repository.deletePetFromFirestore(pet.petId)

                // 3. Cập nhật state để UI refresh ngay
                _pets.value = _pets.value.filter { it.petId != pet.petId }

                Log.d("PetViewModel", "Deleted pet: ${pet.name}")
            } catch (e: Exception) {
                Log.e("PetViewModel", "Error deleting pet", e)
            } finally {
                _isLoading.value = false
                onComplete()
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
        Log.d("PetViewModel", "Realtime listener removed")
    }

    fun loadPets() {
        viewModelScope.launch {
            try {
                // Lấy pets từ Room trước
                val localPets = repository.getAllPets()
                _pets.value = localPets

                // Đồng bộ từ Firebase
                val firebasePets = repository.getPetsFromFirebase()
                if (firebasePets.isNotEmpty()) {
                    firebasePets.forEach { repository.insertPet(it) }
                    _pets.value = repository.getAllPets()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}

// Factory
class PetViewModelFactory(private val repository: PetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class YourViewModel(private val auth: FirebaseAuth) : ViewModel() {
    val currentUser = mutableStateOf<FirebaseUser?>(auth.currentUser)

    // Logout
    fun logoutOnly() {
        auth.signOut()
        currentUser.value = null

    }
}
class YourViewModelFactory(private val auth: FirebaseAuth) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(YourViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return YourViewModel(auth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


