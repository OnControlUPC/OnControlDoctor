package oncontroldoctor.upc.edu.pe.profile.data.repository

import android.content.SharedPreferences
import oncontroldoctor.upc.edu.pe.profile.data.model.DoctorProfileRequest
import oncontroldoctor.upc.edu.pe.profile.data.model.toDomain
import oncontroldoctor.upc.edu.pe.profile.data.remote.ProfileService
import oncontroldoctor.upc.edu.pe.profile.domain.model.DoctorProfile
import oncontroldoctor.upc.edu.pe.profile.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val service: ProfileService,
): ProfileRepository {
    override suspend fun getDoctorUuid(token: String): String? {
        val response = service.getDoctorUuid("Bearer $token")
        if (response.isSuccessful) {
            return response.body()?.uuid
        }

        return when (response.code()) {
            404 -> null
            else -> throw Exception("Error: ${response.code()} - ${response.message()}")
        }

    }

    override suspend fun getDoctorProfile(
        uuid: String,
        token: String
    ): DoctorProfile? {
        val response = service.getDoctorProfileByUuid("Bearer $token", uuid)

        if(response.isSuccessful){
            val profile = response.body()?.toDomain()

            if(profile?.active == false){
                throw IllegalStateException("Doctor profile is inactive")
            }

            return profile
        }
        return null
    }

    override suspend fun createDoctorProfile(
        token: String,
        request: DoctorProfileRequest
    ): Boolean{
        val response = service.createProfile("Bearer $token", request)
        return response.isSuccessful
    }

}