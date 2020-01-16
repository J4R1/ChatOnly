import java.io.InputStream
import java.io.OutputStream
import java.io.PrintStream
import java.net.Socket
import java.util.*
//CommandInterpreter GUIDES THE INPUT(S) AND OUTPUT(S) OF USER(S)
class CommandInterpreter(inputStream: InputStream, output: OutputStream, client: Socket) : ChatHistoryObserver, Runnable {
    //Socket is used to create connection, uses Java library
    private val client: Socket = client
    //INPUT/OUTPUT
    val scanner: Scanner = Scanner(inputStream)     //scanner: Scanner, takes in user inputs of the socket
    val printOut: PrintStream = PrintStream(output) //printOut: PrintStream, prints out to users by socket
    //FOR COLORING----------------------------
    val esc = "\u001B"              //Needed code to change text
    val bold   = esc + "[1"  //For turning displayed text to bold
    val normal = esc + "[0"  //For turning displayed text back to normal(not in use)
    val colours = listOf(//Reference color code and name(not in use)
        ";31m" to "red",
        ";32m" to "green",
        ";33m" to "yellow",
        ";34m" to "blue",
        ";35m" to "magenta",
        ";36m" to "cyan",
        ";37m" to "white"
    )
    val yellow = listOf(";33m" to "")//Used as command interaction
    val blue   = listOf(";34m" to "")//Used in ChatMessage(NOT NECESSARY HERE)
    //END OF COLORING-------------------------
    var username: String = " " //Stores username as String
    var exit = false           //Used for continuing while loop
    //OVERRIDES newMessage FROM ChatHistoryObserver
    override fun newMessage(message: ChatMessage) {
        if (message.username != username) {//If given username is not empty: [var username: String = " "]
            printOut.println("${message.username} : ${message.message} @${message.timeHours}:" +
                    "${message.timeMinutes}:${message.timeSeconds}") //Gives message structure: Name-Message-Time
        }
    }
    //FUNCTION RUN, STARTS THE CHAT PART AS A THREAD TO USER, AFTER CONNECTION IS ACCEPTED IN ChatServer()-class
    override fun run() {
        ChatHistory.registerObserver(this)
        for (attr in listOf( bold)) {                //Commands and other info is displayed in bold & YELLOW
            for (color in yellow)
                print("$attr${color.first}${color.second}") //Empty print to select color for following message
        }
        printOut.println("Welcome to BabbleGabble™") //Welcomes user and prints out the application name
        //printOut.println("=============================")                //Alternative title
        var command = " "
        //REGISTERING OF USERNAME
        do {
            while (username == " ") {                      //Username is asked until acceptable name is given
                printOut.println("Type :user [username] to set your username ")
                val command = scanner.nextLine()    //Takes in username
                if (command.split(' ')[0] == ":user") {//Forces the user to give username HERE, after command :user, otherwise user is asked again
                    val separateString = command.split(' ') //Splits command is there is space ' '
                    if (separateString.size > 1) {                               //If user gives input after :user, there is more than 1
                        val inputCommand = command.substringAfter(" ")//inputcommand is the string after the space(' ') after :user
                        var rangepoisto = inputCommand           //Stores inputCommad as 'var', so it can be modified later
                        val stringpoisto = Regex("[^0-9A-Za-z]")//^Removes all but the markings inside []
                        rangepoisto = stringpoisto.replace(rangepoisto, "_") //Replaces all illegal chars as '_'
                        if (rangepoisto.length >=12){   //Username is limited to 12(0..11), checks if input is 13 or more characters
                            var newAnnetturiviOver = rangepoisto.substring(0..11)//Removes characters not in range 0 to 11 (12-characters)
                            printOut.println("Username can only be 12 characters long: ")    //Gives user a warning, but doesn't ask for a new name if it's not taken
                            if (Users.addUsername(newAnnetturiviOver)) {//Checks if given username already taken, if not, name is accepted
                                username = newAnnetturiviOver           //name is the given input, limited to 12-characters
                                printOut.println("Your username is: $username") //Informs user their username
                                printOut.println("Now you can send messages or type :help for help") //Informs user can now send messages of use commands, such as :help
                            } else {    //if given username is taken, it's asked again
                                printOut.println("This username is already taken!")
                            }
                        }else if (rangepoisto.length<12 && rangepoisto.length>=1){ //Checks if given username is not empty and under 12 characters
                            var newAnnetturiviUnder = rangepoisto           //Username length is accepted as is
                            if (Users.addUsername(newAnnetturiviUnder)) {         //Checks if given username already taken, if not, name is accepted
                                username = newAnnetturiviUnder                    //Username is accepted as is
                                printOut.println("Your username is: $username")   //Informs user their username
                                printOut.println("Now you can send messages or use :help for help") //Informs user can now send messages of use commands, such as :help
                            }else{     //if given username is taken, it's asked again
                                printOut.println("This username is already taken!")
                            }
                        }
                    }else{
                        printOut.println("Oops, something went wrong") //In case of unexpected error
                    }
                }
            }
            //END OF USERNAME REGISTERING
            command = scanner.nextLine()//Any user input, message or command
            when (command.split(' ')[0]) { //When first word after space ' ', is a command
                //Commands:
                ":users" -> printUserList(Users.getUserList()) //Prints active users
                ":help" -> HelpList(printOut).show() //Prints possible commands and their action
                ":exit" -> shutdown()   //Closes user connection
                ":quit" -> shutdown()   //Alternative typing to ':exit'
                ":history" -> printMessageHistory() //Prints all previous user messages
                else -> {  //If user input is not a command
                    if (command.length>=100){ //Limits users message length to 100 characters
                        command=command.substring(0..99) //Cuts the string to 100-characters
                    }
                    for (attr in listOf( bold)) {//Turns text to bold
                        for (color in blue)
                            print("$attr${color.first}${color.second}")//Empty print that colors following messages to BLUE
                    }
                    ChatHistory.insert(ChatMessage(username, command))//Stores user, message and time to history
                    for (attr in listOf( bold)) {//Turns text to bold
                        for (color in yellow)
                            print("$attr${color.first}${color.second}")//Empty print that colors following messages to YELLOW, only visible as notification from commands as there is no message
                    }
                    ChatHistory.notifyObservers(ChatMessage(username, command))//Sends user and message, ready for sending
                }
            }
            when (command.split(' ')[0]){//when first word after ' ' is empty(Enter)
                "" -> printMessage() //Prints message
            }
            if(command.split(' ')[0]!=":"){//when first word is not a command
                printMessage() //Prints message
            }
        } while (!exit)//Users while loop doesn't end before shutdown() command
    }
    //FUNCTIONS FOR COMMANDS
    private fun printMessage() {//Prints latest message
        for (message: ChatMessage in ChatHistory.returnMessageList()) {
        }
    }
    private fun printMessageHistory() {//Prints each message in message history
        for (message: ChatMessage in ChatHistory.returnMessageList()) {
            printOut.println(message.getMessageInOneLine())
        }
    }
    private fun printUserList(users: List<String>) {//Prints user(s) in list of active users
        users.forEach{ printOut.println(it) }
    }
    private fun shutdown() {//Disconnects user from socket
        println("$username Has exited the chat")//Notifies all that user has left the chat !notice: printline, not printOut
        ChatHistory.notifyObservers(ChatMessage(username, "Goodbye!"))//Automated goodbye message assigned to exiting user
        client.close()//Closes users connection to chat server
        Users.removeUsername(username) //Removes user from list of active users
        exit = true //Ends users loop
    }
}