package com.cszczotka.http.api.handler

import scala.collection.mutable.ListBuffer


trait JwtApiHandler extends DBHandler {

  def getAllUserName(): ListBuffer[String] = {
    val query = "select * from login ;"
    val rs = stmt.executeQuery(query)
    val result: ListBuffer[String] = new ListBuffer()
    while (rs.next()) {
      result.append(rs.getString(1))
    }
    result
  }

  def getLoginInfo(name: String): (String, String, String, Int) = {
    val query = s"select name, password, role, id from login where name ='${name}';"
    val rs = stmt.executeQuery(query)
    val result = if (rs.next()) {
      (rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4))
    } else {
      ("not found", "not found", "not found", 0)
    }
    result
  }

  def getUserInfoById(userId: Int): (String, String, Int) = {
    val query = s"select * from login where id =${userId};"
    val rs = stmt.executeQuery(query)
    val result = if (rs.next()) {
      (rs.getString(2),  rs.getString(4), rs.getInt(1))
    } else {
      ("not found", "not found", 0)
    }
    result
  }



}
