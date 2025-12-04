package com.example.pet_health.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pet_health.data.entity.HealthRecordEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HealthRecordViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    private val _healthRecords = MutableStateFlow<List<HealthRecordEntity>>(emptyList())
    val healthRecords = _healthRecords.asStateFlow()

    init {
        setupRealtimeListener()
    }

    // Realtime listener - tự động cập nhật khi có thay đổi
    private fun setupRealtimeListener() {
        val userId = auth.currentUser?.uid ?: return

        listenerRegistration = db.collection("users")
            .document(userId)
            .collection("health_records")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(HealthRecordEntity::class.java)
                } ?: emptyList()


                _healthRecords.value = list.sortedByDescending { it.date }
            }
    }

    fun addRecord(record: HealthRecordEntity) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("health_records")
            .document(record.recordId)
            .set(record)

    }

    fun deleteRecord(recordId: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("health_records")
            .document(recordId)
            .delete()

    }

    // Chỉ dùng khi cần load lại thủ công
    fun loadRecords() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("health_records")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(HealthRecordEntity::class.java)
                }
                _healthRecords.value = list.sortedByDescending { it.date }
            }
    }


    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}

class HealthRecordViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HealthRecordViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}