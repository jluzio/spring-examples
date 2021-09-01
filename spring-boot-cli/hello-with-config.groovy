@Grab("spring-boot-starter-actuator")

@Configuration
@ConfigurationProperties("app")
class Config {
    def message
}

@RestController
class Example {
    @Autowired
    Config config
 
    @GetMapping("/")
    public def helloWorld() {
        return [message: config.message]
    }
}
