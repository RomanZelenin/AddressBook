package com.romazelenin.addressbook.usecase

import com.romazelenin.addressbook.domain.UserRepository
import com.romazelenin.addressbook.domain.entity.State
import com.romazelenin.addressbook.domain.entity.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor(private val userRepository: UserRepository) {

    operator fun invoke(): Flow<State<out List<User>>> {
        return userRepository.getAllUsers()
    }
}