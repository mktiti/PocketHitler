package com.mktiti.pockethitler

typealias Bi<T> = Pair<T, T>

typealias Tri<T> = Triple<T, T, T>

sealed class Either<L, R>

data class Left<L, R>(val value: L) : Either<L, R>()

data class Right<L, R>(val value: R) : Either<L, R>()

inline fun forever(code: () -> Unit): Nothing {
    while (true) {
        code()
    }
}

fun <T> T.repeatList(length: Int) = (1..length).map { this }