package oncontroldoctor.upc.edu.pe.treatment.presentation

import oncontroldoctor.upc.edu.pe.shared.data.remote.ServiceFactory
import oncontroldoctor.upc.edu.pe.treatment.data.remote.TreatmentService
import oncontroldoctor.upc.edu.pe.treatment.data.repository.TreatmentRepositoryImpl
import oncontroldoctor.upc.edu.pe.treatment.domain.repository.TreatmentRepository
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.ActivatePatientLinkUseCase
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.CancelPatientRequestUseCase
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.CheckPatientConnectionUseCase
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.InvitePatientUseCase
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.SearchPatientsUseCase
import oncontroldoctor.upc.edu.pe.treatment.presentation.components.PatientsViewModelFactory
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.PatientProfileViewModelFactory
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.PatientSearchViewModel
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.PatientsViewModel

object TreatmentModule {

    fun provideTreatmentService(): TreatmentService {
        return ServiceFactory.create()
    }

    fun provideTreatmentRepository(): TreatmentRepository {
        return TreatmentRepositoryImpl(provideTreatmentService())
    }

    fun providePatientRepository(): TreatmentRepository {
        return TreatmentRepositoryImpl(provideTreatmentService())
    }

    fun providePatientsViewModelFactory(): PatientsViewModelFactory {
        val repository = provideTreatmentRepository()
        val activateLinkUseCase = ActivatePatientLinkUseCase(repository)
        return PatientsViewModelFactory(
            repository = repository,
            activateLinkUseCase = activateLinkUseCase
        )
    }
    fun providePatientProfileViewModelFactory(patientUuid: String): PatientProfileViewModelFactory {
        val repository = provideTreatmentRepository()
        return PatientProfileViewModelFactory(patientUuid, repository)
    }

    fun getPatientsViewModel(): PatientsViewModel {
        val repository = provideTreatmentRepository()
        return PatientsViewModel(
            providePatientRepository(),
            activateLinkUseCase = ActivatePatientLinkUseCase(repository)
        )
    }

    fun getPatientSearchViewModel(): PatientSearchViewModel {
        val repository = provideTreatmentRepository()
        return PatientSearchViewModel(
            searchPatientsUseCase = SearchPatientsUseCase(repository),
            checkConnectionUseCase = CheckPatientConnectionUseCase(repository),
            invitePatientUseCase = InvitePatientUseCase(repository),
            cancelRequestUseCase = CancelPatientRequestUseCase(repository),
            activateLinkUseCase = ActivatePatientLinkUseCase(repository)
        )
    }
}
