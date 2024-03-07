package com.sessac.myaitrip.presentation.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import com.sessac.myaitrip.MainActivity
import com.sessac.myaitrip.common.KakaoOAuthClient
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.databinding.ActivityLoginBinding
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.presentation.common.ViewBindingBaseActivity
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.presentation.progress.ProgressActivity
import com.sessac.myaitrip.presentation.register.RegisterActivity
import com.sessac.myaitrip.util.showToast
import kotlinx.coroutines.launch

class LoginActivity :
    ViewBindingBaseActivity<ActivityLoginBinding>({ ActivityLoginBinding.inflate(it) }) {

    private val loginViewModel: LoginViewModel by viewModels() { ViewModelFactory(this) }

    private lateinit var userEmail: String
    private lateinit var userPassword: String

    private var userNickname: String? = null
    private var userProfileImageUrl: String? = null

    private val KAKAO_TAG = "카카오 로그인"
    private val KAKAO_ERROR_TAG = "카카오 로그인 에러"
    private val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        error?.let { handleError(error) }
        token?.let { startKakaoLogin(token) }
    }

    private fun startKakaoLogin(token: OAuthToken) {
        Log.e(KAKAO_TAG, "로그인 성공 ${token.accessToken}")

        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Log.e(KAKAO_TAG, "토큰 정보 보기 실패", error)
            } else if (tokenInfo != null) {
                Log.i(
                    KAKAO_TAG, "토큰 정보 보기 성공" +
                            "\n회원번호: ${tokenInfo.id}" +
                            "\n만료시간: ${tokenInfo.expiresIn} 초" +
                            "\n앱 ID : ${tokenInfo.appId}"
                )

                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e(KAKAO_TAG, "사용자 정보 요청 실패", error)
                    } else if (user != null) {
                        Log.d(KAKAO_TAG, "회원번호 = ${user.id}")
                        user.kakaoAccount?.let { userAccount ->
                            userAccount.profile?.let { profile ->
                                // 닉네임 (선택사항)
                                profile.nickname?.let {
                                    Log.d(KAKAO_TAG, "닉네임 = $it")
                                    userNickname = it
                                }

                                // 프로필 사진 (선택사항)
                                profile.profileImageUrl?.let {
                                    Log.d(KAKAO_TAG, "프로필 이미지 URL = $it")
                                    userProfileImageUrl = it
                                }
                            }

                            // user.id = 회원 ID -> 회원 비밀번호로 사용
                            userEmail = userAccount.email.toString()
                            userPassword = user.id.toString()

                            Log.d(KAKAO_TAG, "이메일 = $userEmail")
                            Log.d(KAKAO_TAG, "비밀번호 = $userPassword")

                            if (::userEmail.isInitialized && ::userPassword.isInitialized) {
                                loginViewModel.login(userEmail, userPassword)
                            }

                        }
                    }
                }
            }
        }

    }

    private fun handleError(error: Throwable) {
        Log.e(KAKAO_ERROR_TAG, error.toString())
        when {
            error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                Log.d(KAKAO_ERROR_TAG, "접근이 거부 됨(동의 취소)")
            }

            error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                Log.d(KAKAO_ERROR_TAG, "유효하지 않는 앱")
            }

            error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                Log.d(KAKAO_ERROR_TAG, "인증 수단이 유효하지 않아 인증할 수 없는 상태")
            }

            error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                Log.d(KAKAO_ERROR_TAG, "요청 파라미터 오류")
            }

            error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                Log.d(KAKAO_ERROR_TAG, "유효하지 않은 scope ID")
            }

            error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                Log.d(KAKAO_ERROR_TAG, "설정이 올바르지 않음(android key hash)")
            }

            error.toString() == AuthErrorCause.ServerError.toString() -> {
                Log.d(KAKAO_ERROR_TAG, "서버 내부 에러")
            }

            error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                Log.d(KAKAO_ERROR_TAG, "앱이 요청 권한이 없음")
            }

            else -> { // Unknown
                Log.d(KAKAO_ERROR_TAG, "기타 에러")
            }
        }

    }

    private fun handleUiState(state: UiState<AuthResult>) {
        Log.e("LoginState", "HandleUiState()")
        when (state) {
            is UiState.Success -> {
                Log.e("LoginHandleState", "UiState.Success")
                // currentUser가 있어야, 로그인이 성공한 것이다.
                val currentUser = MyAiTripApplication.getInstance().getFirebaseAuth().currentUser

                currentUser?.let { user ->
                    saveUserPreferenceData(user)
                    moveToMain()
                } ?: {
                    showToast("계정 정보가 없습니다.")
                }
            }

            is UiState.FirebaseAuthError -> {
                Log.e("LoginHandleState", "UiState.FirebaseAuthError")
                /*
                Firebase Auth Error Code
                    auth/email-already-exists: 제공된 이메일이 이미 다른 사용자에 의해 사용 중인 경우
                    auth/invalid-email: 제공된 이메일이 유효한 이메일 주소 형식이 아닌 경우1.
                    auth/operation-not-allowed: 해당 인증 방법이 Firebase 프로젝트에서 활성화되지 않은 경우2.
                    auth/weak-password: 제공된 비밀번호가 너무 약한 경우2.
                    auth/wrong-password: 잘못된 비밀번호를 입력한 경우2.
                    auth/user-disabled: 사용자 계정이 비활성화된 경우2.
                    auth/too-many-requests: 짧은 시간 동안 너무 많은 요청을 보낸 경우2.
                    auth/network-request-failed: 네트워크 요청이 실패한 경우2.
                 */

                val errorCode = state.firebaseAuthException.errorCode
                Log.e("FirebaseAuthException", errorCode)

                // 로그인 오류와 계정이 없을 때는 회원가입 화면으로 이동
                if (errorCode.contains("USER_NOT_FOUND", true) ||
                    errorCode.contains("INVALID_CREDENTIAL", true)
                ) {

                    val registerIntent =
                        Intent(this@LoginActivity, RegisterActivity::class.java).apply {
                            if (this@LoginActivity::userEmail.isInitialized) putExtra(
                                "userEmail",
                                userEmail
                            )
                            if (this@LoginActivity::userPassword.isInitialized) putExtra(
                                "userPassword",
                                userPassword
                            )
                            userNickname?.let { putExtra("userNickname", it) }
                            userProfileImageUrl?.let { putExtra("userProfileImageUrl", it) }
                        }

                    startActivity(registerIntent)
                }
            }

            is UiState.Error -> {
//
            }

            is UiState.Loading -> {
                Log.e("LoginHandleState", "UiState.Loading")
                // TODO. 프로그레스 바 (로그인 중..)
            }

            else -> {
                Log.e("LoginHandleState", "$state")
            }
        }
    }

    private fun moveToMain() {
        Intent(this@LoginActivity, ProgressActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        }
    }

    /**
     * Save user preference data
     * 로그인 성공 시, 유저 데이터 디바이스에 저장하기
     * @param user
     */
    private fun saveUserPreferenceData(user: FirebaseUser) {
        with(loginViewModel) {
            updateUserPreferenceAutoLogin(true)
            updateUserPreferenceUserId(user.uid)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding) {
            btnLoginKakao.throttleClick().bind {
                KakaoOAuthClient.loginWithKakao(this@LoginActivity, kakaoCallback)
            }
        }

        setupLoginStatusCollection()
    }
    override fun onResume() {
        super.onResume()
        userNickname = null
        userProfileImageUrl = null
    }

    private fun setupLoginStatusCollection() {
        lifecycleScope.launch {
            loginViewModel.loginStatus.collect { state ->
                handleUiState(state)
            }
        }
    }
}