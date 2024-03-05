package com.sessac.myaitrip.data.repository.user.firebase

import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sessac.myaitrip.data.UiState
import com.sessac.myaitrip.data.entities.User
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository: IFirebaseAuthRepository {

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference
    private val usersRef = db.collection("user")

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
        nickname: String,
        password: String
    ): UiState<AuthResult> {
        // 회원가입
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = auth.currentUser
            // TODO. 프로필 사진 업로드
            // storageRef.child("profileImage").child("${resultUid}/profileImage.png")

            user?.let {
                usersRef.document(user.uid).set(
                    User(user_imgUrl = "", user_nickName = nickname)
                ).await()

                return UiState.Success(result)
            }

            return UiState.Error("회원 가입 실패")

        } catch (e: FirebaseAuthException) {
            val errorCode = e.errorCode
            Log.e("FirebaseAuthException", "회원가입 실패: $errorCode")
            return UiState.Error(errorCode)
        }
    }

    /**
     * Login
     * 로그인 결과
     * @param email
     * @param password
     * @return
     */
    override suspend fun login(email: String, password: String): UiState<AuthResult> {
        // 로그인
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = auth.currentUser

            user?.let {
                return UiState.Success(result)
            }

            return UiState.Error("유저 없음")
        } catch (e: FirebaseAuthException) {
            val errorCode = e.errorCode
            Log.e("FirebaseAuthException", "로그인 실패: $e, $errorCode")
            return UiState.Error(errorCode)
        }
    }
}