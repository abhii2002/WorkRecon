package com.blissvine.workrecon.Firebase

import android.app.Activity
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.widget.Toast
import com.blissvine.workrecon.R
import com.blissvine.workrecon.activities.*
import com.blissvine.workrecon.adapters.CardListItemsAdapter
import com.blissvine.workrecon.models.Card
import com.blissvine.workrecon.models.User
import com.blissvine.workrecon.utils.Board
import com.blissvine.workrecon.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.math.acos

class FirestoreClass {
    // Create a instance of Firebase Firestore
    private val mFirestore = FirebaseFirestore.getInstance()
    /**
     * A function to make an entry of the registered user in the firestore database.
     */
    fun registerUser(activity: SignUpActivity, userInfo: User){
                 mFirestore.collection(Constants.USERS)
                     .document(getCurrentUserId())
                     .set(userInfo, SetOptions.merge())
                     .addOnSuccessListener {
                         // Here call a function of base activity for transferring the result to it.
                         activity.userRegisteredSuccess()
                     }

                     .addOnFailureListener{ e ->
                             Log.e(
                                 activity.javaClass.simpleName,
                                  "Error writing document",
                                  e
                             )
                     }
    }

    fun getBoardDetails(activity: TasksListActivity, documentId: String){
        mFirestore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
                activity.boardDetails(board)

            }.addOnFailureListener { e ->

                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }



fun createBoard(activity: CreateBoardActivity, board: Board){
    mFirestore.collection(Constants.BOARDS)
        .document()
        .set(board, SetOptions.merge())
        .addOnSuccessListener {
              Log.e(activity.javaClass.simpleName, "Board created successfully")
              Toast.makeText(activity, "Board created successfully", Toast.LENGTH_SHORT).show()
              activity.boardCreatedSuccessfully()
        }.addOnFailureListener {
             exception ->
               activity.hideProgressDialog()
               Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating board.",
                     exception
               )
        }


}

    fun getBoardList(activity: MainActivity){
          mFirestore.collection(Constants.BOARDS)
              .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
              .get()
              .addOnSuccessListener {
                  document ->
                    Log.i(activity.javaClass.simpleName, document.documents.toString())
                    val boardList: ArrayList<Board> = ArrayList()
                    for(i in document.documents){
                           val board = i.toObject(Board::class.java)!!
                           board.documentId = i.id
                           boardList.add(board)
                    }

                  activity.populateBoardsListToUI(boardList)
              }.addOnFailureListener { e ->

                  activity.hideProgressDialog()
                   Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
              }
    }

    fun addUpdateTaskList(activity: Activity, board: Board){
         val taskListHashMap = HashMap<String, Any>()
          taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFirestore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                  Log.e(activity.javaClass.simpleName, "TaskList added successfully.")
                if(activity is TasksListActivity) {
                    activity.addUpdateTaskListSuccess()
                }else if(activity is CardDetailsActivity){
                     activity.addUpdateTaskListSuccess()
                }
            }.addOnFailureListener {
                 exception ->
                if(activity is TasksListActivity) {
                    activity.hideProgressDialog()
                }
                else if(activity is CardDetailsActivity){
                     activity.hideProgressDialog()
                }
                  Log.e(activity.javaClass.simpleName, "Error while creating a board", exception)
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                 Log.i(activity.javaClass.simpleName, "Profile Data updated successfully!")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_LONG).show()
                when(activity) {
                    is MainActivity -> {
                           activity.tokenUpdateSuccess()
                        }
                    is MyProfileActivity -> {
                        activity.profileUpdateSuccess()
                       }
                    }
            }.addOnFailureListener{
                 e ->
                when(activity) {
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                   Log.e(
                        activity.javaClass.simpleName,
                         "Error while creating boards",
                          e
                   )
                Toast.makeText(activity, "Error when updating the profile!", Toast.LENGTH_SHORT).show()
            }
    }

    fun loadUserData(activity: Activity, readBoardsList: Boolean = false){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e(
                    activity.javaClass.simpleName, document.toString()
                )

                // TODO (STEP 3: Pass the result to base activity.)
                // START
                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)

                when(activity){
                     is SignInActivity ->{
                         // Here call a function of base activity for transferring the result to it.
                         if(loggedInUser != null) {
                             activity.signInSuccess(loggedInUser)
                             // END
                         }
                     }
                    is MainActivity -> {
                        if(loggedInUser != null) {
                            activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                        }
                    }
                    is MyProfileActivity ->{
                         if(loggedInUser != null) {
                             activity.setUserDataInUI(loggedInUser)
                         }
                    }
                 }
              }
            .addOnFailureListener{ e ->
                when(activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                     }
                    is MainActivity ->{
                           activity.hideProgressDialog()
                    }

                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }


    }

    /**
     * A function for getting the user id of current logged user.
     */
    fun getCurrentUserId():  String{
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getAssignedMembersListDetails(
         activity: Activity, assignedTo: ArrayList<String>){
         mFirestore.collection(Constants.USERS)
             .whereIn(Constants.ID, assignedTo)
             .get()
             .addOnSuccessListener {
                   document ->
                     Log.e(activity.javaClass.simpleName, document.documents.toString())
                   val usersList: ArrayList<User> = ArrayList()

                 for(i in document.documents){
                      val user = i.toObject(User::class.java)!!
                     usersList.add(user)
                 }
                 if(activity is MembersActivity)
                 activity.setupMembersList(usersList)
                 else if(activity is TasksListActivity)
                     activity.boardMemberDetailsList(usersList)
             }.addOnFailureListener { e->
                 if(activity is MembersActivity)
                   activity.hideProgressDialog()
                 else if(activity is TasksListActivity)
                     activity.hideProgressDialog()
                   Log.e(
                        activity.javaClass.simpleName,
                       "Error while creating a board.",
                       e
                   )
             }
    }

    fun getMemberDetails(activity: MembersActivity, email: String){
                mFirestore.collection(Constants.USERS)
                    .whereEqualTo(Constants.EMAIL, email)
                    .get()
                    .addOnSuccessListener {
                         document->
                        if(document.documents.size > 0){
                             val user = document.documents[0].toObject(User::class.java)!!
                             activity.memberDetails(user)
                        }else{
                             activity.hideProgressDialog()
                             activity.showErrorSnackBar("No such member found")
                        }
                    }.addOnFailureListener { e ->
                         activity.hideProgressDialog()
                         Log.e(
                              activity.javaClass.simpleName,
                              "Error while getting user details",
                               e
                         )
                    }
            }

    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User){
            val assignedToHashMap = HashMap<String, Any>()
            assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo

        mFirestore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                  activity.memberAssignSuccess(user)
            }.addOnFailureListener { e ->
                  activity.hideProgressDialog()
                  Log.e(activity.javaClass.simpleName,
                   "Error while creating a board.", e)
            }
    }

}