package monitoring

import com.gu.monitoring.StatusMetrics

object EventbriteMetrics extends Metrics with StatusMetrics {
  val service = "Eventbrite"
}