@Grab("spring-boot-starter-actuator")

@RestController
class Example {
 
    @GetMapping("/")
    public def helloWorld() {
        return [message: "Hello World!"]
    }
}
