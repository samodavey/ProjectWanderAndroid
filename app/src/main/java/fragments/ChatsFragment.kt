package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.wander.R
import com.example.wander.WanderCallback
import com.google.firebase.database.DatabaseReference

/**
 * A simple [Fragment] subclass.
 */
class ChatsFragment : Fragment() {

    //Variables for creating a separate chats database
    private lateinit var userId: String
    private lateinit var userDatabase: DatabaseReference
    private lateinit var chatDatabase: DatabaseReference

    private var callback: WanderCallback? = null

    fun setCallback(callback: WanderCallback){
        this.callback = callback
        userId = callback.onGetUserId()
        userDatabase = callback.getUserDatabase()
        chatDatabase = callback.getChatDatabase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_matches, container, false)
    }

}
