@Entity(
    tableName = "vaccines",
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
data class Vaccine(
    @PrimaryKey val vaccineId: String,
    val petId: String,
    val name: String,
    val date: Long,
    val clinic: String? = null,
    val doseNumber: Int? = null,
    val note: String? = null,
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val nextDoseDate: Long? = null
)
