package oncontroldoctor.upc.edu.pe.authentication.presentation

import oncontroldoctor.upc.edu.pe.authentication.data.remote.AuthService
import oncontroldoctor.upc.edu.pe.authentication.data.repository.AuthRepositoryImpl
import oncontroldoctor.upc.edu.pe.authentication.domain.repository.AuthRepository
import oncontroldoctor.upc.edu.pe.authentication.domain.usecase.SignInUseCase
import oncontroldoctor.upc.edu.pe.authentication.domain.usecase.SignInWithGoogleUseCase
import oncontroldoctor.upc.edu.pe.authentication.domain.usecase.SignUpUseCase
import oncontroldoctor.upc.edu.pe.authentication.presentation.viewmodel.LoginViewModel
import oncontroldoctor.upc.edu.pe.authentication.presentation.viewmodel.RegisterViewModel

import oncontroldoctor.upc.edu.pe.shared.data.remote.ServiceFactory

object AuthModule {
    private val service: AuthService by lazy {
        ServiceFactory.create()
    }

    private val repository: AuthRepository by lazy {
        AuthRepositoryImpl(service)
    }

    private val signInUseCase: SignInUseCase by lazy {
        SignInUseCase(repository)
    }

    private val signInWithGoogleUseCase: SignInWithGoogleUseCase by lazy {
        SignInWithGoogleUseCase(repository)
    }

    private val signUpUseCase: SignUpUseCase by lazy {
        SignUpUseCase(repository)
    }

    fun provideLoginViewModel(): LoginViewModel {
        return LoginViewModel(
            signInUseCase = signInUseCase,
            signInWithGoogleUseCase = signInWithGoogleUseCase
        )
    }

    fun provideRegisterViewModel(): RegisterViewModel {
        return RegisterViewModel(
            signUpUseCase = signUpUseCase
        )
    }
}