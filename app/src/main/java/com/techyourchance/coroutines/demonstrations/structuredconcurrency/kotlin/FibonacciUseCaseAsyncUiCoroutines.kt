package com.techyourchance.coroutines.demonstrations.structuredconcurrency.kotlin

import kotlinx.coroutines.*
import java.math.BigInteger

// In production the bgDispatcher have to be injected with the background Dispatcher
// While in testing we can inject the TestCoroutineDispatcher
internal class FibonacciUseCaseAsyncUiCoroutines(private val bgDispatcher: CoroutineDispatcher) {

    interface Callback {
        fun onFibonacciComputed(result: BigInteger?)
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun computeFibonacci(index: Int, callback: Callback) {
        coroutineScope.launch {
            val result = computeFibonacciBg(index)
            callback.onFibonacciComputed(result)
        }
    }

    private suspend fun computeFibonacciBg(index: Int): BigInteger
    {
        return withContext(bgDispatcher) {
            if (index == 0) {
                BigInteger("0")
            } else if (index == 1) {
                BigInteger("1")
            } else {
                computeFibonacciBg(index - 1).add(computeFibonacciBg(index - 2))
            }
        }
    }

}