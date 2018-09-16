package model.db

import play.api.i18n.Lang

case class Translation(
  id:           Int,
  tran_key:     String,
  translation:  String,
  lang:         Lang,
  is_active:    Boolean
)
