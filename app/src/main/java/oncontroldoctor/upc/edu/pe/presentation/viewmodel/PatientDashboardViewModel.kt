package oncontroldoctor.upc.edu.pe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import oncontroldoctor.upc.edu.pe.domain.model.Treatment

class PatientDashboardViewModel : ViewModel() {
    private val _treatments = MutableStateFlow<List<Treatment>>(emptyList())
    val treatments: StateFlow<List<Treatment>> = _treatments

    init {
        _treatments.value = listOf(
            Treatment(1, "Quimioterapia Fase 1", true),
            Treatment(2, "Examen Pre-operatorio", true),
            Treatment(3, "Radioterapia", true),
            Treatment(4, "Ciclo Finalizado", false)
        )
    }
}
