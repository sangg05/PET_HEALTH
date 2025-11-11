@Entity(
    tableName = "pet_images",
    foreignKeys = [
        ForeignKey(
            entity = Pet::class,
            parentColumns = ["petId"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("petId")]
)
data class PetImage(
    @PrimaryKey val imageId: String,
    val petId: String,
    val imageUrl: String,
    val description: String? = null
)
