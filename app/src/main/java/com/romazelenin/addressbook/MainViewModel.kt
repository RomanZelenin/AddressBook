package com.romazelenin.addressbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romazelenin.addressbook.domain.UsersServiceApi
import com.romazelenin.addressbook.domain.entity.State
import com.romazelenin.addressbook.domain.entity.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val usersServiceApi: UsersServiceApi) :
    ViewModel() {

    private val _usersStateFlow = MutableStateFlow<State<List<User>>>(State.Loading())
    val users: Flow<State<List<User>>> = _usersStateFlow

    init { refresh() }

    fun refresh(){
        viewModelScope.launch {
            try {
                _usersStateFlow.value = State.Loading()
                val result = usersServiceApi.getUsers()
                _usersStateFlow.value = State.Success(result)
            } catch (e: Throwable) {
                _usersStateFlow.value = State.Failed(e, null)
            }
        }
    }

}