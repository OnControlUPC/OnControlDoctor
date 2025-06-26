package oncontroldoctor.upc.edu.pe.dashboard.data.repository

import oncontroldoctor.upc.edu.pe.dashboard.data.local.SubscriptionEntity
import oncontroldoctor.upc.edu.pe.dashboard.data.local.dao.PlanDao
import oncontroldoctor.upc.edu.pe.dashboard.data.local.dao.SubscriptionDao
import oncontroldoctor.upc.edu.pe.dashboard.data.mapper.toDashboardDomain
import oncontroldoctor.upc.edu.pe.dashboard.data.mapper.toEntity
import oncontroldoctor.upc.edu.pe.dashboard.data.remote.DashboardService
import oncontroldoctor.upc.edu.pe.dashboard.domain.model.PlanFull
import oncontroldoctor.upc.edu.pe.dashboard.domain.repository.DashboardRepository
import oncontroldoctor.upc.edu.pe.profile.data.model.DoctorProfileRequest
import oncontroldoctor.upc.edu.pe.profile.data.model.toDomain
import oncontroldoctor.upc.edu.pe.profile.data.remote.ProfileService
import oncontroldoctor.upc.edu.pe.profile.domain.model.DoctorProfile
import oncontroldoctor.upc.edu.pe.profile.domain.repository.ProfileRepository
import oncontroldoctor.upc.edu.pe.shared.data.mappers.toEntity
import oncontroldoctor.upc.edu.pe.shared.data.remote.BaseService

class DashboardRepositoryImpl(
    private val service: DashboardService,
    private val subscriptionDao: SubscriptionDao,
    private val planDao: PlanDao
) : DashboardRepository, BaseService() {

    override suspend fun syncSubscriptionAndPlan(adminId: Long) {
        val subscription = authorizedCall { token ->
            service.getActiveSubscription(token, adminId)
        }.getOrElse { throw it }

        subscriptionDao.clearAll()
        subscriptionDao.insertSubscription(subscription.toEntity())

        val plan = authorizedCall { token ->
            service.getPlanById(token, subscription.planId)
        }.getOrElse { throw it }

        planDao.clearAll()
        planDao.insertPlan(plan.toEntity())
    }

    override suspend fun getLocalSubscription(): SubscriptionEntity? {
        return subscriptionDao.getActiveSubscription()
    }

    override suspend fun getLocalPlan(): PlanFull? {
        val subscription = subscriptionDao.getActiveSubscription()
        return subscription?.let {
            planDao.getPlanById(it.planId)?.toDashboardDomain()
        }
    }

    override suspend fun getLocalPlanById(planId: Long): PlanFull? {
        return planDao.getPlanById(planId)?.toDashboardDomain()
    }

}


