package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.wander.R
import com.example.wander.WanderCallback

/**
 * A simple [Fragment] subclass.
 */
class MatchesFragment : Fragment() {

    private var callback: WanderCallback? = null

    fun setCallback(callback: WanderCallback){
        this.callback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_matches, container, false)
    }

}
