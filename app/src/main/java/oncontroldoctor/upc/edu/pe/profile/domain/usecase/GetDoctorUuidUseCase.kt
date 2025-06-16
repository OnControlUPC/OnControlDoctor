package oncontroldoctor.upc.edu.pe.profile.domain.usecase

import oncontroldoctor.upc.edu.pe.profile.domain.repository.ProfileRepository

class GetDoctorUuidUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(token: String): String? {

        return repository.getDoctorUuid(token)
    }
}