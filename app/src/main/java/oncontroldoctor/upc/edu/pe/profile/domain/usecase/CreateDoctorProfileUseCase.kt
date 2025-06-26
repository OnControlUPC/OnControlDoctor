package oncontroldoctor.upc.edu.pe.profile.domain.usecase

import oncontroldoctor.upc.edu.pe.profile.data.model.DoctorProfileRequest
import oncontroldoctor.upc.edu.pe.profile.domain.repository.ProfileRepository

class CreateDoctorProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(request: DoctorProfileRequest): Result<Unit> {
        return repository.createDoctorProfile(request)
    }
}
