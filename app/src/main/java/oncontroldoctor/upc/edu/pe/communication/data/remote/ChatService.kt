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
    val token: String = SessionHolder.getToken() ?: ""
    @SuppressLint("CheckResult")
    fun connect() {
        // 1) Construye la URL correcta
        val url = "$WS_BASE_URL/ws/websocket"
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)

        // 2) Headers de autenticación
        val headers = listOf(StompHeader("Authorization", "Bearer $token"))
        stompClient.connect(headers)

        // 3) Observa el ciclo de vida
        lifecycleDisposable = stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event ->
                when (event.type) {
                    LifecycleEvent.Type.OPENED -> {
                        println("STOMP ▶ Conectado con éxito")
                        subscribeToChatTopic()
                    }
                    LifecycleEvent.Type.ERROR -> {
                        println("STOMP ✖ Error en conexión: ${event.exception?.message}")
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        println("STOMP ■ Conexión cerrada")
                    }
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                        println("STOMP ⚠ Heartbeat fallido")
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
                println("Mensaje recibido en ChatService: ${chatMessage.content}")
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
        if (!::stompClient.isInitialized || !stompClient.isConnected) {
            println("Cannot send message, StompClient not connected.")
            return
        }
        val payload = ChatMessage(
            id = null,
            content = content,
            type = type,
            fileUrl = fileUrl,
            senderRole = "ROLE_ADMIN",
            doctorUuid = doctorUuid,
            patientUuid = patientUuid,
            createdAt = null
        )
        val jsonPayload = gson.toJson(payload)
        val destination = "/app/chat/$doctorUuid/$patientUuid/send"

        stompClient.send(destination, jsonPayload)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                println("Message sent successfully: $content")
            }, { throwable: Throwable ->
                println("Error sending message: ${throwable.message}")
                throwable.printStackTrace()
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