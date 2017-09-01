package tt.kao.sakuraprogress

import android.os.AsyncTask
import android.os.SystemClock
import java.util.*

/**
 * @author luke_kao
 */
class DemoTask(val callback: Callback) : AsyncTask<Unit, Int, Unit>() {
    interface Callback {
        fun updateProgress(progress: Int)
    }

    override fun doInBackground(vararg param: Unit?) {
        val STOP_INDEX = Random().nextInt(100)

        for (i in 0..100) {
            publishProgress(i)

            if (i == STOP_INDEX) {
                SystemClock.sleep(3000)
            } else {
                SystemClock.sleep(30)
            }
        }
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)

        callback.updateProgress(values[0]!!)
    }
}