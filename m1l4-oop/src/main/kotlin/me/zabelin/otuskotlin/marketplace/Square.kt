package me.zabelin.otuskotlin.marketplace

class Square(length: Int) : Rectangle(length, length) {
    override fun toString() = "${this::class.simpleName}($width)"
}