package com.bernaferrari.emojisliderSample

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView

fun TextView.doAfterChanged(
    afterChanged: (Editable) -> Unit
): TextWatcher = addTextChangedListener(afterChanged = afterChanged)

fun TextView.doBeforeChanged(
    beforeChanged: (s: CharSequence, start: Int, count: Int, after: Int) -> Unit
): TextWatcher = addTextChangedListener(beforeChanged = beforeChanged)

fun TextView.doOnChanged(
    onChanged: (s: CharSequence, start: Int, before: Int, count: Int) -> Unit
): TextWatcher = addTextChangedListener(onChanged = onChanged)

fun TextView.addTextChangedListener(
    beforeChanged: ((s: CharSequence, start: Int, count: Int, after: Int) -> Unit)? = null,
    afterChanged: ((Editable) -> Unit)? = null,
    onChanged: ((s: CharSequence, start: Int, before: Int, count: Int) -> Unit)? = null
): TextWatcher {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            afterChanged?.invoke(s)
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            beforeChanged?.invoke(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            onChanged?.invoke(s, start, before, count)
        }
    }
    addTextChangedListener(watcher)
    return watcher
}