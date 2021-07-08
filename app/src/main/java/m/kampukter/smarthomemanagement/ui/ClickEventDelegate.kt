package m.kampukter.smarthomemanagement.ui

interface ClickEventDelegate<T> {
    fun onClick(item: T)
    fun onLongClick(item: T)
}