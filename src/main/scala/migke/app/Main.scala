import io.javalin.Javalin
import upickle.default.{ReadWriter, write, read}
import scala.collection.mutable.ArrayBuffer
import scalikejdbc.*

@main def run(): Unit = {
  BallsDB.init()
  val app = Javalin
    .create()
    .get(
      "/api/balls",
      (ctx) => ctx.json(write(BallsDB.all))
    )
    .get(
      "/api/balls/{id}",
      (ctx) => ctx.json(write(BallsDB.find(ctx.pathParam("id").toLong)))
    )
    .post(
      "/api/balls",
      (ctx) => {
        val received = read[BallDTO](ctx.body)
        ctx.json(write(BallsDB.add(received)))
      }
    )
    .start(8080)
}

case class Ball(id: Long, x: Int, y: Int, radius: Int) derives ReadWriter
case class BallDTO(x: Int, y: Int, radius: Int) derives ReadWriter

object BallsDB {
  given dbSession: DBSession = NamedDB("db").autoCommitSession()

  def init() = {
    Class.forName("org.sqlite.JDBC")
    ConnectionPool.add("db", "jdbc:sqlite:database.db", null, null)
    if (!BallsDB.tableExists("balls")) BallsDB.createTable()
  }

  def all: List[Ball] = sql"SELECT * FROM balls"
    .map(extractor)
    .list
    .apply()
  def find(id: Long): Option[Ball] = sql"""
    SELECT * FROM balls where id = ${id}
  """.map(extractor).single.apply()
  def add(dto: BallDTO): Ball = Ball(
    sql"""
    INSERT INTO balls (x, y, radius) VALUES (${dto.x}, ${dto.y}, ${dto.radius})
  """.updateAndReturnGeneratedKey.apply(),
    dto.x,
    dto.y,
    dto.radius
  )

  def extractor(rs: WrappedResultSet) =
    Ball(rs.long("id"), rs.int("x"), rs.int("y"), rs.int("radius"))

  def createTable() =
    sql"""create table balls (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      x  INTEGER NOT NULL,
      y  INTEGER NOT NULL,
      radius INTEGER NOT NULL CHECK (radius > 0)
    )""".execute.apply()
  def tableExists(tableName: String): Boolean =
    sql"""
    select name
    from sqlite_master
    where type = 'table' and name = $tableName
  """
      .map(_.string("name"))
      .single
      .apply()
      .isDefined
}
