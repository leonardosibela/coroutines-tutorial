package com.sibela.coroutines

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val RESULT_ONE = "Result 1"
        private const val RESULT_TWO = "Result 2"
    }

    private lateinit var button: Button
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button)
        textView = findViewById(R.id.textview)

        button.setOnClickListener {

            // Dispatchers.IO -> Network requests or database requests
            // Dispatchers.Main -> Work to be done in the main thread
            // Dispatchers.Default -> Heavy computation work
            CoroutineScope(Dispatchers.IO).launch {
                fakeApiRequest()
            }
        }
    }

    private suspend fun fakeApiRequest() {
        val resultOne = getResultOneFromApi()
        println("Debug: $resultOne")
        setTextOnMainThread(resultOne)

        val resultTwo = getResultTwoFromApi()
        setTextOnMainThread(resultTwo)
    }

    private fun setText(text: String) {
        val newText = textView.text.toString() + "\n$text"
        textView.text = newText
    }

    private suspend fun setTextOnMainThread(text: String) {
        // shifts the execution of the block into a different thread (Main) if a new dispatcher is specified
        withContext(Dispatchers.Main) {
            setText(text)
        }
    }

    private suspend fun getResultOneFromApi(): String {
        logThread("getResultOneFromApi()")
        // delay the single coroutine
        delay(1_000)

        // Sleep the entire thread (all the coroutines in that thread)
        // You usually wont use it!
        // Thread.sleep(1_000)

        return RESULT_ONE
    }

    private suspend fun getResultTwoFromApi(): String {
        logThread("getResultOneFromApi()")
        delay(1_000)
        return RESULT_TWO
    }

    private fun logThread(methodName: String) {
        println("Debug $methodName: ${Thread.currentThread().name}")
    }
}