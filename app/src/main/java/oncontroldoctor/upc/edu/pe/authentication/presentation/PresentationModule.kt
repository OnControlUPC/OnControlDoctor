package oncontroldoctor.upc.edu.pe.authentication.presentation

import android.content.SharedPreferences
import oncontroldoctor.upc.edu.pe.authentication.data.remote.AuthService
import oncontroldoctor.upc.edu.pe.authentication.data.repository.AuthRepositoryImpl
import oncontroldoctor.upc.edu.pe.authentication.domain.repository.AuthRepository
import oncontroldoctor.upc.edu.pe.authentication.domain.usecase.SignInUseCase
import oncontroldoctor.upc.edu.pe.authentication.domain.usecase.SignUpUseCase
import oncontroldoctor.upc.edu.pe.authentication.presentation.viewmodel.LoginViewModel
import oncontroldoctor.upc.edu.pe.authentication.presentation.viewmodel.RegisterViewModel
import oncontroldoctor.upc.edu.pe.profile.data.remote.ProfileService
import oncontroldoctor.upc.edu.pe.profile.data.repository.ProfileRepositoryImpl
import oncontroldoctor.upc.edu.pe.profile.domain.usecase.CreateDoctorProfileUseCase
import oncontroldoctor.upc.edu.pe.profile.domain.usecase.GetDoctorProfileUseCase
import oncontroldoctor.upc.edu.pe.profile.domain.usecase.GetDoctorUuidUseCase
import oncontroldoctor.upc.edu.pe.profile.presentation.viewmodel.CompleteProfileViewModel
import oncontroldoctor.upc.edu.pe.shared.data.remote.ApiConstants
import oncontroldoctor.upc.edu.pe.shared.data.remote.ApiConstants.BASE_URL
import oncontroldoctor.upc.edu.pe.treatment.data.remote.TreatmentService
import oncontroldoctor.upc.edu.pe.treatment.data.repository.TreatmentRepositoryImpl
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.GetDoctorPatientsUseCase
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.LinkDoctorPatientUseCase
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.SearchPatientsUseCase
import oncontroldoctor.upc.edu.pe.treatment.presentation.viewmodel.TreatmentViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PresentationModule {
    private fun getAuthService(): AuthService{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }

    private fun getAuthRepository(): AuthRepository{
        return AuthRepositoryImpl(getAuthService())
    }

    private fun getSignInUseCase(): SignInUseCase{
        return SignInUseCase(getAuthRepository())
    }

    fun getLoginViewModel(): LoginViewModel{
        return LoginViewModel(getSignInUseCase())
    }

    fun getRegisterViewModel(): RegisterViewModel{
        val repository = getAuthRepository()
        return RegisterViewModel(
            signUpUseCase = SignUpUseCase(repository),
            signInUseCase = SignInUseCase(repository)
        )
    }

    fun getCompleteProfileViewModel(): CompleteProfileViewModel{
        val repository = ProfileRepositoryImpl(getProfileService())
        return CompleteProfileViewModel(
            getDoctorUuidUseCase = GetDoctorUuidUseCase(repository),
            getDoctorProfileUseCase = GetDoctorProfileUseCase(repository),
            createDoctorProfileUseCase = CreateDoctorProfileUseCase(repository)
        )
    }

    fun getProfileService(): ProfileService{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProfileService::class.java)
    }

    fun getTreatmentViewModel(): TreatmentViewModel {
        val treatmentService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TreatmentService::class.java)

        val treatmentRepository = TreatmentRepositoryImpl(treatmentService)

        val searchPatientsUseCase = SearchPatientsUseCase(treatmentRepository)
        val linkDoctorPatientUseCase = LinkDoctorPatientUseCase(treatmentRepository)
        val getDoctorPatientsUseCase = GetDoctorPatientsUseCase(treatmentRepository)

        return TreatmentViewModel(
            searchPatientsUseCase,
            linkDoctorPatientUseCase,
            getDoctorPatientsUseCase
        )
    }
}