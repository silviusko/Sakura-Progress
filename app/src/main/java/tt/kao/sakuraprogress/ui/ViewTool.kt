package tt.kao.sakuraprogress.ui

import android.content.Context
import android.view.View

/**
 * @author luke_kao
 */
class ViewTool private constructor() {
    companion object {
        fun dp2Px(context: Context?, dp: Float): Float {
            return if (context != null) {
                context.resources.displayMetrics.density * dp
            } else {
                dp
            }
        }

        fun measure(defaultSize: Int, measureSpec: Int): Int {
            var result = defaultSize

            val mode = View.MeasureSpec.getMode(measureSpec)
            val size = View.MeasureSpec.getSize(measureSpec)

            when (mode) {
                View.MeasureSpec.EXACTLY -> {
                    result = size
                }
                View.MeasureSpec.AT_MOST -> {
                    result = Math.min(defaultSize, size)
                }
                View.MeasureSpec.UNSPECIFIED -> {
                    result = defaultSize
                }
            }

            return result
        }
    }
}