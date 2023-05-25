package com.salmakhd.forpracticelocal

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // GlobalScope.launch{} launches a coroutines whose lifecycle follows
        // the lifecycle of the application, when the application is closed,
        // this coroutine is cancelled automatically
        GlobalScope.launch(Dispatchers.IO) {
            // this coroutine runs in the thread dedicated to IO operations
            Log.i("MAIN", "Coroutine currently running in context: ${Thread.currentThread().name}")
            // switch the context of the coroutine
            withContext(Dispatchers.Main) { // Main will not block the main thread
                /* TODO: update Ui */
                Log.i("MAIN", "Coroutine currently running in context: ${Thread.currentThread().name}")

            }
            // suspend the coroutine for 3 sec
            delay(1000)
            // execute a suspend function from within a coroutine
            val networkCallResult = doNetworkCall()
        }

        // runBlocking{} provides a coroutine scope that will block the main thread
        runBlocking {
            Log.i("MAIN", "Coroutine currently running in: ${Thread.currentThread().name}")
        }
    }
    // create a suspend function
    private suspend fun doNetworkCall(): String {
        // simulate network call
        delay(3000)
        return "This is the result"
    }
}
