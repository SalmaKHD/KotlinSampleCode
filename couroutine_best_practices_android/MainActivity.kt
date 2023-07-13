/*
TOPIC OF INVESTIGATION: Coroutine Best Practices in Android
Reference: [https://developer.android.com/kotlin/coroutines/coroutines-best-practices#viewmodel-coroutines]
 */
package com.salmakhd.android.forpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

      //1: explicitly specify dispatchers in functions that change context. why? for tests
      abstract class Repository(
          private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
      ) {
          private suspend fun fetchData() = withContext(defaultDispatcher) {
              // perform IO operation & return result
          }

          //2: all suspending functions must be main-safe meaning using withContext() when doing
          // heavy computations mandatory
          private suspend fun fetchCredentials()= withContext(defaultDispatcher) {
              // fetchCredentialsFromServer()
          }

          // 3: do not expose suspend functions to composables
          // bad
          suspend fun fetchNews() = withContext(Dispatchers.IO) { // let uiState pass data to composables only
              // fetchNewsFromServer()
          }

          // 4: do not expose mutable types to composables
          // good
          private val _uiState = MutableStateFlow(false)
          val uiString: StateFlow<Boolean> = _uiState

          // the data layer should expose suspend functions and flows
          // good
          abstract suspend fun getUserName(): String // can used in DbRepository interface

          // 5: if the data layer has to launch coroutines: should the coroutine outlive the caller?
          // no.
          // use coroutineScope
          suspend fun saveToDb(userName: String) {
              coroutineScope {
                  // repo.saveUserName(userName)
              }
          }
          // yes.
          // use a coroutineScope
          inner class UserRepo(
              private val externalScope: CoroutineScope
          ) {
              suspend fun saveToDb(userName: String) {
                  externalScope.launch {
                      // repo.saveToDb(userName)
                  }.join()
              }
          }

          // 6 + 7: inject test dispatchers for more control over coroutines that run,
          // avoid using GlobalScope (or any other hard-coded scope into your classes
          // in different contexts in original code
          /*
           @Test
           fun exampleTest() = runTest {
           // create Unconfined Dispatcher for simple tests: blocking
           val dispatcher = UnconfinedTestDispatcher(testScheduler)
           // pass this dispatcher to classes that need a dispatcher
           val viewModel = UserDataViewModel(dispatcher = dispatcher)
            // run coroutines as needed
            viewModel.getUserData()
           viewModel.saveUserDataToDb(UserName(name="Philippe", age=29)
          }
           */

          // 8: cancellation in coroutines is cooperative meaning
          // if the job of a coroutine is cancelled, the coroutine itself is
          // cancelled only if it checks the status of its parent job => check parent job status
          // regularly in blocking coroutines
          suspend fun readFiles(filenames: List<String>) {
              coroutineScope {
                  for (filename in filenames) {
                      launch {
                          // check if the parent job is still active
                          // note: suspending coroutines are cancellable: no need for calling ensureActive() here: coroutineScope() cancellable
                          ensureActive()
                          // readFile(filename)
                      }
                  }
              }
          }

          // 9: don't forget to check for exceptions if they may be thrown in coroutine block
          // 10: do not catch exceptions of Exception or CancellationException  types ->
          // they are used as part of Kotlin's mechanism for handling coroutines
          suspend fun getUserNameFromServer(): String? {
              val userName: String?
              coroutineScope {
                  try {
                      launch {
                          // fetchUserName()
                          // return userName
                      }
                  } catch (e: NetworkErrorException) {
                      // update UI, retry if needed
                  }
              }
              return null
          }
      }
    }
}
