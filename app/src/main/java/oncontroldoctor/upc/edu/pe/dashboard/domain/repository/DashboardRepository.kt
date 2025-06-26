package oncontroldoctor.upc.edu.pe.dashboard.domain.repository

import oncontroldoctor.upc.edu.pe.dashboard.data.local.SubscriptionEntity
import oncontroldoctor.upc.edu.pe.dashboard.domain.model.PlanFull

interface DashboardRepository {
    suspend fun syncSubscriptionAndPlan(adminId: Long)
    suspend fun getLocalSubscription(): SubscriptionEntity?
    suspend fun getLocalPlan(): PlanFull?
    suspend fun getLocalPlanById(planId: Long): PlanFull?
}