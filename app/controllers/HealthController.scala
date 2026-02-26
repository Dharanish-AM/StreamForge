package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class HealthController @Inject() (
    val controllerComponents: ControllerComponents
) extends BaseController {
  def health: Action[AnyContent] = Action {
    Ok("OK")
  }
}
