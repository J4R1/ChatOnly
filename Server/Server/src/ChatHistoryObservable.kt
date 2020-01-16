interface ChatHistoryObservable {//INTERFACE FOR USE
    fun registerObserver(observer: ChatHistoryObserver)
    fun deregisterObserver(observer: ChatHistoryObserver)
    fun notifyObservers (message: ChatMessage)
}