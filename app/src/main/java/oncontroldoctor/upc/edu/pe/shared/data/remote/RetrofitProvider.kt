package oncontroldoctor.upc.edu.pe.shared.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    //"https://oncontrolbackend-gtbdhpc9fgd2epdx.westus3-01.azurewebsites.net/api/v1/"
    const val BASE_URL = "https://oncontrolbackend-gtbdhpc9fgd2epdx.westus3-01.azurewebsites.net/api/v1/"
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}