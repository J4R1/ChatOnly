import java.time.LocalDateTime
import java.time.LocalTime
//ChatMessage COMBINES PARTS OF THE MESSAGE AND MANAGES OUTPUT
class ChatMessage (val username: String, val message: String) {
    //Different ways to display time of the message
    private val date = LocalDateTime.now() //@YEAR-MM-DD T HH:MM:SS
    private val time = LocalTime.now() //@HH:MM:SS.000  Displays fractions of a second also
     val timeHours = time.hour          //Displays hours
     val timeMinutes = time.minute      //Displays minutes
     val timeSeconds = time.second      //Displays seconds
    private val year = date.year        //Displays Year(Not in use)
    private val month = date.month  //Displays month as text 'JANUARY'(Not in use)
    val esc = "\u001B"//Needed code to change text
    val bold   = esc + "[1"//For having displayed text to bold
    //Different colors assigned to different users for better distinction
    val blue   = listOf(";34m" to "")//User 1
    val green  = listOf(";32m" to "")//User 2
    val magenta= listOf(";35m" to "")//User 3
    val cyan   = listOf(";36m" to "")//User 4
    val red    = listOf(";31m" to "")//Reserved colors, not used
    val grey   = listOf(";37m" to "")//Reserved colors, not used
    //Assigns the user message  to unique color
    override fun toString(): String {
        val userlist=Users.getUserList() //Gets active users in index form for color assignment
        userlist.toList()
        //These 'if's check who the user is in the list index, and colors the message to distinguish each user
        if(userlist.size==2){//If 2 users, second user messages are in BLUE
            if(username in userlist[1]){
                for (attr in listOf( bold)) {
                    for (color in green)//For 2nd user
                    print("$attr${color.first}${color.second}")
                }
            }
        }else if(userlist.size==3){//If 3 users, second user messages are in BLUE, third MAGENTA
            if(username in userlist[2]){
                for (attr in listOf( bold)) {
                    for (color in magenta)//For 3rd user
                        print("$attr${color.first}${color.second}")
                }
            }
        }else if(userlist.size==4){//If 4 users
            if(username in userlist[3]){
                for (attr in listOf( bold)) {//If 4 users, second user messages are in BLUE, third MAGENTA, fourth CYAN
                    for (color in cyan)//For 4th user
                        print("$attr${color.first}${color.second}")
                }
            }
        }else{//All messages in BLUE, if 1 user or >=5 users
            for (attr in listOf( bold)) {
                for (color in blue)
                print("$attr${color.first}${color.second}")
            }
         }
        return username + ": " + message+ "  @" +  timeHours + ":" + timeMinutes +":"+ timeSeconds//The way messages are displayed to user(s)
    }
    //Returns the message form to history
    fun getMessageInOneLine(): String {
        return "${username} : ${message} @${timeHours}:${timeMinutes}:${timeSeconds}"//The way messages in history are displayed to user(s)
    }
}
