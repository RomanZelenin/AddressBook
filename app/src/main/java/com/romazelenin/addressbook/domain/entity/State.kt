package com.romazelenin.addressbook.domain.entity

sealed class State<T> {

    object Loading : State<Nothing>()

    class Success<T>(val data: T) : State<T>()

    class Failed<T>(val throwable: Throwable, val data: T?) : State<T>()
}
