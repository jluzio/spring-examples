# Run task in command line
- Default settings
>  mvn spring-boot:run -Dspring-boot.run.profiles=run-task
- Settings in outside folder using profile
>  mvn spring-boot:run -Dspring-boot.run.profiles=run-task,access-control -Dspring-boot.run.jvmArguments="-Dspring.config.additional-location=.settings/config/"
- Settings in outside folder
>  mvn spring-boot:run -Dspring-boot.run.profiles=run-task -Dspring-boot.run.jvmArguments="-Dspring.config.additional-location=.settings/config/application-access-control.yml"
