package comtest.ct.cd.core.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class HttpInterceptor: Interceptor {
    var urlLog : String? = null
    var headersLog : String? = null
    var bodyLog : String? = null
    var methodLog : String? = null
    var statusCodeLog : Int? = null
//    var mContext: Context = this.getA

    //    var preferenceHelper = PreferenceHelper(mContext)
    var charset : Charset? = Charsets.UTF_8

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val token = ""
        val requestBuilder = original.newBuilder()
            .addHeader("Authorization", token)
        val request = requestBuilder.build()

        val t1 = System.nanoTime()
        getRequestValue(request)

        val response = chain.proceed(request)

        // Get Execution Time
        val t2 = System.nanoTime()
        val executionTime = TimeUnit.MINUTES.convert((t2 - t1), TimeUnit.MILLISECONDS)

        // Get Response
        val buffer = getResponseValue(response)

        if (response.body()?.contentLength() != 0L) {
            AppLogger().i("Request ($methodLog) : $urlLog \n[Body] => \n$bodyLog \n[Headers] => \n$headersLog" +
                    "Excecution Time : $executionTime" +
                    "\nResponse ($statusCodeLog) : " + buffer?.clone()?.readString(charset))
        }

        return response
    }

    fun getRequestValue(value : Request){
        urlLog = value.url().toString()
        headersLog = value.headers().toString()
        bodyLog = getBodyRequestValue(value.body())
        methodLog = value.method()
    }

    fun getResponseValue(value : Response) : Buffer? {
        val source = value.body()?.source()
        source?.request(Long.MAX_VALUE) // Buffer the entire body.
        var buffer = source?.buffer()

        val contentType = value.body()?.contentType()

        if (contentType != null) {
            charset = contentType.charset(Charsets.UTF_8)
        }

        statusCodeLog = value.code()

        return buffer
    }

    fun getBodyRequestValue(value : RequestBody?) : String {
        return try {
            val buffer = Buffer()
            value?.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            "Read Body Request Failed"
        }
    }
}