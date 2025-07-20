package ru.otus.otuskotlin.m2l1.sql

@DslMarker
annotation class QueryDsl

@QueryDsl
fun buildQuery(block: QueryBuilder.() -> Unit): QueryBuilder {
    return QueryBuilder().apply(block)
}