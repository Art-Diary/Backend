package klieme.artdiary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PreDestroy;
import klieme.artdiary.common.SSHConnection;

@Configuration
@EnableScheduling
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ArtdiaryApplication implements CommandLineRunner {
	@Autowired(required = false)
	SSHConnection sshConnection;

	public static void main(String[] args) {
		SpringApplication.run(ArtdiaryApplication.class, args);
	}

	@PreDestroy
	public void end() {
		try {
			if (sshConnection != null) {
				sshConnection.shutdown();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
