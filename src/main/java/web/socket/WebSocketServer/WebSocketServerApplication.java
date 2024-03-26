package web.socket.WebSocketServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.KeyStore;
import java.security.KeyStoreException;

@SpringBootApplication
public class WebSocketServerApplication {

	public static void main(String[] args) throws KeyStoreException {
//		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

		SpringApplication.run(WebSocketServerApplication.class, args);
	}

}
