package oncontroldoctor.upc.edu.pe.profile.data.repository

import oncontroldoctor.upc.edu.pe.profile.data.model.DoctorProfileRequest
import oncontroldoctor.upc.edu.pe.profile.data.model.toDomain
import oncontroldoctor.upc.edu.pe.profile.data.remote.ProfileService
import oncontroldoctor.upc.edu.pe.profile.domain.model.DoctorProfile
import oncontroldoctor.upc.edu.pe.profile.domain.repository.ProfileRepository
import oncontroldoctor.upc.edu.pe.shared.data.remote.BaseService

import retrofit2.HttpException

class ProfileRepositoryImpl(
    private val service: ProfileService
): ProfileRepository, BaseService() {

    override suspend fun getDoctorUuid(): Result<String?> {
        return authorizedCall { token ->
            service.getDoctorUuid(token)
        }.fold(
            onSuccess = { Result.success(it.uuid) },
            onFailure = {
                if ((it as? HttpException)?.code() == 404) {
                    Result.success(null)
                } else {
                    Result.failure(Exception("Error getting UUID: ${it.message}"))
                }
            }
        )
    }

    override suspend fun getDoctorProfile(uuid: String): Result<DoctorProfile?> {
        return authorizedCall { token ->
            service.getDoctorProfileByUuid(token, uuid)
        }.fold(
            onSuccess = {
                val profile = it.toDomain()
                if (profile?.active == false) {
                    Result.failure(IllegalStateException("Doctor profile is inactive"))
                } else {
                    Result.success(profile)
                }
            },
            onFailure = {
                Result.failure(Exception("Error getting profile: ${it.message}"))
            }
        )
    }

    override suspend fun createDoctorProfile(request: DoctorProfileRequest): Result<Unit> {
        return authorizedCall { token ->
            service.createProfile(token, request)
        }
    }

}