package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isGone

import com.example.wander.R
import com.example.wander.WanderCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.*
import util.*


class ProfileFragment : Fragment() {



    private lateinit var userId: String
    private lateinit var userDatabase: DatabaseReference
    private var callback: WanderCallback? = null

    fun setCallback(callback: WanderCallback){
        this.callback = callback
        userId = callback.onGetUserId()
        userDatabase = callback.getUserDatabase().child(userId)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressLayout.setOnTouchListener { view, event -> true }

        populateInfo()

        photoIV.setOnClickListener{callback?.startPhotoActivity()}

        applyButton.setOnClickListener{onApply()}
        signOutButton.setOnClickListener {callback?.onSignout()}
    }

    fun populateInfo() {
        progressLayout.visibility = View.VISIBLE
        userDatabase.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                progressLayout.visibility = View.GONE
            }

            override fun onDataChange(p0 : DataSnapshot) {
                if(isAdded){
                    val user = p0.getValue(User::class.java)
                    nameET.setText(user?.name, TextView.BufferType.EDITABLE)
                    emailET.setText(user?.email, TextView.BufferType.EDITABLE)
                    ageET.setText(user?.age, TextView.BufferType.EDITABLE)
                    if(user?.gender == GENDER_MALE){
                        radioMan1.isChecked = true
                    }
                    if(user?.gender == GENDER_FEMALE){
                        radioWoman1.isChecked = true
                    }

                    if(user?.preferredGender == GENDER_MALE){
                        radioMan2.isChecked = true
                    }
                    if(user?.preferredGender == GENDER_FEMALE){
                        radioWoman2.isChecked = true
                    }
                    progressLayout.visibility = View.GONE
                }
            }

        })
    }

    fun onApply() {
        //Need a integer check for age
        //NEED A PAGE TO EDIT AND PAGE TO DISPLAY
        if(nameET.text.toString().isNullOrEmpty() || emailET.text.toString().isNullOrEmpty() ||
            radioGroup1.checkedRadioButtonId == -1 || radioGroup2.checkedRadioButtonId == -1){
            Toast.makeText(context, getString(R.string.error_profile_incomplete),Toast.LENGTH_SHORT).show()
        }else{
            val name = nameET.text.toString()
            val email = emailET.text.toString()
            val age = ageET.text.toString()
            val gender =
                if(radioMan1.isChecked) GENDER_MALE
                else GENDER_FEMALE

            val preferredGender =
                if(radioMan2.isChecked) GENDER_MALE
                else GENDER_FEMALE

            userDatabase.child(DATA_NAME).setValue(name)
            userDatabase.child(DATA_EMAIL).setValue(email)
            userDatabase.child(DATA_AGE).setValue(age)
            userDatabase.child(DATA_GENDER).setValue(gender)
            userDatabase.child(DATA_GENDER_PREFERENCE).setValue(preferredGender)

            callback?.profileComplete()
        }
    }

    fun updateImageUri(uri:String){
        userDatabase.child(DATA_IMAGE_URL).setValue(uri)
    }


}
