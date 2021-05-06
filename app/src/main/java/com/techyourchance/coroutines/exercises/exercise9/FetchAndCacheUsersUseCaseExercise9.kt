package com.techyourchance.coroutines.exercises.exercise9

import com.techyourchance.coroutines.exercises.exercise8.GetUserEndpoint
import com.techyourchance.coroutines.exercises.exercise8.User
import com.techyourchance.coroutines.exercises.exercise8.UsersDao
import kotlinx.coroutines.*

class FetchAndCacheUsersUseCaseExercise9(
        private val getUserEndpoint: GetUserEndpoint,
        private val usersDao: UsersDao
) {

    suspend fun fetchAndCacheUsers(userIds: List<String>):List<User>{
        return withContext(Dispatchers.Default) {

            val listDeferred = mutableListOf<Deferred<User>>()

            for (userId in userIds){
                listDeferred.add(
                    async
                    {
                        val user = getUserEndpoint.getUser(userId)
                        usersDao.upsertUserInfo(user)
                        user
                    })
            }
	        val listUsers = mutableListOf<User>()
            for (deferred in listDeferred) {
                listUsers.add(deferred.await())
            }
            listUsers
        }

    }


}