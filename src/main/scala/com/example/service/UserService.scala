package com.example.service

import java.lang.Iterable

import com.example.bean.Users
import com.example.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.{PostAuthorize, PreAuthorize}
import org.springframework.stereotype.Service

@Service
class UserService(@Autowired private val userRepository: UserRepository) {

  @PreAuthorize("hasRole('admin')")
  def listUsers(): Iterable[Users] = {
    userRepository.findAll
  }

  @PreAuthorize("hasRole('user')")
  @PostAuthorize("returnObject.username==principal.username || hasRole('admin')")
  def getUser(id: Long):Users = {
    userRepository.findOne(id)
  }
  @PreAuthorize("hasRole('admin')")
  def createUser(users: Users): Long = {
    userRepository.save(users)
    users.id
  }

}
