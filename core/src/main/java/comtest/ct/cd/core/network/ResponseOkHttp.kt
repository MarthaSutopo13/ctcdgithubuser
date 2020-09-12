package comtest.ct.cd.core.network

import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.OkHttpResponseAndParsedRequestListener
import okhttp3.Response

abstract class ResponseOkHttp<M> constructor(val successCode : Int)
    : OkHttpResponseAndParsedRequestListener<M> {

    override fun onResponse(okHttpResponse: Response, response: M) {
        when {
            okHttpResponse.code() == 200 -> onSuccess(okHttpResponse, response)
            okHttpResponse.code() == 401 -> onUnauthorized()
            else -> onFailed(okHttpResponse, response)
        }
    }

    override fun onError(anError: ANError) {
        if(anError.errorCode == 401){
            onUnauthorized()
        }
        else {
            onHasError(anError)
        }
    }

    abstract fun onSuccess(response: Response, model: M)

    abstract fun onUnauthorized()

    abstract fun onFailed(response: Response, model : M)

    abstract fun onHasError(error : ANError)
}