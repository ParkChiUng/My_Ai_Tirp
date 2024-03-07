package com.sessac.myaitrip.data.repository.user.remote

import android.net.Uri
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.QuerySnapshot
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.data.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserRemoteDataSource: IUserRemoteDataSource {
    private val auth = MyAiTripApplication.getInstance().getFirebaseAuth()
    private val fireStore = MyAiTripApplication.getInstance().getFireStore()
    /**
     * Register
     * 회원가입 결과
     * @param email
     * @param nickname
     * @param password
     * @return
     */

    override suspend fun register(
        email: String,
        password: String,
        nickname: String,
        profileImgUrl: String?,
        newProfileImgUri: Uri?
    ): UiState<AuthResult> {
        val result = auth.createUserWithEmailAndPassword(email, password).await() // 회원가입
        val user = auth.currentUser

        return CoroutineScope(Dispatchers.IO).async {

            // 유저 정보 저장 + 프로필 사진 업로드
            user?.let { user ->
                var userProfileImgUrl = ""

                // 소셜로 제공받은 기존 프로필 사진 그대로 사용할 때
                profileImgUrl?.let {
                    userProfileImgUrl = it

                    // 유저 정보 생성
                    val userInfo = User(
                        id = user.uid,
                        email = email,
                        nickname = nickname,
                        profileImgUrl = userProfileImgUrl
                    )

                    user.addUserInfo(userInfo)
                }

                // 새로운 프로필 사진일 때
                newProfileImgUri?.let {
                    val fireStorage = MyAiTripApplication.getInstance().getFireStorage()
                    val profileImgRef = fireStorage.reference.child("profileImage").child("${user.uid}.png")
                    val uploadTask = profileImgRef.putFile(newProfileImgUri)

                    uploadTask.addOnSuccessListener {
                        profileImgRef.downloadUrl.addOnSuccessListener {
                            // 프로필 이미지 URL 가져오기
                            userProfileImgUrl = it.toString()

                            // 유저 정보 생성
                            val userInfo = User(
                                id = user.uid,
                                email = email,
                                nickname = nickname,
                                profileImgUrl = userProfileImgUrl
                            )

                            user.addUserInfo(userInfo)
                        }
                    }
                }
            }

            UiState.Success(result)
        }.await()
    }

    private fun FirebaseUser.addUserInfo(
        userInfo: User
    ) {
        fireStore.collection("user").also { userDB ->
            with(userDB.document(this.uid)) {
                set(userInfo)
            }
        }
    }

    /*override suspend fun checkExistNickname(nickname: String): UiState<QuerySnapshot> {
        // 닉네임 중복확인
        val query = fireStore.collection("user").whereEqualTo("nickname", nickname)
        val result = query.get().await()

        return UiState.Success(result)
    }
*/
    /**
     * Login
     * 로그인 결과
     * @param email
     * @param password
     * @return
     */
    override suspend fun login(email: String, password: String): UiState<AuthResult> {

        val result = auth.signInWithEmailAndPassword(email, password).await()
        return UiState.Success(result)
    }
}