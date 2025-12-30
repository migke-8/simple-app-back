import io.javalin.Javalin
import upickle.default.{ReadWriter, write, read}
import scala.collection.mutable.ArrayBuffer
import scalikejdbc.*

@main def run(): Unit = {
  DB.init()
  val app = Javalin
    .create()
    .get(
      "/api/balls",
      (ctx) => ctx.json(write(DB.all))
    )
    .get(
      "/api/balls/{id}",
      (ctx) => ctx.json(write(DB.find(ctx.pathParam("id").toLong)))
    )
    .post(
      "/api/balls",
      (ctx) => {
        val received = read[BallDTO](ctx.body)
        ctx.json(write(DB.add(received)))
      }
    )
    .start(8080)
}

case class Ball(id: Long, x: Int, y: Int, radius: Int) derives ReadWriter
case class BallDTO(x: Int, y: Int, radius: Int) derives ReadWriter

object DB {

  given session: DBSession = AutoSession

  def init() = {
    Class.forName("org.sqlite.JDBC")
    ConnectionPool.singleton("jdbc:sqlite:database.db", "", "")
  }
  def all: List[Ball] = sql"select * from balls"
    .map((rs) =>
      Ball(rs.long("id"), rs.int("x"), rs.int("y"), rs.int("radius"))
    )
    .list
    .apply()
  def find(id: Long): Ball = ???
  def add(dto: BallDTO): Ball = ???
}
