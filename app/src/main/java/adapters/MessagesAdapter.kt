package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wander.R
import kotlinx.android.synthetic.main.item_current_user_message.view.*
import util.Message

class MessagesAdapter(private var messages: ArrayList<Message>, val userId : String) :
    RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    companion object{
        val MESSAGE_CURRENT_USER = 1
        val MESSAGE_OTHER_USER = 2
    }

    //Updates the adapter
    fun addMessage(message : Message){
        messages.add(message)
        notifyDataSetChanged()
    }

    //The TextView is populated with whatever the value of message is
    class MessageViewHolder(private val view : View) : RecyclerView.ViewHolder(view){
        fun bind(message: Message){
            view.findViewById<TextView>(R.id.messageTV).text = message.message
        }
    }

    //Gets the view type from 'getItemViewType' to determine which user is which and returns them accordingly
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        if(viewType == MESSAGE_CURRENT_USER){
            return MessageViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_current_user_message, parent, false)
            )
        }else{
            return MessageViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_other_user_message, parent, false)
            )
        }
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    //This determines which message is coming from which user
    override fun getItemViewType(position: Int): Int {
        return if(messages[position].sentBy.equals(userId)){
            MESSAGE_CURRENT_USER
        }else{
            MESSAGE_OTHER_USER
        }
    }

}