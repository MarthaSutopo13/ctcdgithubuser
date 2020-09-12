package comtest.ct.cd.core.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserResponse {
    @SerializedName("items")
    @Expose
    var items: List<User>? = null

    class User{
        @SerializedName("login")
        @Expose
        var login: String? = null

        @SerializedName("avatar_url")
        @Expose
        var avatarUrl: String? = null
    }
}