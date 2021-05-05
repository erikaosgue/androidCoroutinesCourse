package com.techyourchance.coroutines.exercises.exercise7

import com.techyourchance.coroutines.common.TestUtils
import com.techyourchance.coroutines.common.TestUtils.printCoroutineScopeInfo
import com.techyourchance.coroutines.common.TestUtils.printJobsHierarchy
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import java.lang.Exception
import kotlin.coroutines.EmptyCoroutineContext

class Exercise7Test {

    /*
    Write nested withContext blocks, explore the resulting Job's hierarchy, test cancellation
    of the outer scope
    //R: There is no anything different in this nested coroutine, there is no any problem by doing this
    // Just create a new scope, changes the dispatchers and does structure concurrency
     */
    @Test
    fun nestedWithContext() {
	    println("\n****** nested WithContext ****** \n")
        runBlocking {
	        // scopeJob will remain live for ever or until it gets cancel
            val scopeJob = Job()
            // Creating the Coroutine Scope which will contain the Job Parent scopeJob as context
            val scope = CoroutineScope(scopeJob + CoroutineName("outer scope") + Dispatchers.IO)

            // Creating the Child #1 Job of the scopeJob Parent
            scope.launch {
                delay(100)
                try {
                    //Creating a grandChild Job of the scopeJob Parent
                    withContext(CoroutineName("WithContext Child #1")) {
                        try {
                            delay(100)
                            // Creating a great grandChild from scopeJob Parent
                            withContext(CoroutineName("WithContext grandChild #1")) {
                                try {
                                    delay(100)
                                    printJobsHierarchy(scopeJob)
                                    println("completed withContext grandchild #1")
                                } catch (e: CancellationException) {
                                    println("Cancel WithContext grandChild #1")
                                }
                            }
                            println("Completed WithContext Child #1")
                        } catch (e: CancellationException) {
                            println("Cancel WithContext Child #1")

                        }
                    }
                    println("Completed Parent Scope")
                } catch (e: CancellationException) {
                    println("Cancel Parent scope")
                }
            }

            launch {
                delay(350)
                scopeJob.cancel()
            }
            scopeJob.join()
            println("test done")
        }
    }

    /*
    Launch new coroutine inside another coroutine, explore the resulting Job's hierarchy, test cancellation
    of the outer scope, explore structured concurrency
    R:
     */
    @Test
    fun nestedLaunchBuilders() {
        println("\n****** nested Launch Builders ****** \n")
        runBlocking {
            val scopeJob = Job()
            // Creating the Coroutine Scope which will contain the Job Parent scopeJob as context
            val scope = CoroutineScope(scopeJob + CoroutineName("outer scope") + Dispatchers.IO)

            // Creating the Child #1 Job of the scopeJob Parent
            val job = scope.launch {
                delay(100)
                try {
                    //Creating a grandChild Job of the scopeJob Parent
                    withContext(CoroutineName("WithContext Child #1")) {
                        try {
                            delay(100)
                            // Creating a great grandChild from scopeJob Parent
                            launch (CoroutineName("WithContext grandChild #1")) {
                                try {
                                    delay(100)
                                    printJobsHierarchy(scopeJob)
                                    println("completed withContext grandchild #1")
                                } catch (e: CancellationException) {
                                    println("Cancel WithContext grandChild #1")
                                }
                            }
                            println("Completed WithContext Child #1")
                        } catch (e: CancellationException) {
                            println("Cancel WithContext Child #1")

                        }
                    }
                    println("Completed Parent Scope")
                } catch (e: CancellationException) {
                    println("Cancel Parent scope")
                }
            }

            launch {
                delay(350)
                scopeJob.cancel()
            }
            job.join()
            println("test done")
        }
    }

    /*
    Launch new coroutine on "outer scope" inside another coroutine, explore the resulting Job's hierarchy,
    test cancellation of the outer scope, explore structured concurrency
     */
    @Test
    fun nestedCoroutineInOuterScope() {
        println("\n****** nested Coroutine with outer Scope ****** \n")
        runBlocking {
            val scopeJob = Job()
            // Creating the Coroutine Scope which will contain the Job Parent scopeJob as context
            val scope = CoroutineScope(scopeJob + CoroutineName("outer scope") + Dispatchers.IO)

            // Creating the Child #1 Job of the scopeJob Parent
            val job = scope.launch {
                delay(100)
                try {
                    //Creating a grandChild Job of the scopeJob Parent
                    withContext(CoroutineName("WithContext Child #1")) {
                        delay(100)
                        try {
                            delay(100)
                            // Creating a great grandChild from scopeJob Parent
                            scope.launch (CoroutineName("Child #2--")) {
                                try {
                                    delay(100)
                                    printJobsHierarchy(scopeJob)
                                    println("completed withContext grandchild #1--")
                                } catch (e: CancellationException) {
                                    println("Cancel WithContext grandChild #1--")
                                }
                            }
                            println("Completed Child #2-- ")
                            delay(100)
                        } catch (e: CancellationException) {
                            println("Cancel WithContext Child #1")

                        }
                    }
                    println("Completed Parent Scope")
                    delay(100)
                } catch (e: CancellationException) {
                    println("Cancel Parent scope")
                }
            }

            launch {
                delay(450)
                scopeJob.cancel()
            }
            job.join()
            println("test done")
        }
    }


}