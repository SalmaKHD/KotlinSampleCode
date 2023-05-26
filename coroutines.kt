/* 
Topics od investigation: 
1. coroutines
2. jobs
3. suspend functions
4. runBlocking{}
5. lifecycle-aware coroutines
6. context and dispatchers
7. async{} and await()
8. withConext() {}
*/
package com.salmakhd.forpracticelocal

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // GlobalScope.launch{} launches a coroutines whose lifecycle follows
        // the lifecycle of the application, when the application is closed,
        // this coroutine is cancelled automatically
        val job = GlobalScope.launch(Dispatchers.IO) {
            // this coroutine runs in the thread dedicated to IO operations
            Log.i("MAIN", "Executing coroutine in context IO. Coroutine currently running in context: ${Thread.currentThread().name}")
            // switch the context of the coroutine
            withContext(Dispatchers.Main) { // Main will not block the main thread, meant for operations that requires updating the UI
                /* TODO: update Ui */
                Log.i("MAIN", "Switching context to: MAIN. Coroutine currently running in context: ${Thread.currentThread().name}")
            }
            // checks if the
                repeat(3) {
                    // if the coroutine is canceled or not
                    if (isActive) {
                        Log.i("MAIN", "Coroutine still in active state and not canceled.")
                        // suspend the coroutine for 3 sec
                        delay(1000)
                    }
                }
            withTimeout(3000) {// important point
                repeat(3) {
                    Log.i("MAIN", "Execution taking less than 3 seconds...")
                }
            }
            // execute a suspend function from within a coroutine
            val networkCallResult = doNetworkCall()
        }

        // runBlocking{} provides a coroutine scope that will block the main thread
        runBlocking {
            // job returned by launch{} helps us operate on the coroutine
            job.join()
            // cancel the coroutine
            job.cancel() // not effective here
            // until the coroutine is finished executing, this line won't print
            Log.i("MAIN", "Executing coroutine with runBlocking{}. Coroutine currently running in: ${Thread.currentThread().name}")
        }

        // demonstration of async and await
        GlobalScope.launch(Dispatchers.IO) {
            /*
            difference between async and launch? launch{} returns a job, async returns a Deferred object
            containing the result of the last expression in the block
             */
            val result1 = async {
                doNetworkCall()
            }
            // how to make the coroutine wait for the execution of async{} to complete?
            result1.await()

            // run two coroutines in parallel
            launch {
                doNetworkCall() // assume return value unimportant
            }
            launch {
                doNetworkCall() // assume return value unimportant
            }

            // launch a coroutine whose lifecycle is attached to the lifecycle of the parent scope
            lifecycleScope.launch {
                doNetworkCall()
            }
        }
    }
    // create a suspend function
    private suspend fun doNetworkCall(): String {
        // simulate network call
        delay(3000)
        // force the function to run in a specific scope
        // suspend until the execution is complete
        withContext(Dispatchers.IO) {
            Log.i("MAIN", "Executing doNetworkCall() from ${Thread.currentThread().name}")
        }
        return "So called result of doNetworkCall()"
    }
}
