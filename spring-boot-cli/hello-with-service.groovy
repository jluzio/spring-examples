@RestController
class Example {
 
    @Autowired
    private MyService myService;

    @GetMapping("/")
    public String helloWorld() {
        return myService.sayWorld();
    }
}

@Service
class MyService {
    public String sayWorld() {
        return "World!";
    }
}