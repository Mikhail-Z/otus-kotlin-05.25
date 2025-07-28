package ru.otus.otuskotlin.coroutines.homework.easy

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class HWEasy {

    @Test
    fun easyHw() = runBlocking {
        val numbers = generateNumbers()
        val toFind = 10
        val toFindOther = 1000

//        val foundNumbers = listOf(
//            findNumberInList(toFind, numbers),
//            findNumberInList(toFindOther, numbers)
//        )
        val foundNumbers = listOf(toFind, toFindOther).map {
            async { findNumberInList(it, numbers) }
        }.awaitAll()

        foundNumbers.forEach {
            if (it != -1) {
                println("Your number $it found!")
            } else {
                println("Not found number $toFind || $toFindOther")
            }
        }
    }
}