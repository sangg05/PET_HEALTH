class PetRepository(private val petDao: PetDao) {
    suspend fun addPet(pet: Pet) = petDao.insertPet(pet)
    suspend fun getAllPets() = petDao.getAllPets()
}