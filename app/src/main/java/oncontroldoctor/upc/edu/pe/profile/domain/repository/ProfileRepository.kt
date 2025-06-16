package oncontroldoctor.upc.edu.pe.profile.domain.repository

import oncontroldoctor.upc.edu.pe.profile.data.model.DoctorProfileRequest
import oncontroldoctor.upc.edu.pe.profile.domain.model.DoctorProfile

interface ProfileRepository {
    suspend fun getDoctorUuid(token: String): String?
    suspend fun getDoctorProfile(uuid: String, token: String): DoctorProfile?
    suspend fun createDoctorProfile(token: String, request: DoctorProfileRequest): Boolean
}