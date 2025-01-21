package me.zabelin.otuskotlin.marketplace

import kotlin.math.abs

fun diffArea(f1: Figure, f2: Figure) = abs(f1.area() - f2.area())