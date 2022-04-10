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
                    Sort.birthaday -> {
                        result = birthdaySort(result)
                    }
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
                        val u = users.map { (it as State.Success).data }.first()
                        _usersStateFlow.value = State.Success(birthdaySort(u))
                    }
                    Sort.alphabet -> {
                        val sortedListUsers = users.map {
                            (it as State.Success).data
                        }.first().sortedBy { it.firstName + " " + it.lastName }
                        _usersStateFlow.value = State.Success(sortedListUsers)
                    }
                    Sort.none -> {}
                }
            }
        }
    }


    private suspend fun birthdaySort(users: List<User>): List<User> {
        val currentDate = getNowDate()
        val sortedListUsers = users
            .groupBy { parsingDate(it.birthday).month }
            .toSortedMap()
            .also { mapMonthToUser ->
                mapMonthToUser.forEach {
                    mapMonthToUser[it.key] =
                        it.value.sortedBy { user -> parsingDate(user.birthday).date }
                }
            }
        val (eqOrAboveCurrentMonth, belowCurrentMonth) = sortedListUsers.keys.partition { it >= currentDate.month }

        var belowCurDay = emptyList<User>()
        val res = (eqOrAboveCurrentMonth + belowCurrentMonth).flatMap { month ->
            if (month == currentDate.month) {
                val (eqOrAboveCurrentDay, belowCurrentDay) = sortedListUsers[month]!!.partition { user ->
                    parsingDate(user.birthday).date >= currentDate.date
                }
                belowCurDay = belowCurrentDay
                eqOrAboveCurrentDay!!
            } else {
                sortedListUsers[month]!!
            }
        }
        return res + belowCurDay
    }
}

enum class Sort {
    birthaday, alphabet, none
}