package comtest.ct.cd.core.network

import com.androidnetworking.common.Priority
import com.rx2androidnetworking.Rx2ANRequest
import com.rx2androidnetworking.Rx2AndroidNetworking
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object RestApi {
    var okHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(HttpInterceptor())
        .build()

    var httpClientUpload = OkHttpClient().newBuilder()
        .connectTimeout(2, TimeUnit.MINUTES)
        .readTimeout(2, TimeUnit.MINUTES)
        .writeTimeout(2, TimeUnit.MINUTES)
        .addInterceptor(HttpInterceptor())
        .build()

    fun get(
        endpoint: String, params: Map<String, String>?,
        paths: Map<String, String>?, headers: Map<String, String>?
    ): Rx2ANRequest {

        val getRequest = Rx2AndroidNetworking.get(endpoint)

        if (headers != null) {
            getRequest.addHeaders(headers)
        }

        if (params != null) {
            getRequest.addQueryParameter(params)
        }

        if (paths != null) {
            getRequest.addPathParameter(paths)
        }

        getRequest.setPriority(Priority.LOW)

        getRequest.setOkHttpClient(okHttpClient)

        return getRequest.build()
    }
}