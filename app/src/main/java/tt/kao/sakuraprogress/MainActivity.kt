package tt.kao.sakuraprogress

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.SeekBar
import tt.kao.sakuraprogress.ui.SakuraProgress

class MainActivity : AppCompatActivity(), DemoTask.Callback {

    private lateinit var sakuraProgress: SakuraProgress
    private lateinit var progressSeekBar: SeekBar
    private lateinit var demoBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViews()
    }

    private fun findViews() {
        sakuraProgress = findViewById(R.id.sakuraProgress)
        progressSeekBar = findViewById(R.id.progressSeekBar)
        demoBtn = findViewById(R.id.demo_btn)

        progressSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) return

                sakuraProgress.progress = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        demoBtn.setOnClickListener {
            val task = DemoTask(this)
            task.execute()
        }
    }

    override fun updateProgress(progress: Int) {
        sakuraProgress.progress = progress
    }
}
