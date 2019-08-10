package cz.uhk.graphstheory.database

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class DatabaseConnector{
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("first")
    var result: String = ""

    fun writeFirstActivityValue(value : String){
        // Write a message to the database
        myRef.setValue(value)

    }

    fun getFirstActivityValue(): String {

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val data = dataSnapshot.getValue(String::class.java)
                result = data.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("tag", "Failed to read value.", error.toException())
            }
        })
        return result
    }

    fun createUserAccount(name : String, nickname: String, team : String){
        database.getReference("users").child(name).child("nickname").setValue(nickname)
        database.getReference("users").child(name).child("team").setValue(team)
        database.getReference("users").child(name).child("score").setValue(0)
        database.getReference("users").child(name).child("unlockTopics").setValue(0)
    }
}
