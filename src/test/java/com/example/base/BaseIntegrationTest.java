package com.example.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.modules.auth.entities.Account;
import com.example.modules.auth.repositories.AccountsRepository;
import com.example.modules.users.entities.User;
import com.example.modules.users.repositories.UsersRepository;
import com.redis.testcontainers.RedisContainer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
@ActiveProfiles("test")
public class BaseIntegrationTest {

  @Container
  @ServiceConnection
  protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:16-alpine"
  );

  @Container
  protected static RedisContainer redis = new RedisContainer("redis:7.0-alpine");

  @Container
  protected static MinIOContainer minio = new MinIOContainer("minio/minio:latest");

  @DynamicPropertySource
  static void populateProperties(DynamicPropertyRegistry registry) {
    registry.add("minio.endpoint", minio::getS3URL);
    registry.add("minio.access-key", minio::getUserName);
    registry.add("minio.secret-key", minio::getPassword);
    registry.add("minio.bucket", () -> "spring-rest-api-bucket");

    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", redis::getRedisPort);
  }

  @Autowired
  protected AccountsRepository accountsRepository;

  @Autowired
  protected UsersRepository usersRepository;

  @Autowired
  protected PasswordEncoder passwordEncoder;

  @BeforeEach
  protected void setup() {
    Account account = Account.builder()
      .email("email@example.com")
      .password(passwordEncoder.encode("password@123456"))
      .build();
    User user = User.builder().account(account).build();

    accountsRepository.save(account);
    usersRepository.save(user);
  }

  @Test
  final void postgresConnectionEstablished() {
    assertTrue(postgres.isCreated());
  }

  @Test
  final void redisConnectionEstablished() {
    assertTrue(redis.isCreated());

    String redisURI = redis.getRedisURI();
    RedisClient client = RedisClient.create(redisURI);
    try (StatefulRedisConnection<String, String> connection = client.connect()) {
      RedisCommands<String, String> commands = connection.sync();
      assertEquals("PONG", commands.ping());
    }
  }

  @Test
  final void minioConnectionEstablished() {
    assertTrue(minio.isCreated());
  }

  protected Account getAccount() {
    return accountsRepository.findAll().get(0);
  }

  protected User getUser() {
    return usersRepository.findAll().get(0);
  }
}
