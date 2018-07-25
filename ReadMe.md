# 使用scala搭建 Spring boot 框架
## 1. 前言
>spring boot是java开发平台上最流行的web框架。Scala是在JVM上运行的静态类型的函数编程语言。大数据处理Spark框架就是Scala语言开发的。

>本例子将spring boot框架集成到scala项目中。利用Spring security模块提供的用户权限管理。制作一个简单的用户登录及权限管理。

>使用到的技术 spring boot web service，swagger, h2 数据库，spring boot JPA，spring security

## 2. 创建一个Maven Spring boot应用程序
- 创建一个maven项目并将一下内容添加到Maven POM文件中。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">

  <modelVersion>4.0.0</modelVersion>

  <groupId>com.example</groupId>
  <artifactId>scala-spring-boot</artifactId>
  <packaging>jar</packaging>
  <version>1.0-SNAPSHOT</version>

  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.9.RELEASE</version>
    <relativePath/> <!-- lookup parent from repository -->
  </parent>

  <name>A Camel Scala Route</name>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.version>1.8</java.version>
    <scala.version>2.11.7</scala.version>
  </properties>

  <dependencyManagement>
    <dependencies>
    </dependencies>
  </dependencyManagement>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <scope>runtime</scope>
    </dependency>
    <!-- scala -->
    <dependency>
      <groupId>org.scala-lang</groupId>
      <artifactId>scala-library</artifactId>
      <version>${scala.version}</version>
    </dependency>
    <dependency>
      <groupId>org.scala-lang</groupId>
      <artifactId>scala-compiler</artifactId>
      <version>${scala.version}</version>
    </dependency>
    <!-- Swagger -->
    <dependency>
      <groupId>io.springfox</groupId>
      <artifactId>springfox-swagger-ui</artifactId>
      <version>2.7.0</version>
    </dependency>
    <dependency>
      <groupId>io.springfox</groupId>
      <artifactId>springfox-swagger2</artifactId>
      <version>2.7.0</version>
    </dependency>

  </dependencies>

  <build>
    <defaultGoal>install</defaultGoal>
    <sourceDirectory>src/main/scala</sourceDirectory>
    <testSourceDirectory>src/test/scala</testSourceDirectory>

    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
      <plugin>
        <groupId>net.alchim31.maven</groupId>
        <artifactId>scala-maven-plugin</artifactId>
        <version>3.2.1</version>
        <executions>
          <execution>
            <id>compile-scala</id>
            <phase>compile</phase>
            <goals>
              <goal>add-source</goal>
              <goal>compile</goal>
            </goals>
          </execution>
          <execution>
            <id>test-compile-scala</id>
            <phase>test-compile</phase>
            <goals>
              <goal>add-source</goal>
              <goal>testCompile</goal>
            </goals>
          </execution>
        </executions>
        <configuration>
          <recompileMode>incremental</recompileMode>
          <scalaVersion>${scala.version}</scalaVersion>
          <args>
            <arg>-deprecation</arg>
          </args>
          <jvmArgs>
            <jvmArg>-Xms64m</jvmArg>
            <jvmArg>-Xmx1024m</jvmArg>
          </jvmArgs>
        </configuration>
      </plugin>
    </plugins>
  </build>

</project>

```
- 我们使用Scala插件将src/main/scala 和 src/test/scala 目录下的代码编译成java代码。

## 3. 创建ApiApplication Scala 类
- 这个是spring的入口类。在运行时，该类引导Spring应用程序并启动Spring上下文。
```scala
@SpringBootApplication
class ApiApplication
object ApiApplication extends App {
  SpringApplication.run(classOf[ApiApplication], args :_*)
}
```
## 4. 创建RESTful API
@RestController和@RequestMapping用于在Spring中创建RESTful Web服务。Scala中Controller示例如下
```scala
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
```
## 5. 创建一个UserService 类
- 示例代码
```scala
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
```
- 在UserService的方法上加一个@PreAuthorize和@PostAuthorize。如果登录用户具有"admin"角色，这个用户运行listUsers()和createUser()操作。
如果登录用户拥有"user"角色,那么这个用户只运行getUser()操作。
- 这些操作都是通过Authorities表来实现。下面是import.sql示例:
```sql
DROP IF EXISTS authorities;
CREATE TABLE authorities (id bigint auto_increment not null, username varchar_ignorecase(50) not null, authority varchar_ignorecase(50) not null, constraint fk_authorities_users foreign key(username) references users(username));
INSERT INTO users (id, username, password,enabled) VALUES (1, 'root', 'root', true), (2, 'user', 'user', true);
INSERT INTO authorities (id, username, authority) VALUES (1, 'root', 'ROLE_user'), (2, 'root', 'ROLE_admin'), (3, 'user', 'ROLE_user');
```

## 6. 添加Spring DATA JPA
- 示例代码
```scala
@Repository
trait UserRepository extends CrudRepository[Users, Long] {
  def findUserByUsername(username: String): Users
}
```
## 7. 添加一个实体类Users
- 示例代码

<font color=#f71f02 >***注意*** 这里id的类型必须是java.lang.Long 不能是scala原生Long。原因是CrudRepository 的ID 类型必须是继承Serializable </font>
```scala
@Entity
class Users extends Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @BeanProperty
  var id: Long = _
  @BeanProperty
  @Column(name = "username")
  var username: String = _
  @BeanProperty
  @Column(name = "password")
  var password: String = _
  @BeanProperty
  @Column(name = "enabled")
  var enabled: Boolean = _
}
```
## 8. 添加数据源
- 这里是使用嵌入数据库H2. 需要将以下内容配置到application.properties

*spring.h2.console.enabled=true*是允许通过控制台访问H2数据库
```properties
spring.datasource.url=jdbc:h2:~/test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=sa
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
# h2
spring.h2.console.enabled=true
```
- 通过http://localhost:8080/h2-console 访问h2控制台 默认用户名和密码是sa/sa

## 9. 配置Swagger
- *Swagger*是一个REST Web服务文档工具。可以通过*http://localhost:8080/swagger-ui.html*访问
```scala
@Configuration
@EnableSwagger2
class SwaggerConfig {
  @Bean
  def api(): Docket = {
    new Docket(DocumentationType.SWAGGER_2).select.apis(RequestHandlerSelectors.any).paths(PathSelectors.any).build
  }
}
```
## 10. 将API的认证与Spring Security绑定
```scala
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(@Autowired val dataSource: DataSource) extends WebSecurityConfigurerAdapter {
    override def configure(http: HttpSecurity) = {
        http.authorizeRequests.antMatchers("/console", "/console/**", "/console/", "/swagger-ui.html", "/**/*.css", "/**/*.js", "/**/*.png", "/configuration/**", "/swagger-resources", "/v2/**").permitAll
        http.authorizeRequests.anyRequest.authenticated
        http.csrf.disable
        http.headers.frameOptions.disable
        http.httpBasic
    }
    @Bean override def userDetailsService: UserDetailsService = {
        val manager = new JdbcDaoImpl
        manager.setDataSource(dataSource)
        manager
    }
}
```
- *@EnableGlobalMethodSecurity(prePostEnabled = true)* 启用Spring Security 方法级别安全验证。

## 11. 使用Spring Test对Web服务进行测试
-示例代码
```scala
  @Test
  def testPostCreateUser() = {
    val headers = new HttpHeaders
    headers.add("Authorization", "Basic " + new String(Base64.encodeBase64(("root" + ":" + "root").getBytes)))
    headers.setContentType(MediaType.APPLICATION_JSON)
    headers.setAccept(util.Arrays.asList(MediaType.APPLICATION_JSON))
    val user = new Users
    user.setId(101L)
    user.setUsername("Test")
    user.setPassword("Test")
    user.setEnabled(true)
    val entity = new HttpEntity(user, headers)
    val result = template.postForObject("/api/users", entity, classOf[String])
    println(result)
  }
```

## 12. 参考
- 源码来源 [Making Spring Web Services With Scala](https://dzone.com/articles/spring-web-services-with-scala)


