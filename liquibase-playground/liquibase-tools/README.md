# Run task in command line
- Default settings
> mvn spring-boot:run -Dspring-boot.run.profiles=run-task
- Settings in <liquibase-tools>/config folder
> mvn spring-boot:run -Dspring-boot.run.profiles=run-task,access-control
- Settings in outside folder using profile
> mvn spring-boot:run -Dspring-boot.run.profiles=run-task,access-control -Dspring-boot.run.jvmArguments="-Dspring.config.additional-location=.settings/config/"
