package com.romazelenin.addressbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romazelenin.addressbook.domain.UsersServiceApi
import com.romazelenin.addressbook.domain.entity.State
import com.romazelenin.addressbook.domain.entity.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val usersServiceApi: UsersServiceApi) :
    ViewModel() {

    private val _usersStateFlow = MutableStateFlow<State<List<User>>>(State.Loading())
    private var currentTypeSort = Sort.none

    fun getCurrentSort() = currentTypeSort

    val users: Flow<State<List<User>>> = _usersStateFlow

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                _usersStateFlow.value = State.Loading()
                var result = usersServiceApi.getUsers()
                when (currentTypeSort) {
                    Sort.birthaday -> {}
                    Sort.alphabet -> {
                        result = result.sortedBy { it.firstName + " " + it.lastName }
                    }
                    Sort.none -> {}
                }
                _usersStateFlow.value = State.Success(result)
            } catch (e: Throwable) {
                _usersStateFlow.value = State.Failed(e, null)
            }
        }
    }

    fun getUser(userId: String): Flow<User?> {
        return users.map { (it as State.Success).data.firstOrNull { it.id == userId } }
    }

    fun sortedUsers(sort: Sort) {
        if (currentTypeSort == sort) return
        currentTypeSort = sort

        viewModelScope.launch {
            if (_usersStateFlow.value is State.Success) {
                when (sort) {
                    Sort.birthaday -> {
                        //TODO
                    }
                    Sort.alphabet -> {
                        val sortedListUsers = users.map {
                            (it as State.Success).data
                        }.first()
                            .sortedBy { it.firstName + " " + it.lastName }
                        _usersStateFlow.value = State.Success(sortedListUsers)
                    }
                    Sort.none -> TODO()
                }
            }
        }
    }

}

enum class Sort {
    birthaday, alphabet, none
}