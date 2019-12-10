package com.mktiti.pockethitler.util

object NopResourceManager : ResourceManager {

    override fun get(key: Int): String  = key.toString()

    override fun format(key: Int, vararg formatObjects: Any?): String = "$key: " + formatObjects.joinToString(", ")

    override fun resources(key: Int): List<String> = generateSequence { "$key #it" }.take(100).toList()

}
