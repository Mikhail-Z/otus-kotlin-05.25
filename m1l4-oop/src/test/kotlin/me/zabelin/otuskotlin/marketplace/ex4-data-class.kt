package me.zabelin.otuskotlin.marketplace

data class Product(
    val id: ProductId,
    val name: String,
) {

    @JvmInline
    value class ProductId(
        val value: Long
    )
}