package com.cszczotka.http.api

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, RawHeader}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.cszczotka.http.MyHttpServer.{createTableIfNotExists, createUserIfNotExists, dropTable}
import org.scalatest.{FlatSpec, Matchers}
import com.cszczotka.http.api.handler.AuthenticationHandler


class JwtApiSpec extends FlatSpec with Matchers with ScalatestRouteTest with AuthenticationHandler {

  override def beforeAll(): Unit = {
    super.beforeAll()
    createTableIfNotExists("login")
    createUserIfNotExists("admin", "abc123", "admin")
    createUserIfNotExists("john2", "qwerty", "user")
  }

  override def afterAll(): Unit = {
    super.afterAll()
    dropTable("login")
  }

  behavior of "Akka REST Api with JWT"

  it should "Able to login as admin role in system  " in {
    val usrName = Multipart.FormData.BodyPart.Strict("username", "admin")
    val passwd = Multipart.FormData.BodyPart.Strict("password", "abc123")
    val responseOutput = """Data : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhZG1pbiIsImF1ZCI6ImFkbWluIn0.CrqX06bzvGSQx532rgdlcsHxdB19uPOxgSfbXuCKmaQ successfully saved."""
    val formData = Multipart.FormData(usrName, passwd)
    Post(s"/login", formData) ~> JwtApi.routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe responseOutput
    }
  }

  it should "Able to login in system without role " in {
    val usrName = Multipart.FormData.BodyPart.Strict("username", "john2")
    val passwd = Multipart.FormData.BodyPart.Strict("password", "qwerty")
    val responseOutput = """Data : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJqb2huMiJ9.NozX7YTkog7mQjphyuJ73kCj0g2HRWUMlcavDT14-1I successfully saved."""
    val formData = Multipart.FormData(usrName, passwd)
    Post(s"/login", formData) ~> JwtApi.routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe responseOutput
    }
  }

  it should "not be Able to login  in system with  invalid credentials " in {
    val usrName = Multipart.FormData.BodyPart.Strict("username", "john2")
    val passwd = Multipart.FormData.BodyPart.Strict("password", "abc123")
    val responseOutput = "login credentials are invalid"
    val formData = Multipart.FormData(usrName, passwd)
    Post(s"/login", formData) ~> JwtApi.routes ~> check {
      status shouldBe StatusCodes.Unauthorized
      responseAs[String] shouldBe responseOutput
    }
  }


  it should "Able to access jwt protected uri " in {
    val responseOutput = """user names are  (john2,user,2) """
     val token = createToken(JwtApi.secretKey, "john2")
    Get(s"/getUserDetail/2").withHeaders(RawHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJyYW1lc2gifQ.QUsOimu5p1NuMHfLiufcPeYnodNVqljxfxQ_02bW1RI")) ~> JwtApi.routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe  responseOutput
    }
  }

}

