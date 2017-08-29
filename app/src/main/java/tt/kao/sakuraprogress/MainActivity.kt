package tt.kao.sakuraprogress

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import tt.kao.sakuraprogress.ui.SakuraProgress

class MainActivity : AppCompatActivity() {
    private lateinit var sakuraProgress: SakuraProgress
    private lateinit var progressSeekBar: SeekBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViews()
    }

    private fun findViews() {
        sakuraProgress = findViewById(R.id.sakuraProgress)
        progressSeekBar = findViewById(R.id.progressSeekBar)

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
    }
}
