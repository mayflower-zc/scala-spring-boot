package com.example.controller

import java.lang.Iterable
import com.example.bean.Users
import com.example.service.UserService
import org.apache.tomcat.jdbc.pool.DataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(path = Array("/api"))
class UserController(@Autowired val userService: UserService, @Autowired val dataSource: DataSource) {
  @GetMapping(path = Array("/users"))
  def getAllUsers(): Iterable[Users] = {
    userService.listUsers()
  }
  @GetMapping(path = Array("/users/{id}"))
  def getUser(@PathVariable id: Long): Users = {
    userService.getUser(id)
  }
  @PostMapping(path = Array("/users"))
  def createUser(@RequestBody users: Users): ResponseEntity[Long] = {
    val id = userService.createUser(users)
    new ResponseEntity(id, new HttpHeaders, HttpStatus.CREATED)
  }
}
