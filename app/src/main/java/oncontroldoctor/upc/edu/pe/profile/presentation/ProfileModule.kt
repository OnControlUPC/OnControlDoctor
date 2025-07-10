package oncontroldoctor.upc.edu.pe.profile.presentation

import oncontroldoctor.upc.edu.pe.profile.data.remote.ProfileService
import oncontroldoctor.upc.edu.pe.profile.data.repository.ProfileRepositoryImpl
import oncontroldoctor.upc.edu.pe.profile.domain.repository.ProfileRepository
import oncontroldoctor.upc.edu.pe.profile.domain.usecase.CreateDoctorProfileUseCase
import oncontroldoctor.upc.edu.pe.profile.domain.usecase.GetDoctorProfileUseCase
import oncontroldoctor.upc.edu.pe.profile.domain.usecase.GetDoctorUuidUseCase
import oncontroldoctor.upc.edu.pe.profile.presentation.viewmodel.CompleteProfileViewModel
import oncontroldoctor.upc.edu.pe.profile.presentation.viewmodel.ProfileViewModelFactory
import oncontroldoctor.upc.edu.pe.shared.data.remote.ServiceFactory

object ProfileModule {

    private val service: ProfileService by lazy {
        ServiceFactory.create()
    }

    private val repository: ProfileRepository by lazy {
        ProfileRepositoryImpl(service)
    }

    private val getDoctorUuidUseCase: GetDoctorUuidUseCase by lazy {
        GetDoctorUuidUseCase(repository)
    }

    private val getDoctorProfileUseCase: GetDoctorProfileUseCase by lazy {
        GetDoctorProfileUseCase(repository)
    }

    private val createDoctorProfileUseCase: CreateDoctorProfileUseCase by lazy {
        CreateDoctorProfileUseCase(repository)
    }

    fun getCompleteProfileViewModel(): CompleteProfileViewModel {
        return CompleteProfileViewModel(
            getDoctorUuidUseCase = getDoctorUuidUseCase,
            getDoctorProfileUseCase = getDoctorProfileUseCase,
            createDoctorProfileUseCase = createDoctorProfileUseCase
        )
    }
    fun getProfileViewModelFactory(): ProfileViewModelFactory {
        return ProfileViewModelFactory(repository)
    }
}