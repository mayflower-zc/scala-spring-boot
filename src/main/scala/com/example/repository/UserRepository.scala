package com.example.repository

import java.lang.Long
import com.example.bean.Users
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
trait UserRepository extends CrudRepository[Users, Long] {
  def findUserByUsername(username: String): Users
}
