package klieme.artdiary.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SshTunnelConfig {
	@Value("${ssh.host}")
	private String SSH_HOST;
	@Value("${ssh.port}")
	private int SSH_PORT;
	@Value("${ssh.user}")
	private String SSH_USER;
	@Value("${ssh.pw}")
	private String SSH_PW;
	@Value("${local.host}")
	private String LOC_HOST;
	@Value("${local.db.port}")
	private int LOC_PORT;
	@Value("${remote.db.port}")
	private int REMOTE_PORT;

	@Bean
	public SSHConnection sshConnection() {
		SSHConnection connection = new SSHConnection(SSH_HOST, SSH_PORT, SSH_USER, SSH_PW, LOC_HOST, LOC_PORT,
			REMOTE_PORT);
		connection.init(arg -> {
			if (!arg) {
				System.out.println("!!!! SSH 터널링 실패 !!!! 프로그램을 종료");
				System.exit(0);
			} else {
				System.out.println("!!!! SSH 터널링 성공 !!!!");
			}
		});
		return connection;
	}
}
