package comtest.ct.cd.marthasutopo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.error.ANError
import comtest.ct.cd.core.base.BaseActivity
import comtest.ct.cd.core.listener.OnLoadMoreListener
import comtest.ct.cd.core.model.UserResponse
import comtest.ct.cd.core.network.ResponseOkHttp
import comtest.ct.cd.marthasutopo.adapter.UserAdapter
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Response

class MainActivity : BaseActivity() {
    private val TAG = "Search User"
    private lateinit var userAdapter : UserAdapter
    val layoutManager = LinearLayoutManager(this)
    lateinit var onLoadMoreListener: OnLoadMoreListener
    val users: MutableList<UserResponse.User> = mutableListOf()
    var perPage = 10
    var search = ""

    override fun setLayout(): Int {
        return R.layout.activity_main
    }

    override fun onInitializedView(savedInstanceState: Bundle?) {
        initView()
        initAction()
    }

    fun initView() {
        rv_user.layoutManager = layoutManager
        userAdapter = UserAdapter(this)
        rv_user.adapter = userAdapter
    }

    fun initAction() {
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0?.length == 0) {
                    userAdapter.clearUser()
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        btn_search.setOnClickListener {
            search = et_search.text.toString()
            getListUser(et_search.text.toString())
        }

        sr_user.setOnRefreshListener {
            sr_user.isRefreshing = false
        }

        onLoadMoreListener = object : OnLoadMoreListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                onLoadMoreListener.counter = 5
                perPage += 5
                getMoretListUser(perPage)
                var currentSize = userAdapter.itemCount
                view?.post { userAdapter.notifyItemRangeInserted(currentSize, userAdapter.itemCount - 1) }
            }
        }

        rv_user.addOnScrollListener(onLoadMoreListener)
    }

    fun getListUser(name: String) {
        apiHelper.userRepo.getListUser(name, perPage).getAsOkHttpResponseAndObject(
            UserResponse::class.java,
            object : ResponseOkHttp<UserResponse>(200){

                override fun onSuccess(response: Response, model: UserResponse) {
                    if (model.items!! == null) {
                        tv_desc.text = getString(R.string.err_data_not_found)
                        userAdapter.clearUser()
                    }

                    if(model.items!!.size == 0) {
                        sr_user.visibility = View.GONE
                        ll_desc.visibility = View.VISIBLE
                        tv_desc.text = getString(R.string.err_data_not_found)
                    } else {
                        model.items?.let {
                            sr_user.visibility = View.VISIBLE
                            ll_desc.visibility = View.GONE
                            userAdapter.setUser(it)
                        }
                    }
                }

                override fun onUnauthorized() {
                    Log.e(TAG, "authorization")
                }

                override fun onFailed(response: Response, model: UserResponse) {
                    Log.e(TAG, response.message())
                }

                override fun onHasError(error: ANError) {
                    if (error.errorCode == 403) {
                        sr_user.visibility = View.GONE
                        ll_desc.visibility = View.VISIBLE
                        tv_desc.text = getString(R.string.err_data_max_limit)
                    }
                    Log.e(TAG, error.errorDetail)
                }
            })
    }

    fun getMoretListUser(page: Int) {
        apiHelper.userRepo.getListUser(search, page).getAsOkHttpResponseAndObject(
            UserResponse::class.java,
            object : ResponseOkHttp<UserResponse>(200){

                override fun onSuccess(response: Response, model: UserResponse) {
                    if(model.items.orEmpty().isNotEmpty())  {
                        for (i in model.items!!) {
                            users.add(i)
                        }
                        sr_user.visibility = View.VISIBLE
                        ll_desc.visibility = View.GONE
                        userAdapter.setUser(users)
                    }
                }

                override fun onUnauthorized() {
                    Log.e(TAG, "authorization")
                }

                override fun onFailed(response: Response, model: UserResponse) {
                    Log.e(TAG, response.message())
                }

                override fun onHasError(error: ANError) {
                    if (error.errorCode == 403) {
                        sr_user.visibility = View.GONE
                        ll_desc.visibility = View.VISIBLE
                        tv_desc.text = getString(R.string.err_data_max_limit)
                    }
                    Log.e(TAG, error.errorDetail)
                }
            })
    }
}