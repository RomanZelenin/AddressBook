package com.romazelenin.addressbook.usecase

import com.romazelenin.addressbook.domain.entity.State
import com.romazelenin.addressbook.domain.entity.User
import com.romazelenin.addressbook.getNowDate
import com.romazelenin.addressbook.parsingDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class GetBirthdayUsersSortUseCase @Inject constructor(private val getAllUsersUseCase: GetAllUsersUseCase) {

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
}