package oncontroldoctor.upc.edu.pe.profile.domain.usecase

import oncontroldoctor.upc.edu.pe.profile.domain.model.DoctorProfile
import oncontroldoctor.upc.edu.pe.profile.domain.repository.ProfileRepository

class GetDoctorProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(uuid: String, token: String): DoctorProfile? {
        return repository.getDoctorProfile(uuid, token)
    }
}