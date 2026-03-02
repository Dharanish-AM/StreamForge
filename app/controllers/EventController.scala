package controllers

import javax.inject._
import play.api.mvc.ControllerComponents
import scala.concurrent.ExecutionContext
import play.api.mvc.AbstractController
import services.EventService

class EventController @Inject() (cc: ControllerComponents, service: EventService)(implicit
    ec: ExecutionContext
) extends AbstractController(cc) {

}
