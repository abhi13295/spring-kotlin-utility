package com.outleap.demo.lock

import org.springframework.integration.support.locks.LockRegistry

fun takeLock(lockRegistry: LockRegistry, lockKey: String, block: () -> Any): Any? {
    val obtain = lockRegistry.obtain(lockKey)
    obtain.lock()
    try {
        return block()
    } finally {
        obtain.unlock()
    }
}