package me.votond.vtlib.util.dsl.item

import org.bukkit.inventory.meta.BookMeta

data class BookPropertiesContext(
    var bookTitle: String? = null,
    var bookAuthor: String? = null,
    var bookGeneration: BookMeta.Generation? = null,
    var bookPages: MutableList<String> = mutableListOf(),
)