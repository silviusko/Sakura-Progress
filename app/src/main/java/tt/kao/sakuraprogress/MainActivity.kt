package tt.kao.sakuraprogress

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import tt.kao.sakuraprogress.ui.SakuraProgress

class MainActivity : AppCompatActivity(), DemoTask.Callback {

    private lateinit var sakuraProgress: SakuraProgress
    private lateinit var progressLabel: TextView
    private lateinit var petalNumLabel: TextView
    private lateinit var progressSeekBar: SeekBar
    private lateinit var petalNumSeekBar: SeekBar
    private lateinit var demoBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViews()
        bindViews()
    }

    private fun findViews() {
        sakuraProgress = findViewById(R.id.sakuraProgress)
        progressLabel = findViewById(R.id.tvProgress)
        petalNumLabel = findViewById(R.id.tvPetalNum)
        progressSeekBar = findViewById(R.id.progressSeekBar)
        petalNumSeekBar = findViewById(R.id.petalNumSeekBar)
        demoBtn = findViewById(R.id.demo_btn)
    }

    private fun bindViews() {
        updateLabels()

        petalNumSeekBar.progress = sakuraProgress.petalNum

        progressSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                sakuraProgress.progress = progress
                updateLabels()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        petalNumSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) return

                sakuraProgress.petalNum = progress
                updateLabels()
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

    private fun updateLabels() {
        progressLabel.text = getString(R.string.label_progress, sakuraProgress.progress)
        petalNumLabel.text = getString(R.string.label_petal_num, sakuraProgress.petalNum)
    }

    override fun updateProgress(progress: Int) {
        progressSeekBar.progress = progress
    }
}
