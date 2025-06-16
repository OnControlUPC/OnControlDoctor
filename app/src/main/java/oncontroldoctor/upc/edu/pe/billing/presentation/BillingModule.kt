package oncontroldoctor.upc.edu.pe.billing.presentation

import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ViewModelComponent
import oncontroldoctor.upc.edu.pe.billing.data.remote.BillingService
import oncontroldoctor.upc.edu.pe.billing.data.repository.BillingRepositoryImpl
import oncontroldoctor.upc.edu.pe.billing.domain.repository.BillingRepository
import oncontroldoctor.upc.edu.pe.billing.domain.usecase.GetActiveSubscriptionUseCase
import oncontroldoctor.upc.edu.pe.billing.domain.usecase.GetPlansUseCase
import oncontroldoctor.upc.edu.pe.billing.domain.usecase.UseSubscriptionKeyUseCase
import oncontroldoctor.upc.edu.pe.shared.data.remote.ApiConstants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BillingModule{

    fun provideBillingService(): BillingService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BillingService::class.java)
    }

    fun provideBillingRepository(
        billingService: BillingService
    ): BillingRepository {
        return BillingRepositoryImpl(billingService)
    }

    fun provideGetPlanUseCase(repository: BillingRepository): GetPlansUseCase{
        return GetPlansUseCase(repository)
    }

    fun provideUseKeyUseCase(repository: BillingRepository): UseSubscriptionKeyUseCase{
        return UseSubscriptionKeyUseCase(repository)
    }

    fun provideGetActiveSubscriptionUseCase(repository: BillingRepository): GetActiveSubscriptionUseCase{
        return GetActiveSubscriptionUseCase(repository)
    }
}