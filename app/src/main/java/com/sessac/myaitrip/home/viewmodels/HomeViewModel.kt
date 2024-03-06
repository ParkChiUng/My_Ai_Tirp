package com.sessac.myaitrip.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessac.myaitrip.data.tour.TourItem
import com.sessac.myaitrip.data.tour.repository.TourRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(){

    private val tourRepository = TourRepository.getInstance()

    private val _tourList = MutableStateFlow<List<TourItem>>(emptyList())
    val tourList: StateFlow<List<TourItem>> = _tourList

    private val dispatchers = CoroutineScope(Dispatchers.IO)

    fun getTourListByTitle(title: List<String>){
        viewModelScope.launch {
            tourRepository.getTourListByTitle(title).collect {tourList ->
                _tourList.value = tourList
            }
        }
    }
}