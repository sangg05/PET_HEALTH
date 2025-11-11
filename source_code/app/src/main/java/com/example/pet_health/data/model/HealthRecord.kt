@Entity(
    tableName = "health_records",
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
data class HealthRecord(
    @PrimaryKey val recordId: String,
    val petId: String,
    val date: Long,
    val symptom: String? = null,
    val diagnosis: String? = null,
    val prescription: String? = null,
    val weight: Float? = null,
    val height: Float? = null,
    val alert: Boolean = false
)
