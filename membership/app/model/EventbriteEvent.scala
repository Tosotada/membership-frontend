package model

import play.api.libs.json.Json

case class EBRichText(text: String, html: String)

case class EBAddress(country_name: Option[String], city: Option[String], region: Option[String], address_1: Option[String], country: Option[String])

case class EBVenue(id: Option[String], address: EBAddress, latitude: Option[String], longitude: Option[String], name: Option[String])

case class EBTime(timezone: String, local: String, utc: String)

case class EBEvent(
                    name: EBRichText,
                    description: EBRichText,
                    logo_url: String,
                    id: String,
                    start: EBTime,
                    end: EBTime,
                    venue: EBVenue)

case class EBResponse(events: Seq[EBEvent])


object EventbriteDeserializer {
  implicit val ebAddress = Json.reads[EBAddress]
  implicit val ebVenue = Json.reads[EBVenue]
  implicit val ebTime = Json.reads[EBTime]
  implicit val ebRichText = Json.reads[EBRichText]
  implicit val ebEventReads = Json.reads[EBEvent]
  implicit val ebResponseReads = Json.reads[EBResponse]
}