package com.example.data.progres

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProgresLoginRequest(
    @Json(name = "username") val username: String, // Matricule / Bac Year + Roll No (e.g. 202131012345)
    @Json(name = "password") val password: String  // WebEtu Password / Bac Code
)

@JsonClass(generateAdapter = true)
data class ProgresAuthResponse(
    @Json(name = "token") val token: String? = null,
    @Json(name = "id") val id: Long? = null,
    @Json(name = "nomArabe") val nomAr: String? = null,
    @Json(name = "prenomArabe") val prenomAr: String? = null,
    @Json(name = "nomLatin") val nomFr: String? = null,
    @Json(name = "prenomLatin") val prenomFr: String? = null,
    @Json(name = "dateNaissance") val birthDate: String? = null,
    @Json(name = "matricule") val matricule: String? = null,
    @Json(name = "etablissement") val university: String? = null,
    @Json(name = "faculte") val faculty: String? = null,
    @Json(name = "domaine") val domain: String? = null,
    @Json(name = "filiere") val specialty: String? = null,
    @Json(name = "niveau") val level: String? = null,
    @Json(name = "groupe") val group: String? = null,
    @Json(name = "section") val section: String? = null
)

@JsonClass(generateAdapter = true)
data class ProgresModuleGrade(
    @Json(name = "libelleMatiere") val moduleName: String,
    @Json(name = "codeMatiere") val moduleCode: String? = null,
    @Json(name = "coefficient") val coefficient: Double = 1.0,
    @Json(name = "credit") val credits: Int = 1,
    @Json(name = "noteCc") val scoreCc: Double? = null,
    @Json(name = "noteTp") val scoreTp: Double? = null,
    @Json(name = "noteExamen") val scoreExam: Double? = null,
    @Json(name = "moyenne") val finalAverage: Double? = null,
    @Json(name = "ueType") val unitType: String = "Fondamentale", // Fondamentale, Methodologique, Decouverte, Transversale
    @Json(name = "acquise") val isAcquired: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ProgresSemesterGrades(
    @Json(name = "semestre") val semesterNumber: Int,
    @Json(name = "anneeAcademique") val academicYear: String,
    @Json(name = "moyenneSemestre") val semesterAverage: Double,
    @Json(name = "totalCredits") val totalCreditsAcquired: Int,
    @Json(name = "modules") val modules: List<ProgresModuleGrade> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ProgresStudentCardInfo(
    val studentName: String,
    val matricule: String,
    val university: String,
    val faculty: String,
    val specialty: String,
    val level: String,
    val academicYear: String,
    val group: String,
    val section: String,
    val s1Average: Double? = null,
    val s2Average: Double? = null,
    val annualAverage: Double? = null,
    val totalCreditsAcquired: Int = 0,
    val isRegistered: Boolean = true
)

sealed interface ProgresSyncState {
    object Idle : ProgresSyncState
    object Connecting : ProgresSyncState
    data class Success(
        val studentInfo: ProgresStudentCardInfo,
        val importedGradesCount: Int,
        val message: String
    ) : ProgresSyncState
    data class Error(val errorMessage: String) : ProgresSyncState
}
