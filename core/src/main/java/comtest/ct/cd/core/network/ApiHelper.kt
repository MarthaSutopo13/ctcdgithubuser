package comtest.ct.cd.core.network

import comtest.ct.cd.core.network.repo.UserRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiHelper @Inject
constructor() {
    val userRepo = UserRepo()
}