//ChatHistory
object ChatHistory : ChatHistoryObservable {
    val observers: MutableList<ChatHistoryObserver> = mutableListOf()
    override fun registerObserver(observer: ChatHistoryObserver) {
        observers.add(observer)//Adds new user to observer list
    }
    override fun deregisterObserver(observer: ChatHistoryObserver) {
        observers.remove(observer)//Removes the user from observer list
    }
    override fun notifyObservers(message: ChatMessage) {
        observers.forEach { it.newMessage(message) }//Sends message for user(s)
    }
    private val messageHistory: MutableList<ChatMessage> = mutableListOf()
    fun insert(message: ChatMessage) {
        messageHistory.add(message)
        println("$message")//not printOut.println(), prints message to console
    }
    fun returnMessageList(): List<ChatMessage> {
        return messageHistory.toList()//Records chat history of the server without limit
    }
}