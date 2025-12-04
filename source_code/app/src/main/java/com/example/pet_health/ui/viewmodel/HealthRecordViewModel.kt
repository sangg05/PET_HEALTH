package com.example.pet_health.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pet_health.data.entity.HealthRecordEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HealthRecordViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _healthRecords = MutableStateFlow<List<HealthRecordEntity>>(emptyList())
    val healthRecords = _healthRecords.asStateFlow()

    init {
        loadRecords()
    }

    fun loadRecords() {
        db.collection("health_records")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(HealthRecordEntity::class.java)
                }
                _healthRecords.value = list
            }
    }

    fun addRecord(record: HealthRecordEntity) {
        db.collection("health_records")
            .document(record.recordId)
            .set(record)
            .addOnSuccessListener { loadRecords() }
    }

    fun deleteRecord(recordId: String) {
        db.collection("health_records")
            .document(recordId)
            .delete()
            .addOnSuccessListener { loadRecords() }
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

