package web.socket.WebSocketServer.config;

import jakarta.servlet.ServletContext;
import org.eclipse.jetty.ee10.websocket.server.JettyWebSocketServerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import web.socket.WebSocketServer.handler.TestHandler;

import java.security.Principal;
import java.time.Duration;
import java.util.List;
import java.util.Map;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new TestHandler(),"/test")//특정URL에 매핑하기 위한 전용 websocket 구성
                //HandshakeInterceptor 사용하여 handshake 전과 후의 메서드 노출
                .setHandshakeHandler(handshakeHandler())
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    //HandshakeInterceptor 설정 Jetty 식이고 최신 업데이트로 인해 JettyRequestUpgradeStrategy 생성자 부분 수정. 공식 사이트랑 내용이 다를거임
//                .setAllowedOrigins("*");// 출처 허용부분. 원래 기본동작은 동일한 출처의 요청만 허용되는 것

        System.out.println("registry : "+ registry);
    }

//    @Bean
//    public org.eclipse.jetty.websocket.server.WebSocketHandler testHandler(){//웹 소켓 핸들러를 빈객체로 생성한다.
//        return new TestHandler();
//    }

//    @Bean
//    public WebSocketHandler testHandler(){//웹 소켓 핸들러를 빈객체로 생성한다.
//        return new TestHandler();
//    }

    //Jetty
    @Bean
    public DefaultHandshakeHandler handshakeHandler(){
        return new DefaultHandshakeHandler(new ServletContextAwareJettyRequestUpgradeStrategy());
        // JettyRequestUpgradeStrategy 생성자 부분 수정
    }
    /*
https://github.com/spring-projects/spring-framework/issues/30344#issuecomment-1702906503
https://www.tabnine.com/code/java/classes/org.eclipse.jetty.websocket.server.WebSocketServerFactory
참고하여 만듬
 */
    public class ServletContextAwareJettyRequestUpgradeStrategy extends JettyRequestUpgradeStrategy
            implements ServletContextAware {

        private ServletContext servletContext ;


        public ServletContextAwareJettyRequestUpgradeStrategy(){
            super();
        }

        @Override
        public void setServletContext(ServletContext servletContext) {
            System.out.println("??? : "+servletContext);
            this.servletContext = servletContext;
        }

        @Override
        public void upgrade(ServerHttpRequest request, ServerHttpResponse response, String selectedProtocol, List<WebSocketExtension> selectedExtensions, Principal user, WebSocketHandler handler, Map<String, Object> attributes) throws HandshakeFailureException {
            System.out.println("servletContext : "+servletContext);
            if(servletContext != null){
                JettyWebSocketServerContainer container = JettyWebSocketServerContainer.getContainer(servletContext);
                System.out.println("container : "+container);

                if (container != null) {
                    container.setInputBufferSize(8192);
                    container.setIdleTimeout(Duration.ofSeconds(600000));
                }
            }
            super.upgrade(request, response, selectedProtocol, selectedExtensions, user, handler, attributes);
        }


    }
}
