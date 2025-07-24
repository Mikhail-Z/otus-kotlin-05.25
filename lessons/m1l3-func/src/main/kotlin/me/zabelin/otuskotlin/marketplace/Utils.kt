package me.zabelin.otuskotlin.marketplace


fun mapListToNames(names: List<Map<String, String>>): List<String> =
    names.map { name ->
        fullNameParts.mapNotNull {
            name[it]
        }.joinToString(" ")
    }
        .toList()

const val firstName = "first"
const val middleName = "middle"
const val lastName = "last"
val fullNameParts = listOf(lastName, firstName, middleName)
