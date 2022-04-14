package com.romazelenin.addressbook.usecase

import com.romazelenin.addressbook.domain.entity.State
import com.romazelenin.addressbook.domain.entity.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class GetAlphabetUsersSort @Inject constructor(private val getAllUsersUseCase: GetAllUsersUseCase) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<State<out List<User>>> {
        return getAllUsersUseCase().mapLatest { result ->
            when (result) {
                is State.Failed -> {
                    if (result.data != null) State.Failed(
                        result.throwable,
                        sort(result.data)
                    ) else result
                }
                is State.Loading -> {
                    result
                }
                is State.Success -> {
                    State.Success(sort(result.data))
                }
            }
        }
    }

    private fun sort(users: List<User>): List<User> {
        return users.sortedBy { it.firstName + " " + it.lastName }
    }

}