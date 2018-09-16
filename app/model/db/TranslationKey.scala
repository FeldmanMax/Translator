package model.db

import java.time.Instant

final case class TranslationKey (
  id:               Int,
  key:              String,
  service:          String,
  feature:          String,
  isActive:         Boolean,
  create_timestamp: Instant,
  update_timestamp: Instant,
)