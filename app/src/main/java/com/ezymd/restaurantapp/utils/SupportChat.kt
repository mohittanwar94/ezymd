package com.ezymd.restaurantapp.utils

import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.ServerConfig
import com.zendesk.logger.Logger
import zendesk.chat.Chat
import zendesk.chat.ChatConfiguration
import zendesk.chat.ChatEngine
import zendesk.chat.VisitorInfo
import zendesk.messaging.MessagingActivity

class SupportChat {


    companion object {
        @JvmStatic
        fun startChatSupport(activity: BaseActivity, userInfo: UserInfo) {
            val supportChat = SupportChat()
            supportChat.startChat(userInfo, activity)

        }
    }


    fun getRoleByType(userInfo: UserInfo): String {
        return "User-" + userInfo.userID + "\n" + userInfo.userName

    }

    fun startChat(userInfo: UserInfo, activity: BaseActivity) {
        Logger.setLoggable(true)
        Chat.INSTANCE.init(
            activity.applicationContext,
            ServerConfig.ZENDESK_CHAT_KEY,
            ServerConfig.APPID
        )

        val chatConfiguration = ChatConfiguration.builder()
            .withAgentAvailabilityEnabled(true)
            .withPreChatFormEnabled(false)
            .withTranscriptEnabled(false)
            .withChatMenuActions()
            .build()

        val profileProvider = Chat.INSTANCE.providers()!!.profileProvider()
        val chatProvider = Chat.INSTANCE.providers()!!.chatProvider()


        val visitorInfo = VisitorInfo.builder()
            .withName(userInfo.userName + " " + getRoleByType(userInfo))
            .withEmail(if (userInfo.email.equals("")) "NA@example.com" else userInfo.email)
            .withPhoneNumber(if (userInfo.phoneNumber.equals("")) "1234567890" else userInfo.phoneNumber)
            // numeric string
            .build()
        profileProvider.setVisitorInfo(visitorInfo, null)
        chatProvider.setDepartment("Department name", null)
        val pushProvider = Chat.INSTANCE.providers()!!.pushNotificationsProvider()
        pushProvider.registerPushToken(userInfo.getDeviceToken())


        MessagingActivity.builder()
            .withBotLabelString(activity.getString(R.string.ezymd_support))
            .withToolbarTitle(activity.getString(R.string.ezymd_support))
            .withEngines(ChatEngine.engine())
            .show(activity, chatConfiguration)
        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out)
    }


}