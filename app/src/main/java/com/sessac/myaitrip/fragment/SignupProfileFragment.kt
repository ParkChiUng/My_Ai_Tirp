package com.sessac.myaitrip.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sessac.myaitrip.databinding.FragmentSignupProfileBinding

/**
 * 프로필 추가/수정 페이지
 */
class SignupProfileFragment :
    ViewBindingBaseFragment<FragmentSignupProfileBinding>(FragmentSignupProfileBinding::inflate) {

    private lateinit var signupProfileBinding: FragmentSignupProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        signupProfileBinding = FragmentSignupProfileBinding.inflate(inflater, container, false)
        return signupProfileBinding.root
    }
}