package com.sessac.myaitrip.presentation.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.sessac.myaitrip.fragment.FragmentInflate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

abstract class ViewBindingBaseFragment<VB: ViewBinding>
    (private val inflate: FragmentInflate<VB>): Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    /**
     * Bind
     * func을 flow의 각 Stream마다 uiScope에서 수행
     * @param T : Flow Type
     * @param func : 각 Stream에 대한 행위
     * @receiver
     */
    fun <T> Flow<T>.bind(func: (T) -> Unit) = onEach { func(it) }.launchIn(lifecycleScope)

    fun View.throttleClick(): Flow<Unit> = this.clicks().throttleFirst()    // FlowBinding ThrottleClick

    /**
     * Throttle first
     * 중복 클릭 방지
     * @param T
     * @return
     */
    private fun <T> Flow<T>.throttleFirst(): Flow<T> = flow {
        var lastEmissionTime = 0L
        collect { upstream ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastEmissionTime > 700L) {
                lastEmissionTime = currentTime
                emit(upstream)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}