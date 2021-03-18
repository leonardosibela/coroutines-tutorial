package com.sibela.coroutines

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class CoroutineJobsActivity : AppCompatActivity() {

    companion object {
        private const val PROGRESS_MAX = 100
        private const val PROGRESS_START = 0
        private const val JOB_TIME = 4000 //ms
    }

    private lateinit var button: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var textView: TextView

    private lateinit var job: CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_jobs)

        button = findViewById(R.id.button)
        progressBar = findViewById(R.id.progress_bar)
        textView = findViewById(R.id.textView)

        button.setOnClickListener {
            if (!::job.isInitialized) {
                initJob()
            }
            progressBar.startJobOrCancel(job)
        }
    }

    fun initJob() {
        button.text = "Start Job 1"
        updateJobCompleteTextView("")
        job = Job()
        job.invokeOnCompletion {
            it?.message.let {
                var msg = it
                if (msg.isNullOrBlank()) {
                    msg = "Unknow cancellation error."
                }
                println("$job was cancelled. Reason: $msg")
                showToast(msg)
            }
        }
        progressBar.max = PROGRESS_MAX
        progressBar.progress = PROGRESS_START
    }

    private fun showToast(message: String) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(this@CoroutineJobsActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun ProgressBar.startJobOrCancel(job: Job) {
        if (this.progress > 0) {
            println("$job is already active. Cancelling")
            restJob()
        } else {
            button.text = "Cancel job 1"
            val scope = CoroutineScope(Dispatchers.IO + job).launch {
                println("Coroutine $this is activated with job $job")
                for (i in PROGRESS_START..PROGRESS_MAX) {
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                updateJobCompleteTextView("Job is completed")
            }

            // job.cancel() // cancel that single job
            // scope.cancel() cancel all jobs within that scope
        }
    }

    private fun restJob() {
        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Resetting job"))
        }
        initJob()
    }

    private fun updateJobCompleteTextView(text: String) {
        GlobalScope.launch(Dispatchers.Main) {
            textView.text = text
        }
    }
}