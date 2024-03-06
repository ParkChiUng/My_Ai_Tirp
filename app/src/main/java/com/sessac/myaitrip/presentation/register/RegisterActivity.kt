package com.sessac.myaitrip.presentation.register

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.kakao.sdk.user.UserApiClient
import com.sessac.myaitrip.databinding.ActivityRegisterBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseActivity
import com.sessac.myaitrip.util.GlideUtil
import com.sessac.myaitrip.util.showToast
import reactivecircus.flowbinding.android.widget.textChanges

class RegisterActivity : ViewBindingBaseActivity<ActivityRegisterBinding>(
    { layoutInflater -> ActivityRegisterBinding.inflate(layoutInflater) }
) {
    private lateinit var userEmail: String
    private lateinit var userPassword: String

    private var userNickname: String? = null
    private var userProfileImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initProfileIntent()
        initTextWatcher()
        initImageOnClick()
        initBackPressed()

        initSubmitButtonOnClick()
    }

    // 뒤로가기 콜백
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 뒤로가기 클릭 시 실행시킬 코드 입력
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                } else {
                    showToast("회원가입을 취소하셨습니다.")
                    finish()
                }
            }
        }
    }

    // 뒤로가기 -> LoginActivity (동의항목 다시 받기)
    private fun initBackPressed() {
        // 툴바 뒤로가기 Navigation
        binding.tbRegister.setNavigationOnClickListener {
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                } else {
                    showToast("회원가입을 취소하셨습니다.")
                    finish()
                }
            }
        }

        // Android 시스템 뒤로가기를 하였을 때, 콜백 설정
        onBackPressedDispatcher.addCallback(this@RegisterActivity, onBackPressedCallback)
    }

    private fun initSubmitButtonOnClick() {
        with(binding) {
            btnRegisterSubmit.throttleClick().bind {
    //                registerViewModel.register(email, password, )
            }

        }
    }

    private fun initImageOnClick() {
        with(binding) {
            btnRegisterAddProfileImage.throttleClick().bind {

            }

            ivRegisterProfile.throttleClick().bind {

            }
        }

    }

    private fun initTextWatcher() {
        // 닉네임 TextChangedWatcher
        with(binding) {
            etRegisterNickName.textChanges().bind {
                btnRegisterSubmit.isEnabled = it.length in 1..8 && it.isNotBlank()
            }
        }
    }

    /**
     * Init profile intent
     * 사용자가 동의한 프로필 정보가 있다면 가져와서 뿌려주기
     */
    private fun initProfileIntent() {
        intent?.let { intent ->
            with(intent) {
                getStringExtra("userEmail")?.let {
                    userEmail = it
                }
                getStringExtra("userPassword")?.let {
                    userPassword = it
                }
                getStringExtra("userNickname")?.let {
                    binding.etRegisterNickName.setText(it)
                }
                getStringExtra("userProfileImageUrl")?.let {
                    GlideUtil.loadProfileImage(this@RegisterActivity, it, binding.ivRegisterProfile)
                }
            }
        }
    }
}