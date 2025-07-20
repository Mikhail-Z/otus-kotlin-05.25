@file:Suppress("unused")

package ru.otus.otuskotlin.m2l1

import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

// Реализуйте dsl для составления sql запроса, чтобы все тесты стали зелеными.
class Hw1Sql {

    private fun checkSQL(expected: String, sql: SqlSelectBuilder) {
        assertEquals(expected, sql.build())
    }

    @Test
    fun `simple select all from table`() {
        val expected = "select * from table"

        val real = query {
            from("table")
        }

        checkSQL(expected, real)
    }

    @Test
    fun `check that select can't be used without table`() {
        assertFailsWith<Exception> {
            query {
                select("col_a")
            }.build()
        }
    }

    @Test
    fun `select certain columns from table`() {
        val expected = "select col_a, col_b from table"

        val real = query {
            select("col_a", "col_b")
            from("table")
        }

        checkSQL(expected, real)
    }

    @Test
    fun `select certain columns from table 1`() {
        val expected = "select col_a, col_b from table"

        val real = query {
            select("col_a", "col_b")
            from("table")
        }

        checkSQL(expected, real)
    }

    /**
     * __eq__ is "equals" function. Must be one of char:
     *  - for strings - "="
     *  - for numbers - "="
     *  - for null - "is"
     */
    @Test
    fun `select with complex where condition with one condition`() {
        val expected = "select * from table where col_a = 'id'"

        val real = query {
            from("table")
            where { "col_a" eq "id" }
        }

        checkSQL(expected, real)
    }

    /**
     * __nonEq__ is "non equals" function. Must be one of chars:
     *  - for strings - "!="
     *  - for numbers - "!="
     *  - for null - "!is"
     */
    @Test
    fun `select with complex where condition with two conditions`() {
        val expected = "select * from table where col_a != 0"

        val real = query {
            from("table")
            where {
                "col_a" nonEq 0
            }
        }

        checkSQL(expected, real)
    }

    @Test
    fun `when 'or' conditions are specified then they are respected`() {
        val expected = "select * from table where (col_a = 4 or col_b !is null)"

        val real = query {
            from("table")
            where {
                or {
                    "col_a" eq 4
                    "col_b" nonEq null
                }
            }
        }

        checkSQL(expected, real)
    }
}

class SqlSelectBuilder {
    private var table: String? = null
    private var columns: MutableList<String> = mutableListOf()
    private var where: SqlExpression? = null

    fun build(): String {
        require(table != null) { "Table not specified" }
        val selectClause = if (columns.isEmpty()) "*" else columns.joinToString(", ")
        val whereClause = if (where == null) "" else " where ${where?.build()}"
        return "select $selectClause from $table$whereClause"
    }

    fun from(tableName: String) {
        table = tableName
    }

    fun select(vararg cols: String) {
        columns.addAll(cols)
    }

    fun where(block: SqlWhereBuilder.() -> SqlExpression) {
        where = SqlWhereBuilder().block()
    }
}

interface SqlExpression {
    fun build(): String
}

data class SqlEq(val left: SqlExpression, val right: SqlExpression) : SqlExpression {
    override fun build(): String {
        return "${left.build()} = ${right.build()}"
    }
}

data class SqlNotEq(val left: SqlExpression, val right: SqlExpression) : SqlExpression {
    override fun build(): String {
        return "${left.build()} != ${right.build()}"
    }
}

data class SqlIsNull(val left: SqlExpression) : SqlExpression {
    override fun build(): String {
        return "${left.build()} is null"
    }
}

data class SqlIsNotNull(val left: SqlExpression) : SqlExpression {
    override fun build(): String {
        return "${left.build()} !is null"
    }
}

data class SqlCol(val name: String) : SqlExpression {
    override fun build(): String = name
}

sealed class SqlConst : SqlExpression {

    data class SqlString(val value: String) : SqlConst() {
        override fun build(): String = "'$value'"
    }

    data class SqlNumber(val value: Number) : SqlConst() {
        override fun build(): String = value.toString()
    }

    companion object {
        fun of(value: Any): SqlConst {
            return when (value) {
                is String -> SqlString(value)
                is Number -> SqlNumber(value)
                else -> throw IllegalArgumentException("Unsupported constant type: ${value::class}")
            }
        }
    }
}



data class SqlAnd(val left: SqlExpression, val right: SqlExpression) : SqlExpression {
    override fun build(): String {
        return "(${left.build()} and ${right.build()})"
    }
}

data class SqlOr(val left: SqlExpression, val right: SqlExpression) : SqlExpression {
    override fun build(): String {
        return "(${left.build()} or ${right.build()})"
    }
}

class SqlWhereBuilder {
    internal val expressions: MutableList<SqlExpression> = mutableListOf()

    infix fun String.eq(value: Any?): SqlExpression {
        val expr = if (value == null) {
            SqlIsNull(SqlCol(this))
        } else {
            SqlEq(SqlCol(this), SqlConst.of(value))
        }
        expressions.add(expr)
        return expr
    }

    infix fun String.nonEq(value: Any?): SqlExpression {
        val expr = if (value == null) {
            SqlIsNotNull(SqlCol(this))
        } else {
            SqlNotEq(SqlCol(this), SqlConst.of(value))
        }
        expressions.add(expr)
        return expr
    }

    fun and(block: SqlWhereBuilder.() -> Unit): SqlExpression {
        val nested = SqlWhereBuilder().apply(block)
        return nested.expressions.reduce { acc, expr -> SqlAnd(acc, expr) }
    }

    fun or(block: SqlWhereBuilder.() -> Unit): SqlExpression {
        val nested = SqlWhereBuilder().apply(block)
        return nested.expressions.reduce { acc, expr -> SqlOr(acc, expr) }
    }
}



fun query(block: SqlSelectBuilder.() -> Unit): SqlSelectBuilder {
    return SqlSelectBuilder().apply(block)
}