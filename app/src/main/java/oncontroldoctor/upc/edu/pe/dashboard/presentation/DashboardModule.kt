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
import oncontroldoctor.upc.edu.pe.treatment.data.remote.TreatmentService
import oncontroldoctor.upc.edu.pe.treatment.data.repository.TreatmentRepositoryImpl
import oncontroldoctor.upc.edu.pe.treatment.domain.repository.TreatmentRepository
import oncontroldoctor.upc.edu.pe.treatment.domain.usecase.GetCalendarAppointmentsUseCase

object DashboardModule {

    fun provideDashboardService(): DashboardService = ServiceFactory.create()
    fun provideTreatmentService(): TreatmentService = ServiceFactory.create()

    fun provideDashboardRepository(
        service: DashboardService,
        subscriptionDao: SubscriptionDao,
        planDao: PlanDao
    ): DashboardRepository {
        return DashboardRepositoryImpl(service, subscriptionDao, planDao)
    }

    fun provideTreatmentRepository(
        service: TreatmentService
    ): TreatmentRepository {
        return TreatmentRepositoryImpl(service)
    }

    fun provideSyncUseCase(repository: DashboardRepository): SyncSubscriptionAndPlanUseCase {
        return SyncSubscriptionAndPlanUseCase(repository)
    }

    fun provideGetSubscriptionUseCase(repository: DashboardRepository): GetLocalSubscriptionUseCase {
        return GetLocalSubscriptionUseCase(repository)
    }

    fun provideGetPlanUseCase(repository: DashboardRepository): GetLocalPlanUseCase {
        return GetLocalPlanUseCase(repository)
    }

    fun provideGetCalendarAppointmentsUseCase(service: TreatmentService): GetCalendarAppointmentsUseCase {
        return GetCalendarAppointmentsUseCase(service)
    }

    fun provideViewModel(
        subscriptionDao: SubscriptionDao,
        planDao: PlanDao
    ): DashboardViewModel {
        val dashboardService = provideDashboardService()
        val dashboardRepository = provideDashboardRepository(dashboardService, subscriptionDao, planDao)

        val treatmentService = provideTreatmentService()
        val treatmentRepository = provideTreatmentRepository(treatmentService)

        return DashboardViewModel(
            syncSubscriptionAndPlanUseCase = provideSyncUseCase(dashboardRepository),
            getLocalSubscriptionUseCase = provideGetSubscriptionUseCase(dashboardRepository),
            getLocalPlanUseCase = provideGetPlanUseCase(dashboardRepository),
            getCalendarAppointmentsUseCase = provideGetCalendarAppointmentsUseCase(treatmentService),
            treatmentService = treatmentService
        )
    }

}
