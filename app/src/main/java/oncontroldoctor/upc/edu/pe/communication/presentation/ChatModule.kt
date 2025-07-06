package oncontroldoctor.upc.edu.pe.communication.presentation

import oncontroldoctor.upc.edu.pe.communication.data.remote.PatientsLIstService
import oncontroldoctor.upc.edu.pe.communication.data.repository.ChatRepositoryImpl
import oncontroldoctor.upc.edu.pe.communication.domain.repository.ChatRepository
import oncontroldoctor.upc.edu.pe.communication.presentation.components.ChatViewModelFactory
import oncontroldoctor.upc.edu.pe.shared.data.remote.ServiceFactory

object ChatModule {
    fun provideChatService(): PatientsLIstService{
        return ServiceFactory.create()
    }
    fun provideChatRepository(): ChatRepository{
        return ChatRepositoryImpl(provideChatService())
    }

    fun provideChatViewModelFactory(): ChatViewModelFactory {
        val repository = provideChatRepository()
        return ChatViewModelFactory(repository)
    }

}