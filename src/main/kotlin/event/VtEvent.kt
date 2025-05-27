package me.votond.vtlib.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

abstract class VtEvent(isAsync: Boolean = false) : Event(isAsync) {
    companion object {
        @JvmField
        protected val handlerList = HandlerList()
        fun getHandlerList() = handlerList
    }

    override fun getHandlers() = handlerList

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VtEvent) return false

        if (eventName != other.eventName) return false

        return true
    }

    override fun hashCode(): Int {
        return eventName.hashCode()
    }

    override fun toString(): String {
        return "VtEvent(eventName='$eventName')"
    }
}