package com.romazelenin.addressbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romazelenin.addressbook.domain.UserRepository
import com.romazelenin.addressbook.domain.entity.State
import com.romazelenin.addressbook.domain.entity.User
import com.romazelenin.addressbook.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getBirthdayUsersSortUseCase: GetBirthdayUsersSortUseCase,
    private val getAlphabetUsersSort: GetAlphabetUsersSort,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getAllDepartmentsUseCase: GetAllDepartmentsUseCase
) :
    ViewModel() {

    private val _usersStateFlow = MutableStateFlow<State<out List<User>>>(State.Loading)
    private var collectLatestUsersJob: Job? = null
    private var currentSort = MutableStateFlow(Sort.none)
    private val departments = getAllDepartmentsUseCase()

    init {
        refresh()
    }

    val users: Flow<State<out List<User>>>
        get() {
            return _usersStateFlow
        }

    fun getCurrentSort(): Flow<Sort> = currentSort


    fun isCached(): Boolean {
        return userRepository.isCached()
    }

    fun refresh() {
        userRepository.setCache(true)
        collectLatestUsersJob?.cancel()
        collectLatestUsersJob = viewModelScope.launch {
                val users = when (currentSort.value) {
                    Sort.none -> {
                        getAllUsersUseCase()
                    }
                    Sort.birthaday -> {
                        getBirthdayUsersSortUseCase()
                    }
                    Sort.alphabet -> {
                        getAlphabetUsersSort()
                    }
                }
                users.collect { result ->
                    _usersStateFlow.value = when (result) {
                        State.Loading -> {
                            result
                        }
                        is State.Success -> {
                           if(userRepository.isCached()) delay(500)
                            result
                        }
                        is State.Failed -> {
                            if(userRepository.isCached()) delay(500)
                            result
                        }
                    }
                }
        }
    }

    fun getUser(userId: String): Flow<State<out User>> {
        return getUserByIdUseCase(userId)
    }

    fun setSort(sort: Sort) {
        if (currentSort.value == sort) return
        currentSort.value = sort
        refresh()
    }

    fun getDepartments() = departments

    fun editUser(user: User) {
        viewModelScope.launch {
            userRepository.update(user)
        }
    }
}

enum class Sort {
    birthaday, alphabet, none
}