package filters

import javax.inject.Inject
import play.api.http.HttpFilters

class Filters @Inject() (requestLoggingFilter: RequestLoggingFilter) extends HttpFilters {
  override val filters = Seq(requestLoggingFilter)
}
