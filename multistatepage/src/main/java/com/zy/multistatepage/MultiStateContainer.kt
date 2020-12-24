package com.zy.multistatepage

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.zy.multistatepage.state.SuccessState

/**
 * @ProjectName: MultiStatePage
 * @Author: 赵岩
 * @Email: 17635289240@163.com
 * @Description: TODO
 * @CreateDate: 2020/9/17 11:54
 */
@SuppressLint("ViewConstructor")
class MultiStateContainer(
    context: Context,
    private val originTargetView: View,
    private val onRetryEventListener: OnRetryEventListener
) : FrameLayout(context) {
    private var statePool: MutableMap<Class<out MultiState>, MultiState> = mutableMapOf()

    private var animator = ValueAnimator.ofFloat(0.0f, 1.0f).apply {
        duration = MultiStatePage.config.alphaDuration
    }
    private var enableOriginTargetViewAnimator = true
    fun initialization() {
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        addView(originTargetView, 0, layoutParams)
    }

    inline fun <reified T : MultiState> show(noinline notify: (T) -> Unit = {}) {
        show(T::class.java, notify)
    }

    fun <T : MultiState> show(clazz: Class<T>) {
        show(clazz, onNotifyListener = { })
    }

    fun <T : MultiState> show(
        clazz: Class<T>,
        onNotifyListener: OnNotifyListener<T> = OnNotifyListener { }
    ) {
        findState(clazz)?.let { multiState ->
            if (childCount == 0) {
                initialization()
            }
            if (childCount > 1) {
                removeViewAt(1)
            }
            //1 展示原view
            //2 不需要执行动画
            if (multiState is SuccessState) {
                originTargetView.visibility = View.VISIBLE
                if (enableOriginTargetViewAnimator) {
                    originTargetView.doAnimator()
                }
            } else {
                if (!multiState.dialogMode()) {
                    originTargetView.visibility = View.GONE
                    enableOriginTargetViewAnimator = true
                } else {
                    enableOriginTargetViewAnimator = false
                }
                val currentStateView =
                    multiState.onCreateMultiStateView(context, LayoutInflater.from(context), this)
                multiState.onMultiStateViewCreate(currentStateView)
                val retryView = multiState.bindRetryView()
                if (multiState.enableReload() && retryView != null) {
                    retryView.setOnClickListener { onRetryEventListener.onRetryEvent(this) }
                } else {
                    currentStateView.setOnClickListener { onRetryEventListener.onRetryEvent(this) }
                }
                addView(currentStateView)
                if (!multiState.dialogMode()) {
                    currentStateView.doAnimator()
                }
                onNotifyListener.onNotify(multiState as T)
            }
        }
    }

    private fun <T : MultiState> findState(clazz: Class<T>): MultiState? {
        return if (statePool.containsKey(clazz)) {
            statePool[clazz]
        } else {
            val state = clazz.newInstance()
            statePool[clazz] = state
            state
        }
    }

    private fun View.doAnimator() {
        this.clearAnimation()
        animator.addUpdateListener {
            this.alpha = it.animatedValue as Float
        }
        animator.start()
    }
}