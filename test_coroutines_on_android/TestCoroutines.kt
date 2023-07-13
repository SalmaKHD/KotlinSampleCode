/*
Topic of Investigation: Testing Coroutines using JUnit4 in Android [https://developer.android.com/kotlin/coroutines/test]
 */
package com.salmakhd.android.forpractice

import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class TestCoroutines {
    /* for learning purpose only */
    /**
     * Running tests that run parallel coroutines
     */
    // Topic 1: Running local tests
    // 1: use special functions to yield the thread and allow other coroutines to run
    @Test
    fun standardTest() = runTest() {
        val sleepDao = db.sleepRecordDao()
        val newSleepRecord1 = SleepRecord(amountInHours = 8)
        val newSleepRecord2 = SleepRecord(amountInHours = 12, date = LocalDate.of(2023, 5, 6))

        launch { sleepDao.insert(newSleepRecord1) }
        launch { sleepDao.insert(newSleepRecord2)}
        advanceUntilIdle() // yields the thread and allows other coroutines to run, no need to wait for the parent coroutine builder to return

        assertEquals(listOf(newSleepRecord1, newSleepRecord2), sleepDao.getAllRecords().first())
    }

    // start coroutines eagerly: as if they are not coroutines. coroutines will not wait for their coroutine builder to return
    // good for simple tests only
    // runTest(UnconfinedDispatcher()) acts like runBlocking()
    @Test
    fun UnconfinedRunTestDispathcer() = runTest(UnconfinedTestDispatcher()) {
        val sleepDao = db.sleepRecordDao()
        val newSleepRecord1 = SleepRecord(amountInHours = 8)
        val newSleepRecord2 = SleepRecord(amountInHours = 12, date = LocalDate.of(2023, 5, 6))

        // execute launch without waiting for parent builder to return. Similar to runBlockingTest
        launch { sleepDao.insert(newSleepRecord1) }
        launch { sleepDao.insert(newSleepRecord2)}
        assertEquals(listOf(newSleepRecord1, newSleepRecord2), sleepDao.getAllRecords().first())
    }

    // 3: inject a test dispatcher using DI
    // Why? for exerting control over all the dispatchers in which coroutines run + scheduling possible
    /*
    why is this useful? because some tests may require switching contexts regularly.
    A test dispatcher ensures that they all run in the same context for getting results as expected.
     */
    @Test
    fun TestDispatcherInjection() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val dbRepo = OfflineDatabaseRepository(dispatcher = dispatcher)
        dbRepo.saveUserData(name= "Philippe", age=29)
        advanceUntilIdle()
        assertEquals(dbRepo.getUser().first(), User(name="Philippe", age= 29))
        // bottom line: use  StandardTestDispatcher(testScheduler) for testing
        // advance clock manually for tests to be run
        // runTest() with no parameters uses the StandardDispatcher
    }

    /*
    Why all this needed? because local tests are run on the JVM meaning the Main Android thread
    is not available when running these tests
     */

    // Topic 2: Running instrumented tests
}
