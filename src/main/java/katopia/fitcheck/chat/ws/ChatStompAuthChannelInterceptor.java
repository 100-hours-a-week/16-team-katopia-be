package katopia.fitcheck.chat.ws;

import katopia.fitcheck.global.exception.AuthException;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.security.jwt.JwtProvider;
import katopia.fitcheck.global.security.jwt.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatStompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();
        if (StompCommand.CONNECT.equals(command)) {
            String token = extractBearerToken(accessor);
            Long memberId = jwtProvider.extractMemberId(token, JwtProvider.TokenType.ACCESS);
            if (memberId == null || !jwtProvider.isTokenType(token, JwtProvider.TokenType.ACCESS)) {
                throw new AuthException(AuthErrorCode.INVALID_AT);
            }
            accessor.setUser(new MemberPrincipal(memberId));
            return message;
        }

        if (requiresAuthenticatedUser(command) && !(accessor.getUser() instanceof MemberPrincipal)) {
            throw new AuthException(AuthErrorCode.INVALID_AT);
        }
        return message;
    }

    private boolean requiresAuthenticatedUser(StompCommand command) {
        return StompCommand.SEND.equals(command) || StompCommand.SUBSCRIBE.equals(command);
    }

    private String extractBearerToken(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader(JwtProvider.AUTHORIZATION_HEADER);
        if (header == null || !header.startsWith(JwtProvider.BEARER_PREFIX)) {
            throw new AuthException(AuthErrorCode.NOT_FOUND_AT);
        }
        return header.substring(JwtProvider.BEARER_PREFIX.length());
    }
}
