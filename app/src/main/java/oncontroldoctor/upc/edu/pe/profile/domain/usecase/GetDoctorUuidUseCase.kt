package oncontroldoctor.upc.edu.pe.profile.domain.usecase

import oncontroldoctor.upc.edu.pe.profile.domain.repository.ProfileRepository

class GetDoctorUuidUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<String?> {
        return repository.getDoctorUuid()
    }
}
