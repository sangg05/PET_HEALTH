package com.example.pet_health.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pet_health.data.entity.UserEntity
import com.example.pet_health.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class UserViewModel(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
) : ViewModel() {


    data class UserInfo(
        val name: String = "",
        val birthDate: String = "",
        val gender: String = "Nữ",
        val phone: String = ""
    )

    // State user để UI quan sát
    var userInfo = mutableStateOf(UserInfo())
        private set

    init {
        loadUser()
    }

    // Lấy thông tin user hiện tại từ Firestore
    private fun loadUser() {
        val currentUserId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            val user = userRepository.getUserById(currentUserId)
            user?.let {
                userInfo.value = UserInfo(
                    name = it.name,
                    birthDate = it.birthDate ?: "",
                    gender = it.gender ?: "Nữ",
                    phone = it.phone ?: ""
                )
            }
        }
    }

    fun updateUserInfo(name: String, birthDate: String?, gender: String?, phone: String?) {
        val currentUserId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            // 1. Cập nhật Firestore & Room thông qua repository
            userRepository.updateUserInfo(name, birthDate, gender, phone)

            // 2. Cập nhật state để UI tự refresh
            userInfo.value = UserInfo(
                name = name,
                birthDate = birthDate ?: "",
                gender = gender ?: "Nữ",
                phone = phone ?: ""
            )
        }
    }
}
class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    // State để Compose quan sát
    var rememberMe = mutableStateOf(repository.isRememberMe())
        private set
    var savedEmail = mutableStateOf(repository.getRememberedEmail() ?: "")
        private set
    var savedPassword = mutableStateOf(repository.getRememberedPassword() ?: "")
        private set

    fun onLogin(email: String, password: String, remember: Boolean) {
        viewModelScope.launch {
            if (remember) {
                repository.setRememberMe(email, password, true)
            } else {
                repository.setRememberMe("", "", false) // xóa thông tin
            }
        }
    }
}
class LoginViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
