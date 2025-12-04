    package com.example.pet_health.ui.screens

    import android.icu.util.TimeUnit
    import android.net.Uri
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.ArrowBack
    import androidx.compose.material.icons.filled.Edit
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.navigation.NavController
    import androidx.compose.material3.TopAppBar
    import androidx.compose.material3.TopAppBarDefaults
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.material.icons.filled.Delete
    import androidx.compose.material.icons.filled.Home
    import androidx.compose.material.icons.filled.Notifications
    import androidx.compose.material.icons.filled.Person
    import androidx.lifecycle.viewmodel.compose.viewModel
    import coil.compose.rememberAsyncImagePainter
    import com.example.pet_health.data.entity.PetEntity
    import com.example.pet_health.data.repository.PetRepository
    import com.example.pet_health.ui.viewmodel.PetViewModel
    import com.example.pet_health.ui.viewmodel.PetViewModelFactory


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PetProfileScreen(pet: PetEntity, navController: NavController,repository: PetRepository)
    {
        val lightPink = Color(0xFFFFD2FC)
        val petViewModel: PetViewModel = viewModel(
            factory = PetViewModelFactory(repository)
        )


        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Hồ sơ thông tin", fontWeight = FontWeight.Bold, color = Color.Black) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = lightPink)
                )
            },
            bottomBar = {
                // Bottom bar giống PetListScreen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color.White),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Home, "Trang chủ", tint = Color(0xFF6200EE), modifier = Modifier.size(32.dp))
                    Icon(Icons.Default.Notifications, "Thông báo", tint = Color.LightGray, modifier = Modifier.size(32.dp))
                    Icon(Icons.Default.Person, "Hồ sơ", tint = Color.LightGray, modifier = Modifier.size(32.dp))
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))
                        )
                    )
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ảnh thú cưng với background trắng oval
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(90.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(pet.imageUrl ?: ""),
                        contentDescription = pet.name,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Thông tin chính
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(pet.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Loài: ${pet.species}", fontSize = 20.sp, color = Color.Gray)
                            if (!pet.color.isNullOrEmpty()) {
                                Text("Màu sắc: ${pet.color}", fontSize = 20.sp, color = Color(0xFFD87ED6))
                            }
                        }

                        // Nút Edit + Delete
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {IconButton(onClick = {
                            // Tính age từ birthDate
                            val ageYears = if (pet.birthDate > 0) {
                                ((System.currentTimeMillis() - pet.birthDate) / (1000L * 60 * 60 * 24 * 365)).toInt()
                            } else 0

                            // Format adoption date
                            val adoptionDateStr = pet.adoptionDate?.let { millis ->
                                if (millis > 0) {
                                    val cal = java.util.Calendar.getInstance()
                                    cal.timeInMillis = millis
                                    "%02d/%02d/%04d".format(
                                        cal.get(java.util.Calendar.DAY_OF_MONTH),
                                        cal.get(java.util.Calendar.MONTH) + 1,
                                        cal.get(java.util.Calendar.YEAR)
                                    )
                                } else ""
                            } ?: ""

                            // Navigate với tất cả parameters
                            navController.navigate(
                                "add_pet?" +
                                        "editMode=true&" +
                                        "petId=${pet.petId}&" +
                                        "initName=${Uri.encode(pet.name)}&" +
                                        "initType=${Uri.encode(pet.species)}&" +
                                        "initAge=$ageYears&" +
                                        "initColor=${Uri.encode(pet.color ?: "")}&" +
                                        "initWeight=${pet.weightKg}&" +
                                        "initHeight=${pet.sizeCm ?: 0f}&" +
                                        "initAdoptionDate=${Uri.encode(adoptionDateStr)}&" +
                                        "initImageUri=${Uri.encode(pet.imageUrl ?: "")}"
                            )
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black)
                        }

                            IconButton(onClick = {
                                 petViewModel.deletePet(pet)
                                navController.popBackStack()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Thông tin cơ bản
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Thông tin cơ bản",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow(label = "Tuổi:", value = calculateAge(pet.birthDate))
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow(label = "Cân nặng:", value = "${pet.weightKg} kg")
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow(label = "Kích thước:", value = "${pet.sizeCm} cm")
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow(label = "Ngày nhận nuôi:", value = pet.adoptionDate?.let { formatDate(it) } ?: "")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    @Composable
    fun InfoRow(label: String, value: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 18.sp, color = Color.Gray)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        }
    }
    fun calculateAge(birthMillis: Long): String {
        if (birthMillis <= 0) return "chưa có"
        val now = System.currentTimeMillis()
        val diffMillis = now - birthMillis
        val years = (diffMillis / (1000L * 60 * 60 * 24 * 365)).toInt()
        return "$years tuổi"
    }
    fun formatDate(millis: Long): String {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = millis
        val day = cal.get(java.util.Calendar.DAY_OF_MONTH)
        val month = cal.get(java.util.Calendar.MONTH) + 1
        val year = cal.get(java.util.Calendar.YEAR)
        return "%02d/%02d/%04d".format(day, month, year)
    }

