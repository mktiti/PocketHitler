package com.mktiti.pockethitler.util

typealias Bi<T> = Pair<T, T>

typealias Tri<T> = Triple<T, T, T>

inline fun forever(code: () -> Unit): Nothing {
    while (true) {
        code()
    }
}

fun <T> T.repeatList(length: Int) = (1..length).map { this }