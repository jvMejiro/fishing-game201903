package xyz.jvmejiro.fishing_game201903_core

import kotlin.reflect.KClass

class IllegalSignException : RuntimeException()

class IllegalEntityException(vararg notFoundComponent: KClass<*>) : RuntimeException()