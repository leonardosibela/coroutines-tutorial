package com.sibela.coroutines

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class ParallelBackgroundTasksActivity : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parallel_background_tasks)

        button = findViewById(R.id.button)
        textView = findViewById(R.id.textview)

        button.setOnClickListener {
            setNewText("Clicked")

            CoroutineScope(Dispatchers.IO).launch {
                fakeApiRequest()
            }
        }
    }

    private suspend fun fakeApiRequest() {
        val startTime = System.currentTimeMillis()
        val parentJob = CoroutineScope(Dispatchers.IO).launch {
            val jobOne = launch {
                val timeOne = measureTimeMillis {
                    println("Debug: launching job 1 - Thread ${Thread.currentThread().name}")
                    val resultOne = getResultOneFromApi()
                    setTextOnMainThread("Got $resultOne")
                }
                println("Completed job 1 in $timeOne ms.")
            }

            // jobOne.join() this would ask to wait for job one to finish

            val jobTwo = launch {
                val timeTwo = measureTimeMillis {
                    println("Debug: launching job 1 - Thread ${Thread.currentThread().name}")
                    val resultTwo = getResultTwoFromApi()
                    setTextOnMainThread("Got $resultTwo")
                }
                println("Completed job 1 in $timeTwo ms.")
            }
        }
        parentJob.invokeOnCompletion {
            println("Debug: total elapse time: ${System.currentTimeMillis() - startTime}")
        }
    }

    private fun setNewText(message: String) {
        val newText = textView.text.toString() + "\n$message"
        textView.text = newText
    }

    private suspend fun setTextOnMainThread(message: String) {
        withContext(Dispatchers.Main) {
            setNewText(message)
        }
    }

    private suspend fun getResultOneFromApi(): String {
        delay(1_000)
        return "Result 1"
    }

    private suspend fun getResultTwoFromApi(): String {
        delay(1_700)
        return "Result 2"
    }
}
