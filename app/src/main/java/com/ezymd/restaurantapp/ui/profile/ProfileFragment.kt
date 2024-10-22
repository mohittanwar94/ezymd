package com.ezymd.restaurantapp.ui.profile

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.MainActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.ServerConfig
import com.ezymd.restaurantapp.editprofile.EditProfileActivity
import com.ezymd.restaurantapp.refer.ReferActivity
import com.ezymd.restaurantapp.ui.cart.ProfileViewModel
import com.ezymd.restaurantapp.utils.*
import com.ezymd.restaurantapp.wallet.WalletActivity
import com.google.android.material.appbar.AppBarLayout
import com.stripe.android.CustomerSession
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
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar()
        setGUI()

    }

    private fun setGUI() {
        setProfileData()
        edit_profile.setOnClickListener {
            UIUtil.clickAlpha(it)
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out)
        }
        logout.setOnClickListener {
            UIUtil.clickHandled(it)
            val baseRequest = BaseRequest(userInfo)
            notificationsViewModel.logout(baseRequest)

        }

        refer.setOnClickListener {
            UIUtil.clickAlpha(it)
            val intent = Intent(activity, ReferActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out)
        }

        wallet.setOnClickListener {
            UIUtil.clickAlpha(it)
            val intent = Intent(activity, WalletActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out)
        }
        faq.setOnClickListener {
            UIUtil.clickAlpha(it)
            loadFaqs()
        }

    }

    private fun loadFaqs() {
        val builder = CustomTabsIntent.Builder()
        builder.setShowTitle(true)
        builder.setCloseButtonIcon(
            BitmapFactory.decodeResource(resources, R.drawable.ic_back)
        )
        builder.setShareState(CustomTabsIntent.SHARE_STATE_OFF)
        builder.setStartAnimations(requireActivity(), R.anim.left_in, R.anim.left_out)
        builder.setExitAnimations(requireActivity(), R.anim.right_in, R.anim.right_out)

        val customTabsIntent = builder.build()

        val list = ArrayList<String>()
        list.add(ServerConfig.FAQ_URL)
        val packageName = CustomTabsClient.getPackageName(requireContext(), list)
        if (packageName == null) {
            requireActivity().startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(ServerConfig.FAQ_URL)
                )
            )

        } else {
            customTabsIntent.intent.setPackage(packageName)
            customTabsIntent.launchUrl(requireActivity(), Uri.parse(ServerConfig.FAQ_URL))
        }

    }

    override fun onResume() {
        super.onResume()
        setObserver()
    }


    private fun setObserver() {

        userInfo.mUserUpdate.observe(this, Observer {
            setProfileData()
        })
        notificationsViewModel.isLoading.observe(this, Observer {
            if (it)
                logoutProgress.visibility = View.VISIBLE
            else
                logoutProgress.visibility = View.GONE
        })

        notificationsViewModel.mResturantData.observe(this, Observer {
            if (it.status == ErrorCodes.SUCCESS) {
                userInfo.clear()
                CustomerSession.endCustomerSession()
                val intent =
                    Intent(requireActivity(), com.ezymd.restaurantapp.login.Login::class.java)
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out)
                requireActivity().finish()
            } else {
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
        notificationsViewModel.isLoading.removeObservers(this)
        notificationsViewModel.mResturantData.removeObservers(this)
        userInfo.mUserUpdate.removeObservers(this)
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


    private fun setProfileData() {
        userName.text = userInfo!!.userName
        if (userInfo!!.email != "")
            email.text = userInfo!!.email
        else
            email.text = userInfo!!.phoneNumber
        if (userInfo!!.profilePic != null) {
            GlideApp.with(requireActivity().applicationContext)
                .load(userInfo!!.profilePic).centerCrop().override(100, 100).dontAnimate()
                .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL).into(userImage)
        }
    }
}