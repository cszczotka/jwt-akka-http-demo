package com.cszczotka.http.api.handler

import java.sql.{Connection, DriverManager, Statement}

import com.typesafe.config.ConfigFactory
import org.mindrot.jbcrypt.BCrypt

trait DBHandler {

  val con: Connection = DriverManager.getConnection(DBHandler.connectionUrl, DBHandler.userName, DBHandler.password)
  val stmt: Statement = con.createStatement

  def dropTable(table: String) = {
    stmt.executeUpdate(s"drop table ${table}")
  }

  def createTableIfNotExists(table: String) = {
    val rset = con.getMetaData.getTables(null, null, table, null)
    if (!rset.next){
      val ddl = s"create table ${table}(id int auto_increment primary key, name varchar(100) not null, password varchar(100) not null, role varchar(50) not null)"
      stmt.executeUpdate(ddl)
    }
  }

  def createUserIfNotExists(name:String, password:String, role:String) = {
    val query = s"select * from login where name ='${name}';"
    val rs = stmt.executeQuery(query)
    if (!rs.next()) {
      val hash = getHash(password)
      val sql = s"insert into login(name, password, role) values('${name}', '${hash}', '${role}');"
      stmt.executeUpdate(sql)
    }
  }

  def getHash(str: String) : String = {
    BCrypt.hashpw(str, BCrypt.gensalt())
  }

  def checkHash(str: String, strHashed: String): Boolean = {
    BCrypt.checkpw(str,strHashed)
  }

  def releaseDB() = {
    stmt.close()
    con.close()
  }


}

object DBHandler {
  val config = ConfigFactory.load()
  val jdbcDriverName = config.getString("db.driver")
  val connectionUrl = config.getString("db.url")
  val userName = config.getString("db.user")
  val password = config.getString("db.password")
  Class.forName(DBHandler.jdbcDriverName)
}
