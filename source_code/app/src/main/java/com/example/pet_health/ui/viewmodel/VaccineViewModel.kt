package com.example.pet_health.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pet_health.data.entity.PetEntity
import com.example.pet_health.data.entity.VaccineEntity
import com.example.pet_health.data.local.datasources.VaccineLocalDataSource
import com.example.pet_health.data.remote.datasources.VaccineRemoteDataSource
import com.example.pet_health.data.repository.PetRepository
import com.example.pet_health.repository.VaccineRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pet_health.data.local.AppDatabase
import java.util.UUID

class VaccineViewModel(context: Context) : ViewModel() {

    private val repo: VaccineRepository
    private val petRepo: PetRepository // <--- THÊM: Repository để lấy danh sách Pet

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Danh sách Vaccine
    private val _vaccines = MutableStateFlow<List<VaccineEntity>>(emptyList())
    val vaccines = _vaccines.asStateFlow()

    // <--- THÊM: Danh sách Pet để hiển thị Filter/Dropdown
    private val _pets = MutableStateFlow<List<PetEntity>>(emptyList())
    val pets = _pets.asStateFlow()

    var isLoading = mutableStateOf(false)
    var success = mutableStateOf(false)

    // Lưu ID vừa tạo để UI biết đường chuyển trang
    var createdVaccineId = mutableStateOf<String?>(null)

    init {
        val database = AppDatabase.getDatabase(context)

        // Init Vaccine Repo
        val local = VaccineLocalDataSource(database.vaccineDao())
        val remote = VaccineRemoteDataSource()
        repo = VaccineRepository(local, remote)

        // <--- THÊM: Init Pet Repo
        petRepo = PetRepository(database)

        fetchVaccinesRealtime()
        fetchPets() // <--- THÊM: Gọi hàm lấy danh sách Pet
    }

    // 1. Lấy danh sách Pet của User hiện tại
    private fun fetchPets() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                // Lấy từ Room (dữ liệu đã được PetViewModel sync về trước đó)
                val petList = petRepo.getPetsByUserId(userId)
                _pets.value = petList
            }
        }
    }

    // 2. Lắng nghe dữ liệu Vaccine từ Firestore (Realtime)
    private fun fetchVaccinesRealtime() {
        db.collection("vaccines")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = snapshot.toObjects(VaccineEntity::class.java)
                    _vaccines.value = list
                }
            }
    }

    // 3. Thêm mới Vaccine
    fun addVaccine(
        petId: String,
        name: String,
        date: Long,
        clinic: String? = null,
        doseNumber: Int? = null,
        note: String? = null,
        imageUri: Uri? = null,
        nextDoseDate: Long? = null
    ) {
        viewModelScope.launch {
            isLoading.value = true
            // Reset trạng thái trước khi thêm
            success.value = false
            createdVaccineId.value = null

            try {
                // 1. Tự tạo ID tại đây
                val newId = UUID.randomUUID().toString()

                // 2. Truyền ID xuống Repository
                val result = repo.addVaccine(
                    id = newId, // Truyền ID vào
                    petId, name, date, clinic, doseNumber, note, imageUri, nextDoseDate
                )

                // 3. Nếu thành công, lưu ID lại để UI sử dụng
                if (result) {
                    createdVaccineId.value = newId
                    success.value = true
                } else {
                    success.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                success.value = false
            } finally {
                isLoading.value = false
            }
        }
    }
}