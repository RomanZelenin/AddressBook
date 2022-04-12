package com.romazelenin.addressbook

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romazelenin.addressbook.domain.UserRepository
import com.romazelenin.addressbook.domain.entity.Department
import com.romazelenin.addressbook.domain.entity.State
import com.romazelenin.addressbook.domain.entity.User
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @SuppressLint("StaticFieldLeak") @ApplicationContext private val context: Context,
    private val userRepository: UserRepository
) :
    ViewModel() {

    private val _usersStateFlow = MutableStateFlow<State<out List<User>>>(State.Loading)
    private var currentTypeSort = MutableStateFlow(Sort.none)
    private var collectLatestUsersJob: Job? = null

    fun getCurrentSort(): Flow<Sort> = currentTypeSort

    val users: Flow<State<out List<User>>> = _usersStateFlow

    init {
        refresh()
    }

    fun refresh() {
        collectLatestUsersJob?.cancel()
        collectLatestUsersJob = viewModelScope.launch {
            userRepository.getAllUsers()
                .collectLatest { result ->
                    _usersStateFlow.value = when (result) {
                        is State.Failed -> {
                            delay(500)
                            result
                        }
                        is State.Loading -> {
                            result
                        }
                        is State.Success -> {
                            delay(500)
                            when (currentTypeSort.value) {
                                Sort.birthaday -> {
                                    State.Success(birthdaySort(result.data))
                                }
                                Sort.alphabet -> {
                                    State.Success(result.data.sortedBy { it.firstName + " " + it.lastName })
                                }
                                Sort.none -> {
                                    result
                                }
                            }
                        }
                    }
                }
        }
    }

    fun getUser(userId: String): Flow<User> {
        return userRepository.getUserById(userId)
    }

    fun sortedUsers(sort: Sort) {
        if (currentTypeSort.value == sort) return
        currentTypeSort.value = sort

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
                eqOrAboveCurrentDay
            } else {
                sortedListUsers[month]!!
            }
        }
        return res + belowCurDay
    }

    private val departments = listOf(
        Department.all to context.getString(R.string.all),
        Department.android to context.getString(R.string.android),
        Department.design to context.getString(R.string.design),
        Department.analytics to context.getString(R.string.analytics),
        Department.backend to context.getString(R.string.backend),
        Department.back_office to context.getString(R.string.back_office),
        Department.frontend to context.getString(R.string.frontend),
        Department.hr to context.getString(R.string.hr),
        Department.ios to context.getString(R.string.ios),
        Department.management to context.getString(R.string.management),
        Department.pr to context.getString(R.string.pr),
        Department.qa to context.getString(R.string.qa),
        Department.support to context.getString(R.string.support)
    )

    fun getDepartments() = departments
}

enum class Sort {
    birthaday, alphabet, none
}