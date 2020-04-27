package fragments

import adapters.CardsAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast

import com.example.wander.R
import com.example.wander.WanderCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.fragment_swipe.*
import util.*

/**
 * A simple [Fragment] subclass.
 */
class SwipeFragment : Fragment() {

    private var callback: WanderCallback? = null
    private lateinit var userId : String
    private lateinit var userDatabase : DatabaseReference
    //Puts the items in the array into a view
    private var cardsAdapter : ArrayAdapter<User>? = null
    //Array of items
    private var rowItems = ArrayList<User>()
    //Variable to reference against when filtering by gender type
    private var preferredGender : String? = null


    fun setCallback(callback: WanderCallback){
        this.callback = callback
        userId = callback.onGetUserId()
        userDatabase = callback.getUserDatabase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_swipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        userDatabase.child(userId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                preferredGender = user?.preferredGender
                populateItems()
            }
        })

        cardsAdapter = CardsAdapter(context, R.layout.item, rowItems)

        frame.adapter = cardsAdapter
        frame.setFlingListener(object : SwipeFlingAdapterView.onFlingListener{
            override fun removeFirstObjectInAdapter() {
                //This gets rid of the first card
                rowItems.removeAt(0)
                cardsAdapter?.notifyDataSetChanged()
            }

            override fun onLeftCardExit(p0: Any?) {
                var user = p0 as User
                //Update database
                userDatabase.child(user.uid.toString()).child(DATA_SWIPES_LEFT).child(userId).setValue(true)

            }

            override fun onRightCardExit(p0: Any?) {
                var selectedUser = p0 as User
                var selectedUserId = selectedUser.uid
                if(!selectedUserId.isNullOrEmpty()){
                    userDatabase.child(userId).child(DATA_SWIPES_RIGHT).addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if(p0.hasChild(selectedUserId)){
                                Toast.makeText(context,"Match!", Toast.LENGTH_SHORT).show()

                                userDatabase.child(userId).child(DATA_SWIPES_RIGHT).child(selectedUserId).removeValue()
                                userDatabase.child(userId).child(DATA_MATCHES).child(selectedUserId).setValue(true)
                                userDatabase.child(selectedUserId).child(DATA_MATCHES).child(userId).setValue(true)
                            }else{
                                userDatabase.child(selectedUserId).child(DATA_SWIPES_RIGHT).child(userId).setValue(true)
                            }
                        }

                    })
                }
            }

            override fun onAdapterAboutToEmpty(p0: Int) {

            }

            override fun onScroll(p0: Float) {

            }
        })


        //If the like button is clicked
        likeButton.setOnClickListener{
            if(!rowItems.isEmpty()){
                frame.topCardListener.selectRight()
            }
        }

        //if the dislike button is clicked
        dislikeButton.setOnClickListener{
            if(!rowItems.isEmpty()){
                frame.topCardListener.selectLeft()
            }
        }

    }

    fun populateItems(){
        noUsers.visibility = View.GONE
        progressLayout.visibility = View.VISIBLE

        //This variable gets all the data required for gender preference and orders it accordingly
        val cardsQuery = userDatabase.orderByChild(DATA_GENDER).equalTo(preferredGender)
        cardsQuery.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                //If we have already seen this user we don't need to show it
                p0.children.forEach{child ->
                    val user = child.getValue(User::class.java)
                    if(user != null){
                        var showUser = true
                        //If this user already has me in their swipe left, don't display
                        if(child.child(DATA_SWIPES_LEFT).hasChild(userId) || child.child(
                                DATA_SWIPES_RIGHT).hasChild(userId) || child.child(DATA_MATCHES).hasChild(userId) ){
                            showUser = false
                        }
                        if(showUser){
                            //Let the adapter know we have updated it
                            rowItems.add(user)
                            cardsAdapter?.notifyDataSetChanged()
                        }
                    }
                }
                progressLayout.visibility = View.GONE
                //If the row items is empty display the "No users available layout"
                if(rowItems.isEmpty()){
                    noUsers.visibility = View.VISIBLE
                }
            }
        })
    }
}
