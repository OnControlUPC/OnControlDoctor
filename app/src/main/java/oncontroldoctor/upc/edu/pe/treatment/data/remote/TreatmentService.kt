package oncontroldoctor.upc.edu.pe.treatment.data.remote

import oncontroldoctor.upc.edu.pe.treatment.data.model.DoctorPatientLinkDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.DoctorPatientLinkRequestDto
import oncontroldoctor.upc.edu.pe.treatment.data.model.PatientDto
import retrofit2.Response
import retrofit2.http.*

interface TreatmentService {

    // Buscar pacientes por nombre o apellido
    @GET("api/v1/patients/search")
    suspend fun searchPatients(
        @Header("Authorization") token: String,
        @Query("query") query: String
    ): Response<List<PatientDto>>

    // Enlazar un paciente con un doctor
    @POST("api/v1/doctor-patient-links")
    suspend fun linkDoctorPatient(
        @Header("Authorization") token: String,
        @Body request: DoctorPatientLinkRequestDto
    ): Response<Unit>

    // Obtener vínculos de un doctor con pacientes
    @GET("api/v1/doctor-patient-links/doctor/{doctorUuid}")
    suspend fun getDoctorPatients(
        @Header("Authorization") token: String,
        @Path("doctorUuid") doctorUuid: String
    ): Response<List<DoctorPatientLinkDto>>
}
