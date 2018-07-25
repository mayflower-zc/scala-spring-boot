package com.example

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ApiApplication
object ApiApplication extends App {
  SpringApplication.run(classOf[ApiApplication], args :_*)
}
