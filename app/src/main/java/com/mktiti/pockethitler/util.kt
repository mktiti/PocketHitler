package com.mktiti.pockethitler

typealias Bi<T> = Pair<T, T>

fun <T> bi(value: T) = Bi(value, value)

typealias Tri<T> = Triple<T, T, T>

fun <T> tri(value: T) = Triple(value, value, value)

sealed class Either<L, R>

data class Left<L, R>(val value: L) : Either<L, R>()

data class Right<L, R>(val value: R) : Either<L, R>()

inline fun forever(code: () -> Unit): Nothing {
    while (true) {
        code()
    }
}

fun <T> T.repeatList(length: Int) = (1..length).map { this }