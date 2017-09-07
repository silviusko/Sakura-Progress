package tt.kao.sakuraprogress.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.annotation.DrawableRes
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

        fun decodeResourceWithTarget(resource: Resources, @DrawableRes resId: Int, view: View): Bitmap {
            return decodeResourceWithTarget(resource, resId, view.width, view.height)
        }

        fun decodeResourceWithTarget(resource: Resources, @DrawableRes resId: Int, targetWidth: Int, targetHeight: Int): Bitmap {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true

            BitmapFactory.decodeResource(resource, resId, options)

            val wSampleSize = Math.pow(2.0, Math.floor(Math.log(options.outWidth.toDouble() / targetWidth)))
            val hSampleSize = Math.pow(2.0, Math.floor(Math.log(options.outHeight.toDouble() / targetHeight)))

            options.inSampleSize = Math.min(wSampleSize, hSampleSize).toInt()
            options.inJustDecodeBounds = false

            return BitmapFactory.decodeResource(resource, resId, options)
        }
    }
}