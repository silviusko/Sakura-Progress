package tt.kao.sakuraprogress

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import tt.kao.sakuraprogress.ui.SakuraProgress

class MainActivity : AppCompatActivity(), DemoTask.Callback, SeekBar.OnSeekBarChangeListener {

    private lateinit var sakuraProgress: SakuraProgress
    private lateinit var progressLabel: TextView
    private lateinit var petalNumLabel: TextView
    private lateinit var floatTimeLabel: TextView
    private lateinit var rotateTimeLabel: TextView
    private lateinit var progressSeekBar: SeekBar
    private lateinit var petalNumSeekBar: SeekBar
    private lateinit var floatTimeSeekBar: SeekBar
    private lateinit var rotateTimeSeekBar: SeekBar
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
        floatTimeLabel = findViewById(R.id.tvFloatTime)
        rotateTimeLabel = findViewById(R.id.tvRotateTime)
        progressSeekBar = findViewById(R.id.progressSeekBar)
        petalNumSeekBar = findViewById(R.id.petalNumSeekBar)
        floatTimeSeekBar = findViewById(R.id.floatTimeSeekBar)
        rotateTimeSeekBar = findViewById(R.id.rotateTimeSeekBar)

        demoBtn = findViewById(R.id.demo_btn)
    }

    private fun bindViews() {
        updateLabels()

        petalNumSeekBar.progress = sakuraProgress.petalNum
        floatTimeSeekBar.progress = sakuraProgress.petalFloatTime - 500
        rotateTimeSeekBar.progress = sakuraProgress.petalRotateTime - 500

        progressSeekBar.setOnSeekBarChangeListener(this)
        petalNumSeekBar.setOnSeekBarChangeListener(this)
        floatTimeSeekBar.setOnSeekBarChangeListener(this)
        rotateTimeSeekBar.setOnSeekBarChangeListener(this)

        demoBtn.setOnClickListener {
            val task = DemoTask(this)
            task.execute()
        }
    }

    private fun updateLabels() {
        progressLabel.text = getString(R.string.label_progress, sakuraProgress.progress)
        petalNumLabel.text = getString(R.string.label_petal_num, sakuraProgress.petalNum)
        floatTimeLabel.text = getString(R.string.label_float_time, sakuraProgress.petalFloatTime)
        rotateTimeLabel.text = getString(R.string.label_rotate_time, sakuraProgress.petalRotateTime)
    }

    override fun updateProgress(progress: Int) {
        progressSeekBar.progress = progress
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        seekBar ?: return

        when (seekBar) {
            progressSeekBar -> sakuraProgress.progress = progress
            petalNumSeekBar -> sakuraProgress.petalNum = progress
            floatTimeSeekBar -> sakuraProgress.petalFloatTime = progress + 500
            rotateTimeSeekBar -> sakuraProgress.petalRotateTime = progress + 500
            else -> return
        }

        updateLabels()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}
