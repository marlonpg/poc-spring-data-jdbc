# Spring Data JDBC Proof of Concept

## Project Purpose

This project was created to learn and explore Spring Data JDBC as an alternative to traditional ORM solutions like Hibernate. The goal is to understand the tradeoffs between different data access approaches in Spring applications.

## Why Spring Data JDBC vs Hibernate?

### Spring Data JDBC Advantages
- **Simplicity**: No lazy loading, no sessions, no caching complexity
- **Predictable performance**: You know exactly what SQL gets executed
- **Lightweight**: Minimal overhead, faster startup times
- **Explicit control**: Direct mapping to database operations
- **No magic**: Clear, straightforward object-relational mapping
- **Better for microservices**: Stateless, no session management headaches

### Spring Data JDBC Limitations
- **Manual relationship handling**: You manage associations yourself
- **Limited querying**: Basic repository methods, custom queries need @Query
- **No lazy loading**: Must fetch related data explicitly
- **Less feature-rich**: No automatic dirty checking, caching, etc.

### Hibernate Advantages
- **Rich feature set**: Lazy loading, caching, dirty checking, cascading
- **Complex relationships**: Handles intricate object graphs automatically
- **Mature ecosystem**: Extensive documentation, tooling, community
- **JPA standard**: Portable across different JPA implementations
- **Advanced querying**: HQL, Criteria API, native queries

### Hibernate Drawbacks
- **Complexity**: N+1 queries, session management, LazyInitializationException
- **Performance surprises**: Unexpected queries, caching issues
- **Heavy**: Larger memory footprint, slower startup
- **Learning curve**: Requires deep understanding to use effectively
- **Debugging nightmares**: Hard to trace what SQL actually executes

## When to Still Use Hibernate?

Despite common criticism, Hibernate makes sense for:

1. **Complex domain models** with deep object hierarchies
2. **Legacy applications** already using JPA
3. **Teams familiar with ORM patterns** who can handle the complexity
4. **Applications requiring advanced caching** strategies
5. **Rapid prototyping** where you want automatic CRUD operations

## Code Comparison

### Spring Data JDBC Approach

```java
// Entity - Simple POJO
@Table("users")
public class User {
    @Id
    private Long id;
    private String name;
    private String email;
    
    // Constructor, getters, setters
}

@Table("orders")
public class Order {
    @Id
    private Long id;
    private Long userId;  // Manual foreign key
    private BigDecimal amount;
    
    // Constructor, getters, setters
}

// Repository
interface UserRepository extends CrudRepository<User, Long> {
    @Query("SELECT * FROM users WHERE email = :email")
    Optional<User> findByEmail(String email);
}

// Service - Explicit relationship handling
@Service
public class UserService {
    
    public UserWithOrders getUserWithOrders(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<Order> orders = orderRepository.findByUserId(userId);
        return new UserWithOrders(user, orders);
    }
}
```

### Hibernate/JPA Approach

```java
// Entity - Rich annotations
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
    
    // Constructor, getters, setters
}

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    private BigDecimal amount;
    
    // Constructor, getters, setters
}

// Repository
interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);  // Method name query
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
    Optional<User> findByIdWithOrders(Long id);
}

// Service - Automatic relationship handling
@Service
@Transactional
public class UserService {
    
    public User getUserWithOrders(Long userId) {
        return userRepository.findByIdWithOrders(userId).orElseThrow();
        // Orders automatically loaded due to JOIN FETCH
    }
}
```

### Key Differences Highlighted

1. **Relationships**: Spring Data JDBC uses manual foreign keys, Hibernate uses annotations
2. **Fetching**: Spring Data JDBC requires explicit queries, Hibernate offers lazy/eager loading
3. **Complexity**: Spring Data JDBC is explicit, Hibernate hides complexity behind annotations
4. **Performance**: Spring Data JDBC predictable, Hibernate can surprise you

## The Learning Value

This project forces explicit understanding of database interactions, making you a better developer regardless of which data access tool you eventually choose. Spring Data JDBC's simplicity reveals the underlying mechanics that ORMs often hide.


## Getting Started

```bash
./mvnw spring-boot:run
```

## Technologies Used
- Spring Boot
- Spring Data JDBC
- Java
- Maven