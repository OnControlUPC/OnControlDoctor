package oncontroldoctor.upc.edu.pe.communication.data.remote

import android.annotation.SuppressLint
import com.google.gson.Gson
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionHolder
import oncontroldoctor.upc.edu.pe.communication.data.model.ChatMessage
import oncontroldoctor.upc.edu.pe.shared.data.remote.ApiConstants.WS_BASE_URL
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage

class ChatService(
    private val patientUuid: String
) {
    private lateinit var stompClient: StompClient
    private val gson = Gson()
    private var topicDisposable: Disposable? = null
    private var lifecycleDisposable: Disposable? = null

    private val _incomingMessages = PublishProcessor.create<ChatMessage>()
    val incomingMessages: Flowable<ChatMessage> = _incomingMessages
    private val doctorUuid: String = SessionHolder.getUserUuid().toString()
    var token: String = SessionHolder.getToken() ?: ""

    @SuppressLint("CheckResult")
    fun connect() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_BASE_URL)
        val headers = listOf(StompHeader("Authorization", token))
        stompClient.connect(headers)


        lifecycleDisposable = stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> {
                        println("STOMP Conectado: ${lifecycleEvent.message}")
                        subscribeToChatTopic()
                    }
                    LifecycleEvent.Type.ERROR -> {
                        println("STOMP Error: ${lifecycleEvent.exception?.message}")
                        // Handle reconnection logic or notify UI
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        println("STOMP Desconectado: ${lifecycleEvent.message}")
                        // Handle reconnection logic or notify UI
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
            .observeOn(AndroidSchedulers.mainThread())
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
        fileUrl: String? = null
    ) {
        val payload = ChatMessage(
            content = content,
            type = type,
            fileUrl = fileUrl,
            senderRole = "ROLE_ADMIN",
            senderUuid = doctorUuid
        )
        val jsonPayload = gson.toJson(payload)
        val destination = "/app/chat/$doctorUuid/$patientUuid/send"

        stompClient.send(destination, jsonPayload)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                println("Mensaje enviado con éxito: $content")
            }, { throwable: Throwable ->
                println("Error al enviar mensaje: ${throwable.message}")
            })
    }

    fun disconnect() {
        topicDisposable?.dispose()
        lifecycleDisposable?.dispose()
        if (::stompClient.isInitialized && stompClient.isConnected) {
            stompClient.disconnect()
            println("STOMP Desconectado.")
        }
    }
}