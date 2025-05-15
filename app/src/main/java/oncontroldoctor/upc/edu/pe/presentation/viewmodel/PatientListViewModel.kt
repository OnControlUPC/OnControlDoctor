package oncontroldoctor.upc.edu.pe.presentation.viewmodel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch
import oncontroldoctor.upc.edu.pe.domain.model.Patient

class PatientListViewModel: ViewModel() {
    private val _patients = MutableStateFlow<List<Patient>>(emptyList())
    val patients: StateFlow<List<Patient>> = _patients

    init {
        loadPatients()
    }

    private fun loadPatients() {
        // Simulación de datos (luego lo reemplazas por tu repo/API)
        viewModelScope.launch {
            val dummyPatients = listOf(
                Patient(1, "María Ramírez", "70812455"),
                Patient(2, "Carlos Fernández", "70123456"),
                Patient(3, "Lucía Gamarra", "70987654")
            )
            _patients.value = dummyPatients
        }
    }
}