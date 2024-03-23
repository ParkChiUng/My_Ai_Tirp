package com.sessac.myaitrip.presentation.register

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseUser
import com.kakao.sdk.user.UserApiClient
import com.sessac.myaitrip.R
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.databinding.ActivityRegisterBinding
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.presentation.common.ViewBindingBaseActivity
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.presentation.progress.ProgressActivity
import com.sessac.myaitrip.util.GlideUtil
import com.sessac.myaitrip.util.PermissionUtil
import com.sessac.myaitrip.util.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import reactivecircus.flowbinding.android.widget.textChanges

class RegisterActivity : ViewBindingBaseActivity<ActivityRegisterBinding>(
    { layoutInflater -> ActivityRegisterBinding.inflate(layoutInflater) }
) {
    private val registerViewModel: RegisterViewModel by viewModels { ViewModelFactory(this) }
    private lateinit var userEmail: String
    private lateinit var userPassword: String

    private var userNickname: String? = null
    private var userProfileImageURL: String? = null
    private var userProfileImageURI: Uri? = null

    private var isBackPressed = false
//    private var isAvailableNickname = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initProfileIntent()
        initTextWatcher()
        initImageOnClick()
        initBackPressed()
        initSubmitButtonOnClick()

//        checkNickObserverSetup()
        registerStatusCollectionSetup()
    }

    /*private fun checkNickObserverSetup() {
        lifecycleScope.launch {
            registerViewModel.checkNickStatus.collectLatest { uiState ->
                when(uiState) {
                    is UiState.Loading -> {
                        // TODO. 프로그레스 바
                    }
                    is UiState.Success -> {
                        isAvailableNickname = uiState.data.isEmpty

                        if(isAvailableNickname) {
                            Log.e("닉네임 중복 확인", "닉네임 사용 가능")
                            showToast("사용 가능한 닉네임입니다. 회원가입을 완료해주세요.")
                        } else {
                            showToast("중복된 닉네임입니다. 다른 닉네임을 사용해주세요.")

                            // 입력 창 빨갛게?
                            with(binding) {
//                                etRegisterNickName.requestFocus()
//                                lytRegisterNickName.requestFocus()
                                lytRegisterNickName.error = "중복된 닉네임입니다."

                                lytRegisterNickName.requestFocus()
                                etRegisterNickName.requestFocus()
                                delay(2000)
                                lytRegisterNickName.error = null
                            }
                        }
                    }
                    is UiState.Error -> {}
                    else -> {}
                }
            }
        }
    }*/

    private fun registerStatusCollectionSetup() {
        lifecycleScope.launch {
            registerViewModel.registerStatus.collectLatest { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        // TODO. 프로그레스 바
                    }

                    is UiState.Success -> {
                        val currentUser =
                            MyAiTripApplication.getInstance().getFirebaseAuth().currentUser
                        // currentUser가 Null값 이면, 현재 디바이스는 계정 정보가 없는 것, 로그인 실패 or 회원가입 실패
                        currentUser?.let { user ->
                            saveUserPreferenceData(user)
                            Intent(this@RegisterActivity, ProgressActivity::class.java).also {
                                it.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(it)
                            }
                        } ?: showToast("회원가입 실패")
                    }

                    is UiState.Error -> {}
                    else -> {}
                }
            }
        }
    }

    /**
     * Save user preference data
     * 로그인 성공 시, 유저 데이터 디바이스에 저장하기
     * @param user
     */
    private fun saveUserPreferenceData(user: FirebaseUser) {
        with(registerViewModel) {
            updateUserPreferenceAutoLogin(true)
            updateUserPreferenceUserId(user.uid)
        }
    }

    // 뒤로가기 콜백
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 뒤로가기 클릭 시 실행시킬 코드 입력
            if (!isBackPressed) {
                showToast("뒤로가기를 한 번 더 누르면 회원가입이 취소됩니다.")
                isBackPressed = true
            } else {
                UserApiClient.instance.unlink { error ->
                    error?.let {

                    }
                }
                showToast("회원가입을 취소하셨습니다.")
                finish()
            }
        }
    }

    // 뒤로가기 -> LoginActivity (동의항목 다시 받기)
    private fun initBackPressed() {
        // 툴바 뒤로가기 Navigation
        binding.tbRegister.setNavigationOnClickListener {
            UserApiClient.instance.unlink { error ->
                error?.let {

                }
            }
            showToast("회원가입을 취소하셨습니다.")
            finish()
        }

        // Android 시스템 뒤로가기를 하였을 때, 콜백 설정
        onBackPressedDispatcher.addCallback(this@RegisterActivity, onBackPressedCallback)
    }

    /**
     * Init submit button on click
     * 완료 버튼 리스너
     */
    private fun initSubmitButtonOnClick() {
        with(binding) {
            btnRegisterSubmit.throttleClick().bind {
                val nickname = etRegisterNickName.text.toString()
                // Defaul profile image
                if (userProfileImageURL == null && userProfileImageURI == null)
                    userProfileImageURL = resources.getString(R.string.default_img_url)

                registerViewModel.register(
                    userEmail,
                    userPassword,
                    nickname,
                    userProfileImageURL,
                    userProfileImageURI
                ) // 회원 가입 진행

//                registerViewModel.checkExistNickname(nickname) // 닉네임 중복 확인

                // 중복된 닉네임이 아니고, 프로필 사진이 있을 때만 회원 가입 가능
//                if(isAvailableNickname) {
//                    if(userProfileImageURL == null && userProfileImageURI == null) userProfileImageURL = resources.getString(R.string.default_img_url)
//
//                    registerViewModel.register(userEmail, userPassword, nickname, userProfileImageURL, userProfileImageURI) // 회원 가입 진행
//                }
            }
        }
    }

    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                imageUri?.let { loadSelectedImage(it) }
            }
        }

    /**
     * Photo picker launcher
     * Android 13이상부터 사용가능한 PhotoPicker 실행
     */
    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri ->
            imageUri?.let { loadSelectedImage(it) }
        }

    private fun loadSelectedImage(imageUri: Uri) {
        userProfileImageURL = null // 갤러리에서 사진을 가져왔다면, 기존 프로필 이미지 URL은 사용하지 않는다.
        userProfileImageURI = imageUri

        GlideUtil.loadProfileImage(this@RegisterActivity, imageUri, binding.ivRegisterProfile)
    }

    private fun navigateGallery() {
        lifecycleScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13부터는 이미지 권한 없이도 PhotoPicker를 사용가능
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                val permissionResult =
                    PermissionUtil.requestPermissionResultByCoroutine(Manifest.permission.READ_EXTERNAL_STORAGE)
                if (permissionResult.isGranted) {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    imageResultLauncher.launch(intent)
                } else {
                    showToast("사진 권한을 허용해주세요.")
                }
            }
        }
    }

    private fun initImageOnClick() {
        with(binding) {
            btnRegisterAddProfileImage.throttleClick().bind {
                navigateGallery()
            }

            ivRegisterProfile.throttleClick().bind {
                navigateGallery()
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
                    userNickname = it
                    binding.etRegisterNickName.setText(it)
                }
                getStringExtra("userProfileImageUrl")?.let {
                    userProfileImageURL = it
                    GlideUtil.loadProfileImage(this@RegisterActivity, it, binding.ivRegisterProfile)
                }
            }
        }
    }
}