package oncontroldoctor.upc.edu.pe.treatment.presentation

import oncontroldoctor.upc.edu.pe.treatment.data.remote.TreatmentService
import oncontroldoctor.upc.edu.pe.treatment.data.repository.TreatmentRepositoryImpl
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.*
import retrofit2.Retrofit

object TreatmentModule {
    fun provideTreatmentUseCases(retrofit: Retrofit): Triple<SearchPatientsUseCase, LinkDoctorPatientUseCase, GetDoctorPatientsUseCase> {
        val service = retrofit.create(TreatmentService::class.java)
        val repository = TreatmentRepositoryImpl(service)
        return Triple(
            SearchPatientsUseCase(repository),
            LinkDoctorPatientUseCase(repository),
            GetDoctorPatientsUseCase(repository)
        )
    }
}
