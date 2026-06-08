//08-09-June2026 changes for Kafka implementation in payment-service, subscription-service etc
#1) create an interface for user-service feign client and copy the http method from which you want to fetch the information, and here return type should be ResponseEntity<?> to UserDto
@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceClient{

	@GetMapping("/me/{userId}")
    public ResponseEntity<?> getMe(@PathVariable Long userId);
}
#2) in application.properties need to provide the user.service.url
application.properties

user.service.url=http://localhost:8083

#3) now in PaymentService.java our  main work where we need to implement the Feign client to get the user.email()
now changes in PaymentService.java class:
//add:
       private final UserServiceClient userServiceClient; // Feign client // Constructor injection — Spring injects Feign proxy automatically
 // UPDATE constructor:
    public PaymentService(PaymentRepository r, PaymentEventPublisher p ,UserServiceClient userServiceClient) {		
        paymentRepository = r;
        eventPublisher = p; // ADD THIS
        this.userServiceClient=userServiceClient;
    }
	
create UserDto class in payment-service with same filed used in user-service(User model)because feign client call /me/{userid} will return object of User from where we need to get email
public class UserDto{

    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Role role = Role.USER;
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public enum Role { USER, ADMIN }

    public User() {}
    // Getters and Setters
	
}