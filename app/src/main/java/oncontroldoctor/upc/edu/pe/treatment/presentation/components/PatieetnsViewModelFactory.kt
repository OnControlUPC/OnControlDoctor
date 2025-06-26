package oncontroldoctor.upc.edu.pe.treatment.presentation.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import oncontroldoctor.upc.edu.pe.treatment.domain.repository.TreatmentRepository
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.ActivatePatientLinkUseCase
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.PatientsViewModel

class PatientsViewModelFactory(
    private val repository: TreatmentRepository,
    private val activateLinkUseCase: ActivatePatientLinkUseCase

) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PatientsViewModel(
                repository = repository,
                activateLinkUseCase = activateLinkUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}