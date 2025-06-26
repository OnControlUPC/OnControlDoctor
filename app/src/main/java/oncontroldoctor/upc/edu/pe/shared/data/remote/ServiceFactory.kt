package oncontroldoctor.upc.edu.pe.shared.data.remote

object ServiceFactory {
    inline fun <reified T> create(): T {
        return RetrofitProvider.retrofit.create(T::class.java)
    }
}