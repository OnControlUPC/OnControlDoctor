package oncontroldoctor.upc.edu.pe.profile.domain.repository

import oncontroldoctor.upc.edu.pe.profile.data.model.DoctorProfileRequest
import oncontroldoctor.upc.edu.pe.profile.domain.model.DoctorProfile

interface ProfileRepository {
    suspend fun getDoctorUuid(): Result<String?>
    suspend fun getDoctorProfile(uuid: String): Result<DoctorProfile?>
    suspend fun createDoctorProfile(request: DoctorProfileRequest): Result<Unit>
}

