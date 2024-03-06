package com.sessac.myaitrip.data.repository.user.remote

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.data.entities.User
import kotlinx.coroutines.NonCancellable.isCancelled
import kotlinx.coroutines.tasks.await

class UserRemoteDataSource: IUserRemoteDataSource {

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference
    private val userDB = db.collection("user")

    /**
     * Register
     * 회원가입 결과
     * @param email
     * @param nickname
     * @param password
     * @return
     */

    override suspend fun register(email: String, nickname: String, password: String): UiState<AuthResult> {
        // 회원가입
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            val user = auth.currentUser
            // TODO. 프로필 사진 업로드
            // storageRef.child("profileImage").child("${resultUid}/profileImage.png")

            user?.let { user ->
                userDB.document(user.uid).set(
                    User(id = user.uid,
                        email = email,
                        nickname = nickname,
                        profileImgUrl = "")
                ).await()
            }

            UiState.Success(result)

        } catch (exception: FirebaseAuthException) {
            val errorCode = exception.errorCode
            Log.e("FirebaseAuthException", "회원가입 실패: $errorCode")

            UiState.FirebaseAuthError(exception)
        }
    }

    override suspend fun checkExistNickname(nickname: String): UiState<Boolean> {
        // 닉네임 중복확인
        return try {
            var isExist = false
            var failException: Exception? = null
            var isCancelled = false

            val query = userDB.whereEqualTo("nickname", nickname)
            query.get()
                .addOnSuccessListener { isExist = !it.isEmpty }
                .addOnFailureListener { failException = it }
                .addOnCanceledListener { isCancelled = true }

            if(failException == null && !isCancelled && !isExist) UiState.Success(false)
            else if( failException != null ) UiState.Error(failException!!)
            else UiState.Empty

        } catch(exception: FirebaseFirestoreException) {
            val errorCode = exception.code.name
            Log.e("FirebaseStoreException", errorCode)

            UiState.Error(exception)
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

        val result = auth.signInWithEmailAndPassword(email, password).await()
        return UiState.Success(result)
        // 로그인
//        return try {
//            val result = auth.signInWithEmailAndPassword(email, password).await()
//
//            UiState.Success(result)
//        } catch (exception: FirebaseAuthException) {
//            val errorCode = exception.errorCode
//            Log.e("FirebaseAuthException", "로그인 실패: $exception, $errorCode")
//
//            UiState.FirebaseAuthError(exception)
//        }
    }
}