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
            // what if the job is cancelled? use finally to do clean up
            try {
                repeat(3) {
                    // if the coroutine is canceled or not
                    if (isActive) {
                        Log.i("MAIN", "Coroutine still in active state and not canceled.")
                        // suspend the coroutine for 3 sec
                        delay(1000)
                    }
                }
            } finally {
                Log.d("MAIN", "Cleaning up coroutine resources before canceling it...")
                // what if we have to call a suspending function here?
                // use this:
                withContext(NonCancellable) {
                    doNetworkCall()
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

            // launch a timed coroutine without having to handle the TimeoutCancellationException manually
            /*
            what happens when the time exceeds 3 seconds? an exception is thrown to be gracefully handled by functions
            that are part of the coroutines library.
            what happens if there are none functions to handle the exception? an exception will be thrown unless
            we specify this function.
             */
            withTimeoutOrNull(3000) {
                doNetworkCall()
            }
            
            // lazily start a coroutine (not started until await() is called)
            GlobalScope.launch {
                val lazyJob = async(start = CoroutineStart.LAZY) {
                    // this coroutine will be executed only if await() is called (or start() on Job)
                    doNetworkCall()
                }
                // start coroutine execution
                // why using this alone is dangerous: 
                /*
                What happens if the program execution terminated after this line? The coroutine will never be cancelled!
                 */
                lazyJob.start()
                /*
                Solution? use a suspending function to ensure the function won't return
                until all execution is complete.
                 */
                // better solution:
                coroutineScope { 
                    lazyJob.start()
                }
                // get the result
                val result = lazyJob.await()
                
                // create a new thread for a coroutine
                launch(newSingleThreadContext("custom thread")) {
                    doNetworkCall()
                }
            }
        }

        // launch a custom coroutine scope
        /* behavior: it will not complete until all the child coroutines are finished */
        runBlocking {
            // similar to withContext, the coroutine will not return until execution is complete
            coroutineScope { // will not return until all its child coroutines are done executing
                doNetworkCall()
                //concurrent execution of coroutines, function won't return until execution is complete
                launch {
                    delay(3000)
                }
                launch {
                    delay(2000)
                }
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
