package oncontroldoctor.upc.edu.pe.dashboard.presentation

import oncontroldoctor.upc.edu.pe.dashboard.data.local.dao.PlanDao
import oncontroldoctor.upc.edu.pe.dashboard.data.local.dao.SubscriptionDao
import oncontroldoctor.upc.edu.pe.dashboard.data.remote.DashboardService
import oncontroldoctor.upc.edu.pe.dashboard.data.repository.DashboardRepositoryImpl
import oncontroldoctor.upc.edu.pe.dashboard.domain.repository.DashboardRepository
import oncontroldoctor.upc.edu.pe.dashboard.domain.usecase.GetLocalPlanUseCase
import oncontroldoctor.upc.edu.pe.dashboard.domain.usecase.GetLocalSubscriptionUseCase
import oncontroldoctor.upc.edu.pe.dashboard.domain.usecase.SyncSubscriptionAndPlanUseCase
import oncontroldoctor.upc.edu.pe.dashboard.presentation.viewmodel.DashboardViewModel
import oncontroldoctor.upc.edu.pe.shared.data.remote.ServiceFactory

object DashboardModule {
    fun provideService(): DashboardService{
        return ServiceFactory.create()
    }
    fun provideRepository(
        service: DashboardService,
        subscriptionDao: SubscriptionDao,
        planDao: PlanDao

    ): DashboardRepository{
        return DashboardRepositoryImpl(service, subscriptionDao, planDao)
    }

    fun provideSyncUseCase(repository: DashboardRepository): SyncSubscriptionAndPlanUseCase{
        return SyncSubscriptionAndPlanUseCase(repository)
    }

    fun provideGetSubscriptionUseCase(repository: DashboardRepository): GetLocalSubscriptionUseCase {
        return GetLocalSubscriptionUseCase(repository)
    }

    fun provideGetPlanUseCase(repository: DashboardRepository): GetLocalPlanUseCase {
        return GetLocalPlanUseCase(repository)
    }

    fun provideViewModel(
        subscriptionDao: SubscriptionDao,
        planDao: PlanDao
    ): DashboardViewModel {
        val service = provideService()
        val repository = provideRepository(service, subscriptionDao, planDao)

        return DashboardViewModel(
            syncSubscriptionAndPlanUseCase = provideSyncUseCase(repository),
            getLocalSubscriptionUseCase = provideGetSubscriptionUseCase(repository),
            getLocalPlanUseCase = provideGetPlanUseCase(repository)
        )
    }

}