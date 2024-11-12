package com.trip.tripshorts.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

import static com.trip.tripshorts.user.domain.Role.*;

@Entity
@Getter
@Table(name="users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String nickname;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = GENERAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private SocialType socialType;

    @Column(nullable = false, updatable = false)
    private String socialId;

    private boolean active;

    public User createUser(String email, String nickname, String profileImageUrl, Role role, SocialType socialType, String socialId) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .role(role)
                .socialType(socialType)
                .socialId(socialId)
                .build();
    }

    public void deleteUser(boolean active) {
        this.active = active;
    }
}
