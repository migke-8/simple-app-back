import io.javalin.Javalin
import upickle.default.{ReadWriter, write, read}
import scala.collection.mutable.ArrayBuffer
case class Ball(id: Int, x: Int, y: Int, radius: Int) derives ReadWriter
case class BallDTO(x: Int, y: Int, radius: Int) derives ReadWriter
@main def run(): Unit = {
  val db = ArrayBuffer(
    Ball(1, 0, 0, 90),
    Ball(2, 1021, 987, 101),
    Ball(3, 1000, 29, 70)
  )
  val app = Javalin
    .create()
    .get(
      "/api/balls",
      (ctx) => ctx.json(write(db))
    )
    .get(
      "/api/balls/{id}",
      (ctx) => ctx.json(write(db(ctx.pathParam("id").toInt)))
    )
    .post(
      "/api/balls",
      (ctx) => {
        val received = read[BallDTO](ctx.body)
        db.addOne(Ball(db.length + 1, received.x, received.y, received.radius))
        ctx.json(write(db.last))
      }
    )
    .start(8080)
}
