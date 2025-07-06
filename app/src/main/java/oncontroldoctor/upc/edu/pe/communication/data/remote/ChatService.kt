package oncontroldoctor.upc.edu.pe.communication.data.remote

import android.annotation.SuppressLint
import com.google.gson.Gson
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import oncontroldoctor.upc.edu.pe.communication.data.model.ChatMessage
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage

class ChatService(
    private val baseUrl: String,
    private val token: String,
    private val doctorUuid: String,
    private val patientUuid: String,
) {

    private lateinit var stompClient: StompClient
    private val gson = Gson()
    private var topicDisposable: Disposable? = null

    private val _incomingMessages = PublishProcessor.create<ChatMessage>()
    val incomingMessages: Flowable<ChatMessage> = _incomingMessages

    @SuppressLint("CheckResult")
    fun connect() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, baseUrl)
        val headers = listOf(StompHeader("Authorization", token))
        stompClient.connect(headers)

        stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .subscribe { lifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> {
                        println("STOMP Conectado: ${lifecycleEvent.message}")
                        subscribeToChatTopic()
                    }
                    LifecycleEvent.Type.ERROR -> {
                        println("STOMP Error: ${lifecycleEvent.exception?.message}")
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        println("STOMP Desconectado: ${lifecycleEvent.message}")
                    }
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                        println("STOMP Heartbeat fallido: ${lifecycleEvent.message}")
                    }
                }
            }
    }

    private fun subscribeToChatTopic() {
        val destination = "/topic/chat.$doctorUuid.$patientUuid"

        topicDisposable = stompClient.topic(destination)
            .subscribeOn(Schedulers.io())
            .map { stompMessage: StompMessage ->
                gson.fromJson(stompMessage.payload, ChatMessage::class.java)
            }
            .subscribe({ chatMessage: ChatMessage ->
                _incomingMessages.onNext(chatMessage)
            }, { throwable: Throwable ->
                println("Error al suscribirse al topic $destination: ${throwable.message}")
            })
    }

    @SuppressLint("CheckResult")
    fun sendMessage(
        content: String,
        type: String,
        fileUrl: String? = null,
        senderRole: String? = null,
        senderUuid: String? = null,
        doctorUuid: String,
        patientUuid: String
    ) {
        val payload = ChatMessage(
            content = content,
            type = type,
            fileUrl = fileUrl,
            senderRole = senderRole,
            senderUuid = senderUuid
        )
        val jsonPayload = gson.toJson(payload)
        val destination = "/app/chat/$doctorUuid/$patientUuid/send"

        stompClient.send(destination, jsonPayload)
            .subscribeOn(Schedulers.io())
            .subscribe({
                println("Mensaje enviado con éxito: $content")
            }, { throwable: Throwable ->
                println("Error al enviar mensaje: ${throwable.message}")
            })
    }

    fun disconnect() {
        topicDisposable?.dispose()
        if (::stompClient.isInitialized && stompClient.isConnected) {
            stompClient.disconnect()
            println("STOMP Desconectado.")
        }
    }
}