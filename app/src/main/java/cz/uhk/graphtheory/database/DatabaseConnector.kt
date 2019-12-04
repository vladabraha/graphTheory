package cz.uhk.graphtheory.database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import cz.uhk.graphtheory.model.User


class DatabaseConnector() {
    private val database = FirebaseDatabase.getInstance()
    private var updateData: ValuesUpdate? = null
    private var users = arrayListOf<User>()

    init {
        val ref = database.getReference("users")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                users.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val user = postSnapshot.getValue(User::class.java)
                    if (user != null) {
                        users.add(user)
                    }
                }
                updateData?.usersUpdated(users)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
            }
        }
        ref.addValueEventListener(postListener)
    }

    constructor(valuesUpdate: ValuesUpdate) : this() {
        updateData = valuesUpdate
    }

    fun recordUserPoints(userName: String, activity: String): Double? {
        val user = findUser(userName)
        if (user != null) {

            val map = user.remainingPointsFromActivity

            val point: Double
            if (checkIfKeyExist(map, activity)) {
                point = map.getValue(activity)
            } else {
                point = 3.0
                map[activity] = 3.0
            }

            var score = user.score
            score += point

            database.getReference("users").child(user.uuID).child("score").setValue(score)

            if (point > 0) {
                map[activity] = (point - 1)
                database.getReference("users").child(user.uuID).child("remainingPointsFromActivity").setValue(map)
            }
            setUnlockTopics(userName)
            return point
        }
        return null
    }

    private fun setUnlockTopics(userName: String) {
        val user = findUser(userName)
        if (user != null) {
            val points = user.remainingPointsFromActivity
            var activities: MutableList<String> = ArrayList()
            for ((key, value) in points) {
                var found = false
                var name:String
                if (key.indexOf("-") != -1){
                    name = key.substring(0, key.indexOf("-"))
                }else{
                    name = key
                }
                for (activity in activities) {
                    if (activity == name) {
                        found = true
                    }
                }
                if (!found) {
                    activities.add(name)
                }
            }
            val numberOfUnlockedTopics = activities.size
            database.getReference("users").child(user.uuID).child("unlockTopics").setValue(numberOfUnlockedTopics)
        }
    }

    private fun checkIfKeyExist(map: HashMap<String, Double>, activity: String): Boolean {
        for (key in map.keys) {
            if (key == activity) return true
        }
        return false
    }

    fun findUser(email: String): User? {
        for (user in users) {
            if (user.email == email) return user
        }
        return null
    }


    fun createUserAccount(uuID: String, nickName: String, email: String, team: String) {
        database.getReference("users").child(uuID).child("email").setValue(email)
        database.getReference("users").child(uuID).child("nickName").setValue(nickName)
        database.getReference("users").child(uuID).child("team").setValue(team)
        database.getReference("users").child(uuID).child("score").setValue(0)
        database.getReference("users").child(uuID).child("unlockTopics").setValue(0)
        database.getReference("users").child(uuID).child("uuID").setValue(uuID)
        val points = HashMap<String, Double>()
        points["first"] = 0.01
        database.getReference("users").child(uuID).child("remainingPointsFromActivity").setValue(points)

    }

    fun emailAvailable(nickName: String): Boolean {
        for (user in users) {
            if (user.nickName == nickName) return false
        }
        return true
    }

    fun getUsers(): ArrayList<User> {
        return users
    }

    public interface ValuesUpdate {
        fun usersUpdated(users: ArrayList<User>)
    }

}
