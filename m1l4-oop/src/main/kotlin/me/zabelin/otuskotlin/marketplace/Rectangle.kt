package me.zabelin.otuskotlin.marketplace

open class Rectangle(val width: Int, val height: Int): Figure {
    override fun area(): Int = width * height

    override fun toString() = "${this::class.simpleName}(${width}x${height})"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rectangle

        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        return result
    }
}