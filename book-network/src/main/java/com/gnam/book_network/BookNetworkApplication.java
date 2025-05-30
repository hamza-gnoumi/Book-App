package com.gnam.book_network;

import com.gnam.book_network.book.Book;
import com.gnam.book_network.book.BookRepository;
import com.gnam.book_network.role.Role;
import com.gnam.book_network.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
public class BookNetworkApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookNetworkApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository, BookRepository bookRepository) {
		return args -> {
			if (roleRepository.findByName("USER").isEmpty()) {
				roleRepository.save(Role.builder().name("USER").build());
			}
			/*bookRepository.save(
					Book.builder()
							.title("spring for beginners")
							.shareable(true)
							.isbn("w654645")
							.authorName("gnam")
							.synopsis("spring boot for beginner")
							.createdBy(12)
							.createdDate(LocalDateTime.now())
							.build()
			);*/

		};
	}
}
