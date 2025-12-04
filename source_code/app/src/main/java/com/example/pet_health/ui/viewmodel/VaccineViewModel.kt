package com.example.pet_health.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pet_health.data.entity.VaccineEntity
import com.example.pet_health.data.local.datasources.VaccineLocalDataSource
import com.example.pet_health.data.remote.datasources.VaccineRemoteDataSource
import com.example.pet_health.repository.VaccineRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pet_health.data.local.AppDatabase
import java.util.UUID // <--- Nhớ thêm import này

class VaccineViewModel(context: Context) : ViewModel() {

    private val repo: VaccineRepository
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _vaccines = MutableStateFlow<List<VaccineEntity>>(emptyList())
    val vaccines = _vaccines.asStateFlow()

    var isLoading = mutableStateOf(false)
    var success = mutableStateOf(false)

    // <--- BIẾN MỚI: Lưu ID vừa tạo để UI biết đường chuyển trang
    var createdVaccineId = mutableStateOf<String?>(null)

    init {
        val database = AppDatabase.getDatabase(context)
        val local = VaccineLocalDataSource(database.vaccineDao())
        val remote = VaccineRemoteDataSource()
        repo = VaccineRepository(local, remote)

        fetchVaccinesRealtime()
    }

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
            try {
                // 1. Tự tạo ID tại đây
                val newId = UUID.randomUUID().toString()

                // 2. Truyền ID xuống Repository
                val result = repo.addVaccine(
                    id = newId, // <--- Truyền ID vào
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