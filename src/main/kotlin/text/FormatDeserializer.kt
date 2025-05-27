package me.votond.vtlib.text

import net.kyori.adventure.text.Component

fun interface FormatDeserializer {
    fun deserialize(string: String): Component
}