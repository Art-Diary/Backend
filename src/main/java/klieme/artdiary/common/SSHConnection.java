package klieme.artdiary.common;

import java.util.Properties;
import java.util.function.Consumer;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SSHConnection {
	private String SSH_HOST;
	private int SSH_PORT;
	private String SSH_USER;
	private String SSH_PW;
	private String LOC_HOST;
	private int LOC_PORT;
	private int REMOTE_PORT;

	public SSHConnection(String sshHost, int sshPort, String sshUser, String sshPw, String locHost, int locPort,
		int remotePort) {
		this.SSH_HOST = sshHost;
		this.SSH_PORT = sshPort;
		this.SSH_USER = sshUser;
		this.SSH_PW = sshPw;
		this.LOC_HOST = locHost;
		this.LOC_PORT = locPort;
		this.REMOTE_PORT = remotePort;
	}

	private Session sshSession;

	public SSHConnection init(Consumer<Boolean> arg) {
		try {
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");

			JSch jSch = new JSch();
			sshSession = jSch.getSession(SSH_USER, SSH_HOST, SSH_PORT);
			sshSession.setPassword(SSH_PW);
			sshSession.setConfig(config);

			sshSession.setPortForwardingL(LOC_PORT, LOC_HOST, REMOTE_PORT);
			sshSession.connect();
			arg.accept(true);
		} catch (Exception e) {
			e.printStackTrace();
			arg.accept(false);
		}
		return this;
	}

	public void shutdown() throws Exception {
		if (sshSession != null && sshSession.isConnected()) {
			sshSession.disconnect();
		}
	}
}
