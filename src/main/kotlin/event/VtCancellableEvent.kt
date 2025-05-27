package me.votond.vtlib.event

import org.bukkit.event.Cancellable

abstract class VtCancellableEvent(isAsync: Boolean = false) : VtEvent(isAsync), Cancellable {
    private var isCancelled: Boolean = false

    override fun isCancelled() = isCancelled
    override fun setCancelled(cancel: Boolean) {
        isCancelled = cancel
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VtCancellableEvent) return false

        if (isCancelled != other.isCancelled) return false

        return true
    }

    override fun hashCode(): Int {
        return isCancelled.hashCode()
    }

    override fun toString(): String {
        return "VtCancellableEvent(isCancelled=$isCancelled)"
    }
}