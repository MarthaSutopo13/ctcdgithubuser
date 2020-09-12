package comtest.ct.cd.core.network.repo

import com.rx2androidnetworking.Rx2ANRequest
import comtest.ct.cd.core.network.ApiConstant
import comtest.ct.cd.core.network.ApiEndpoint
import comtest.ct.cd.core.network.RestApi

class UserRepo{
    fun getListUser(name: String, perPage: Int) : Rx2ANRequest {
        val params = HashMap<String, String>()
        params[ApiConstant.Q] = name
        params[ApiConstant.PER_PAGE] = perPage.toString()
        return RestApi.get(ApiEndpoint.SEARCH_USER, params, null, null)
    }
}