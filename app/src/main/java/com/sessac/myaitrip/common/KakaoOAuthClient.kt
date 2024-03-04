package com.sessac.myaitrip.common

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

class KakaoOAuthClient {
    companion object {
        fun loginWithKakao(context: Context, kakaoCallback: (OAuthToken?, Throwable?) -> Unit) {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                try {
                    UserApiClient.instance.loginWithKakaoTalk(context, callback = kakaoCallback)
                } catch (error: Throwable) {
                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    // 그냥 에러를 올린다.
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) throw error

                    // 그렇지 않다면, 카카오 계정 로그인을 시도한다.
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = kakaoCallback)
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = kakaoCallback)
            }
        }
    }
}