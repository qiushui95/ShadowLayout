package son.ysy.custom.shadow.drawable

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat

abstract class DrawableWrapper internal constructor(
    originDrawable: Drawable?
) : Drawable(), Drawable.Callback {

    protected lateinit var wrapperDrawable: Drawable

    init {
        setWrappedDrawable(originDrawable)
    }

    override fun draw(canvas: Canvas) {
        wrapperDrawable.draw(canvas)
    }

    override fun setAlpha(alpha: Int) {
        wrapperDrawable.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        wrapperDrawable.colorFilter = colorFilter
    }

    @Suppress("DEPRECATION")
    override fun getOpacity(): Int = wrapperDrawable.opacity

    override fun invalidateDrawable(who: Drawable) {
        invalidateSelf()
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        scheduleSelf(what, `when`)
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        unscheduleSelf(what)
    }

    override fun onBoundsChange(bounds: Rect) {
        wrapperDrawable.bounds = bounds
    }

    override fun setChangingConfigurations(configs: Int) {
        wrapperDrawable.changingConfigurations = configs
    }

    override fun getChangingConfigurations(): Int {
        return wrapperDrawable.changingConfigurations
    }

    @Suppress("DEPRECATION")
    override fun setDither(dither: Boolean) {
        wrapperDrawable.setDither(dither)
    }

    override fun setFilterBitmap(filter: Boolean) {
        wrapperDrawable.isFilterBitmap = filter
    }

    @Suppress("DEPRECATION")
    override fun setColorFilter(color: Int, mode: PorterDuff.Mode) {
        wrapperDrawable.setColorFilter(color, mode)
    }

    override fun isStateful(): Boolean {
        return wrapperDrawable.isStateful
    }

    override fun setState(stateSet: IntArray): Boolean {
        return wrapperDrawable.setState(stateSet)
    }

    override fun getState(): IntArray {
        return wrapperDrawable.state
    }

    override fun jumpToCurrentState() {
        wrapperDrawable.jumpToCurrentState()
    }

    override fun getCurrent(): Drawable {
        return wrapperDrawable.current
    }

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        return super.setVisible(visible, restart) || wrapperDrawable.setVisible(visible, restart)
    }

    override fun getTransparentRegion(): Region? {
        return wrapperDrawable.transparentRegion
    }

    override fun getIntrinsicWidth(): Int {
        return wrapperDrawable.intrinsicWidth
    }

    override fun getIntrinsicHeight(): Int {
        return wrapperDrawable.intrinsicHeight
    }

    override fun getMinimumWidth(): Int {
        return wrapperDrawable.minimumWidth
    }

    override fun getMinimumHeight(): Int {
        return wrapperDrawable.minimumHeight
    }

    override fun getPadding(padding: Rect): Boolean {
        return wrapperDrawable.getPadding(padding)
    }

    override fun onLevelChange(level: Int): Boolean {
        return wrapperDrawable.setLevel(level)
    }

    override fun setAutoMirrored(mirrored: Boolean) {
        DrawableCompat.setAutoMirrored(wrapperDrawable, mirrored)
    }

    override fun isAutoMirrored(): Boolean {
        return DrawableCompat.isAutoMirrored(wrapperDrawable)
    }

    override fun setTint(tintColor: Int) {
        DrawableCompat.setTint(wrapperDrawable, tintColor)
    }

    override fun setTintList(tint: ColorStateList?) {
        DrawableCompat.setTintList(wrapperDrawable, tint)
    }

    override fun setTintMode(tintMode: PorterDuff.Mode?) {
        tintMode?.apply {
            DrawableCompat.setTintMode(wrapperDrawable, this)
        }
    }

    override fun setHotspot(x: Float, y: Float) {
        DrawableCompat.setHotspot(wrapperDrawable, x, y)
    }

    override fun setHotspotBounds(left: Int, top: Int, right: Int, bottom: Int) {
        DrawableCompat.setHotspotBounds(wrapperDrawable, left, top, right, bottom)
    }

    fun setWrappedDrawable(originDrawable: Drawable?) {
        if (this::wrapperDrawable.isInitialized) {
            this.wrapperDrawable.callback = null
        }
        this.wrapperDrawable = originDrawable ?: ColorDrawable(Color.TRANSPARENT)
        this.wrapperDrawable.callback = this
    }
}