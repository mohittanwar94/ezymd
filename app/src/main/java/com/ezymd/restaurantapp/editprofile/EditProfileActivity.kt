package com.ezymd.restaurantapp.editprofile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.login.otp.OTPScreen
import com.ezymd.restaurantapp.utils.*
import com.ezymd.restaurantapp.utils.JSONKeys.OTP_REQUEST
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class EditProfileActivity : BaseActivity() {
    private var picUri: Uri? = null
    var myBitmap: Bitmap? = null

    private val viewModel by lazy {
        ViewModelProvider(this).get(EditProfileViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setToolBar()
        setGUI()
        setProfileData()

    }

    private fun setGUI() {
        next.setOnClickListener {
            checkConditions(it)
        }
        editProfile.setOnClickListener {
            UIUtil.clickAlpha(it)

            val isGranted = checkCameraPermissions(object : PermissionListener {
                override fun result(isGranted: Boolean) {
                    if (isGranted) {
                        callCamera()
                    }

                }
            })

            if (isGranted) {
                callCamera()
            }

        }
    }

    private fun setProfileData() {
        mobile.setText(userInfo!!.phoneNumber)
        name.setText(userInfo!!.userName)
        email.setText(userInfo!!.email)
        if (userInfo!!.profilePic != null) {
            GlideApp.with(applicationContext)
                .load(userInfo!!.profilePic).centerCrop().override(100, 100).dontAnimate()
                .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL).into(userImage)
        }
    }

    private fun callCamera() {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        startActivityForResult(intent, 200)
    }

    private fun getCaptureImageOutputUri(): Uri? {
        var outputFileUri: Uri? = null
        val getImage: File? = externalCacheDir
        if (getImage != null) {
            outputFileUri = Uri.fromFile(File(getImage.getPath(), "profile.png"))
        }
        return outputFileUri
    }


    private fun checkConditions(it: View) {
        UIUtil.clickHandled(it)
        var isEmailChange = false
        var isMobileChange = false

        if (name.text.toString().isEmpty()) {
            ShowDialog(this@EditProfileActivity).disPlayDialog(
                getString(R.string.full_name_can_not_be_empty),
                false,
                false
            )
        }

        if (email.text.toString().isEmpty()) {
            ShowDialog(this@EditProfileActivity).disPlayDialog(
                getString(R.string.email_can_not_be_empty),
                false,
                false
            )
        }

        if (mobile.text.toString().isEmpty()) {
            ShowDialog(this@EditProfileActivity).disPlayDialog(
                getString(R.string.mobile_no_can_not_be_empty),
                false,
                false
            )
        }
        if (!email.text.toString().trim().equals(userInfo!!.email)) {
            isEmailChange = true
        } else if (!mobile.text.toString().trim().equals(userInfo!!.phoneNumber)) {
            isMobileChange = true
        }

        if (!isEmailChange && !isMobileChange) {
            updateProfile(it)
            return

        }
        if (isEmailChange && isMobileChange) {
            ShowDialog(this@EditProfileActivity).disPlayDialog(
                getString(R.string.only_one_can_change_at_a_time),
                false,
                false
            ).setOnDismissListener {
                generateOtp(mobile.text.toString())
            }
        } else if (isEmailChange) {
            generateOtp(email.text.toString())
        } else {
            generateOtp(mobile.text.toString())
        }


    }

    private fun updateProfile(it: View) {
        UIUtil.clickHandled(it)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OTP_REQUEST && resultCode == Activity.RESULT_OK) {
            updateProfile(next)
        } else if (requestCode == OTP_REQUEST && resultCode != Activity.RESULT_OK) {
            SnapLog.print("back pressed")
        } else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            val mybitmap = data!!.extras!!.get("data") as Bitmap
            myBitmap = getResizedBitmap(mybitmap, 120)
            if (userImage != null) {
                userImage.setImageBitmap(myBitmap)

                saveImageOnServer(myBitmap!!)
            }


        }


    }

    private fun saveImageOnServer(myBitmap: Bitmap) {
        val f3: File = File(Environment.getExternalStorageDirectory().toString() + "/Ezymd/")
        if (!f3.exists())
            f3.mkdirs()
        var outStream: OutputStream? = null
        val file = File(
            Environment.getExternalStorageDirectory().toString() + "/Ezymd/" + "profile" + ".png"
        )
        try {
            outStream = FileOutputStream(file)
            myBitmap.compress(Bitmap.CompressFormat.PNG, 85, outStream)
            outStream.close()
            SnapLog.print("Saved")
            rotateManimation()
            viewModel.saveImage(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onStop() {
        super.onStop()
        viewModel.otpResponse.removeObservers(this)
        viewModel.isLoading.removeObservers(this)
        viewModel.errorRequest.removeObservers(this)
        userInfo!!.mUserUpdate.removeObservers(this)
        rotateAnim?.cancel()
    }

    var rotateAnim: RotateAnimation? = null
    private fun rotateManimation() {
        val rotateAnim = RotateAnimation(
            0.0f, 360f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )

        rotateAnim.repeatMode = Animation.INFINITE
        rotateAnim.duration = 500
        rotateAnim.repeatCount = -1
        rotatingImage.startAnimation(rotateAnim)

    }


    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 0) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun getPickImageResultUri(data: Intent?): Uri? {
        var isCamera = true
        if (data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
        }
        return if (isCamera) getCaptureImageOutputUri() else data!!.data
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("pic_uri", picUri);

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        picUri = savedInstanceState.getParcelable("pic_uri");

    }


    private fun generateOtp(text: String) {
        SuspendKeyPad.suspendKeyPad(this)
        viewModel.generateOtp(text)
        viewModel.otpResponse.removeObservers(this)

        viewModel.otpResponse.observe(this, Observer {
            if (it.isStatus != ErrorCodes.SUCCESS) {
                showError(false, it.message, null)
            } else {
                startActivityForResult(
                    Intent(
                        this,
                        OTPScreen::class.java
                    ).putExtra(JSONKeys.MOBILE_NO, text).putExtra(JSONKeys.IS_MOBILE, true),
                    OTP_REQUEST
                )
                overridePendingTransition(R.anim.left_in, R.anim.left_out)
            }
        })
    }


    override fun onResume() {
        super.onResume()
        setObserver()
    }


    private fun setObserver() {
        userInfo!!.mUserUpdate.observe(this, Observer {
            setProfileData()
        })

        viewModel.mResturantData.observe(this, Observer {


        })

        viewModel.errorRequest.observe(this, Observer {
            showError(false, it, null)
        })
        viewModel.isLoading.observe(this, Observer {
            progress.visibility = if (it) View.VISIBLE else View.GONE
            if (!it)
                rotateAnim?.cancel()

        })
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    private fun setToolBar() {
        toolbar.title = ""
        leftIcon.setOnClickListener {
            onNavigateUp()
        }
        setSupportActionBar(findViewById(R.id.toolbar))


    }
}