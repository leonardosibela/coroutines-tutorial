package com.sibela.coroutines

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class AsyncAndAwaitActivity : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_async_and_await)

        button = findViewById(R.id.button)
        textView = findViewById(R.id.textview)

        button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                fakeApiRequest()
            }
        }
    }

    private suspend fun fakeApiRequest() {
        CoroutineScope(Dispatchers.IO).launch {
            val executionTime = measureTimeMillis {

                // You get the result outside the coroutine
                val resultOne: Deferred<String> = async {
                    println("Debug: launching job one: ${Thread.currentThread().name}")
                    getResultOneFromApi()
                }

                val resultTwo: Deferred<String> = async {
                    println("Debug: launching job one: ${Thread.currentThread().name}")
                    getResultTwoFromApi()
                }

                // when you call the await() is when you actually get the value returned
                // Of course it will only run when the coroutine returns the value
                setTextOnMainThread("Got ${resultOne.await()}")
                setTextOnMainThread("Got ${resultTwo.await()}")
            }
            println("Debug: total time elapsed $executionTime")
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