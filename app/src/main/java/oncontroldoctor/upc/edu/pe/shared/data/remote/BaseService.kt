package oncontroldoctor.upc.edu.pe.shared.data.remote

import oncontroldoctor.upc.edu.pe.authentication.data.local.SessionHolder
import retrofit2.HttpException
import retrofit2.Response

abstract class BaseService {
    protected suspend fun <T> authorizedCall(call: suspend (String) -> Response<T>): Result<T> {
        val token = SessionHolder.getToken()
        return try {
            val response = call("Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    @Suppress("UNCHECKED_CAST")
                    Result.success(Unit as T)
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    protected suspend fun <T> plainCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    @Suppress("UNCHECKED_CAST")
                    Result.success(Unit as T)
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
