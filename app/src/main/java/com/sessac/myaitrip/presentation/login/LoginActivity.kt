package com.sessac.myaitrip.presentation.login

import android.os.Bundle
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import com.sessac.myaitrip.common.KakaoOAuthClient
import com.sessac.myaitrip.databinding.ActivityLoginBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseActivity

class LoginActivity : ViewBindingBaseActivity<ActivityLoginBinding>({ActivityLoginBinding.inflate(it)}) {

    private val KAKAO_TAG = "카카오 로그인"
    private val KAKAO_ERROR_TAG = "카카오 로그인 에러"
    private val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {

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
        } else if (token != null) {
            Log.e(KAKAO_TAG, "로그인 성공 ${token.accessToken}")

            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    Log.e(KAKAO_TAG, "토큰 정보 보기 실패", error)
                } else if (tokenInfo != null) {
                    Log.i(KAKAO_TAG, "토큰 정보 보기 성공" +
                            "\n회원번호: ${tokenInfo.id}" +
                            "\n만료시간: ${tokenInfo.expiresIn} 초" +
                            "\n앱 ID : ${tokenInfo.appId}"
                    )

                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e(KAKAO_TAG, "사용자 정보 요청 실패", error)
                        } else if (user != null) {
                            user.kakaoAccount?.let { userAccount ->
//                                email = userAccount.email.toString()

                                userAccount.profile?.let {
//                                    profileImageUrl = it.profileImageUrl
                                    Log.d(KAKAO_TAG, "프로필 이미지 URL = ${it.profileImageUrl}")
                                }

                                // TODO. 회원번호 : 회원 ID
                                tokenInfo.id

                                // 이미 존재하는 회원인지?
//                                loginViewModel.findExistMember(email)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding) {
            btnLoginKakao.throttleClick().bind {
                KakaoOAuthClient.loginWithKakao(this@LoginActivity, kakaoCallback)
            }
        }
    }
}