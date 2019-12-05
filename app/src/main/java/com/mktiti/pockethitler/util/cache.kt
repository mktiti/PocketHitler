package com.mktiti.pockethitler.util

import java.util.*

interface LruCache<K, V> {

    val capacity: Int

    operator fun set(key: K, value: V)

    operator fun get(key: K): V?

    fun getOrCreate(key: K, creator: (key: K) -> V): V

}

class ListLruCache<K, V>(override val capacity: Int = 5) : LruCache<K, V> {

    private val list = LinkedList<Pair<K, V>>()

    private fun remove(key: K) {
        list.removeIf { (k, _) -> k == key }
    }

    override fun set(key: K, value: V) {
        remove(key)
        while (list.size >= capacity) {
            list.removeFirst()
        }
        list += key to value
    }

    override fun get(key: K): V? {
        val elem = list.find { (k, _) -> k == key } ?: return null
        remove(key)
        list += elem
        return elem.second
    }

    override fun getOrCreate(key: K, creator: (key: K) -> V): V = when (val value = get(key)) {
        null -> creator.invoke(key).apply { list += key to this}
        else -> value
    }


}