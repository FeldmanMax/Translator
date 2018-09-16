package repositories.statuses

trait LanguageStatus

case object AddSuccess  extends LanguageStatus
case object AddFailed   extends LanguageStatus
case object LangExists  extends LanguageStatus
