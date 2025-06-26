package oncontroldoctor.upc.edu.pe.billing.presentation

import oncontroldoctor.upc.edu.pe.billing.data.remote.BillingService
import oncontroldoctor.upc.edu.pe.billing.data.repository.BillingRepositoryImpl
import oncontroldoctor.upc.edu.pe.billing.domain.repository.BillingRepository
import oncontroldoctor.upc.edu.pe.billing.domain.usecase.GetActiveSubscriptionUseCase
import oncontroldoctor.upc.edu.pe.billing.domain.usecase.GetPlansUseCase
import oncontroldoctor.upc.edu.pe.billing.domain.usecase.UseSubscriptionKeyUseCase
import oncontroldoctor.upc.edu.pe.billing.domain.usecase.ValidateSubscriptionKeyUseCase
import oncontroldoctor.upc.edu.pe.billing.presentation.viewmodel.BillingViewModel
import oncontroldoctor.upc.edu.pe.shared.data.remote.ServiceFactory


object BillingModule {

    private val service: BillingService by lazy {
        ServiceFactory.create()
    }

    private val repository: BillingRepository by lazy {
        BillingRepositoryImpl(service)
    }

    private val getPlansUseCase: GetPlansUseCase by lazy {
        GetPlansUseCase(repository)
    }

    private val useKeyUseCase: UseSubscriptionKeyUseCase by lazy {
        UseSubscriptionKeyUseCase(repository)
    }

    private val getActiveSubscriptionUseCase: GetActiveSubscriptionUseCase by lazy {
        GetActiveSubscriptionUseCase(repository)
    }

    private val validateKeyUseCase: ValidateSubscriptionKeyUseCase by lazy {
        ValidateSubscriptionKeyUseCase(repository)
    }


    fun provideBillingViewModel(): BillingViewModel {
        return BillingViewModel(
            getPlansUseCase = getPlansUseCase,
            validateKeyUseCase = validateKeyUseCase,
            useKeyUseCase = useKeyUseCase,
            getActiveSubscriptionUseCase = getActiveSubscriptionUseCase
        )
    }
}