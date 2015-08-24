import filters._
import monitoring.SentryLogging
import play.api.Application
import play.api.mvc.WithFilters
import play.filters.csrf._
import services._

object Global extends WithFilters(RedirectMembersFilter, CheckCacheHeadersFilter, CSRFFilter(), Gzipper, AddCSPHeader, AddEC2InstanceHeader) {
  override def onStart(app: Application) {
    SentryLogging.init()

    GuardianLiveEventService.start()
    LocalEventService.start()
    MasterclassEventService.start()

    TouchpointBackend.All.foreach(_.start())
    GuardianContentService.start()
  }
}
