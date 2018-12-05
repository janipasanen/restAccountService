package v1.post2

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import scala.concurrent.Future

final case class PostData2(id: PostId2, title: String, body: String)

class PostId2 private(val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object PostId2 {
  def apply(raw: String): PostId2 = {
    require(raw != null)
    new PostId2(Integer.parseInt(raw))
  }
}


class PostExecutionContext2 @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")

/**
  * A pure non-blocking interface for the PostRepository.
  */
trait PostRepository2 {
  def create(data: PostData2)(implicit mc: MarkerContext): Future[PostId2]

  def list()(implicit mc: MarkerContext): Future[Iterable[PostData2]]

  def get(id: PostId2)(implicit mc: MarkerContext): Future[Option[PostData2]]
}

/**
  * A trivial implementation for the Post Repository.
  *
  * A custom execution context is used here to establish that blocking operations should be
  * executed in a different thread than Play's ExecutionContext, which is used for CPU bound tasks
  * such as rendering.
  */
@Singleton
class PostRepository2Impl @Inject()()(implicit ec: PostExecutionContext2) extends PostRepository2 {

  private val logger = Logger(this.getClass)

  private val postList = List(
    PostData2(PostId2("1"), "title 2_1", "blog post 2_1"),
    PostData2(PostId2("2"), "title 2_2", "blog post 2_2"),
    PostData2(PostId2("3"), "title 2_3", "blog post 2_3"),
    PostData2(PostId2("4"), "title 2_4", "blog post 2_4"),
    PostData2(PostId2("5"), "title 2_5", "blog post 2_5")
  )

  override def list()(implicit mc: MarkerContext): Future[Iterable[PostData2]] = {
    Future {
      logger.trace(s"list: ")
      postList
    }
  }

  override def get(id: PostId2)(implicit mc: MarkerContext): Future[Option[PostData2]] = {
    Future {
      logger.trace(s"get: id = $id")
      postList.find(post => post.id == id)
    }
  }

  def create(data: PostData2)(implicit mc: MarkerContext): Future[PostId2] = {
    Future {
      logger.trace(s"create: data = $data")
      data.id
    }
  }

}
