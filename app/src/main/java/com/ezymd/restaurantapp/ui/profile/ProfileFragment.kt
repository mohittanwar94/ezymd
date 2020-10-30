package com.ezymd.restaurantapp.ui.profile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.MainActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.ui.notifications.ProfileViewModel
import com.ezymd.restaurantapp.utils.*
import com.facebook.login.Login
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    enum class State {
        EXPANDED, COLLAPSED, IDLE
    }

    var mCurrentState: State = State.IDLE

    private lateinit var notificationsViewModel: ProfileViewModel

    private val userInfo by lazy {
        UserInfo.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar()
        setGUI()

    }

    private fun setGUI() {
        logout.setOnClickListener {
            UIUtil.clickHandled(it)
            val baseRequest = BaseRequest(userInfo)
            notificationsViewModel.logout(baseRequest)

        }
    }

    override fun onResume() {
        super.onResume()
        setObserver()
    }

    private fun setObserver() {
        notificationsViewModel.isLoading.observe(this, Observer {
            if (it)
                logoutProgress.visibility = View.VISIBLE
            else
                logoutProgress.visibility = View.GONE
        })

        notificationsViewModel.mResturantData.observe(this, Observer {
            if (it.status == ErrorCodes.SUCCESS) {
                userInfo.clear()
                val intent = Intent(requireActivity(), Login::class.java)
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out)
                requireActivity().finish()
            }else{
                (activity as BaseActivity).showError(false, it.message, null)
            }
        })
    }


    override fun onStart() {
        super.onStart()
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireActivity(), R.color.color_002366)

    }

    override fun onStop() {
        super.onStop()
        requireActivity().window.statusBarColor = Color.WHITE
    }


    private fun setToolBar() {

        (requireActivity() as MainActivity).setSupportActionBar(toolbar)
        requireActivity().window.statusBarColor = Color.TRANSPARENT
        toolbar_layout.title = getString(R.string.title_profile)

        toolbar_layout.setExpandedTitleColor(Color.TRANSPARENT)
        app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            SnapLog.print("" + verticalOffset)
            if (Math.abs(verticalOffset) != verticalOffset) {
                toolbar_layout.setCollapsedTitleTextColor(Color.TRANSPARENT)
            }
            if (verticalOffset == 0) {

                mCurrentState = State.EXPANDED;
            } else if (Math.abs(verticalOffset) >= appBarLayout.totalScrollRange) {
                if (mCurrentState != State.COLLAPSED) {
                    toolbar.setTitleTextColor(Color.BLACK)
                    toolbar.setBackgroundColor(Color.WHITE)
                    requireActivity().window.statusBarColor = Color.WHITE

                }
                toolbar_layout.setCollapsedTitleTextColor(Color.BLACK)
                mCurrentState = State.COLLAPSED;
            } else {
                if (mCurrentState != State.IDLE) {
                    toolbar.setBackgroundColor(Color.TRANSPARENT)
                    toolbar.setTitleTextColor(Color.TRANSPARENT)
                    toolbar_layout.setExpandedTitleColor(Color.TRANSPARENT)
                    requireActivity().window.statusBarColor =
                        ContextCompat.getColor(requireActivity(), R.color.color_002366)
                }
                toolbar_layout.setExpandedTitleColor(Color.TRANSPARENT)
                mCurrentState = State.IDLE
            }

        })

    }
}