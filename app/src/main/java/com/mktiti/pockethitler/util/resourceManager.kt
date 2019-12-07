package com.mktiti.pockethitler.util

import android.content.res.Resources

interface ResourceManager {

    operator fun get(key: Int): String

    fun format(key: Int, vararg formatObjects: Any?): String

    fun resources(key: Int): List<String>

}

class DefaultResourceManager(private val resources: Resources) : ResourceManager {

    override operator fun get(key: Int) = resources.getString(key)

    override fun format(key: Int, vararg formatObjects: Any?) = resources.getString(key, *formatObjects)

    override fun resources(key: Int) = resources.getStringArray(key).toList()

}
