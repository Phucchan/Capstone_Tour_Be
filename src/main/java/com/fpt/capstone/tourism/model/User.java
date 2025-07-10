package com.fpt.capstone.tourism.model;

import com.fpt.capstone.tourism.model.enums.Gender;
import com.fpt.capstone.tourism.model.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fpt.capstone.tourism.constants.Constants.Default.DEFAULT_AVATAR_URL;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotNull(message = "Username cannot be null")
    @Size(min = 8, max = 30, message = "Username must be between 3 and 30 characters")
    @Column(nullable = false, unique = true)
    private String username;

    @Email(message = "Email should be valid")
    @NotNull(message = "Email cannot be null")
    @Column(nullable = false, unique = true)
    private String email;

    private Gender gender;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password must be at least 6 characters long")
    private String password;

    private String phone;

    private String address;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name="avatar_img")
    private String avatarImage;

    @Column(name = "email_confirmed")
    private boolean emailConfirmed;

    @Column(name="is_deleted")
    private Boolean deleted;

    @Column(name = "user_status")
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(userRoles.isEmpty()) return Set.of();
        return userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().getRoleName()))
                .collect(Collectors.toList());
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();


    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Friendship> sentFriendRequests = new HashSet<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Friendship> receivedFriendRequests = new HashSet<>();

    @Override
    protected void beforePersist() {
        if (userStatus == null) {
            userStatus = UserStatus.OFFLINE;
        }

        if (avatarImage == null || avatarImage.trim().isEmpty()) {
            avatarImage = DEFAULT_AVATAR_URL;
        }
    }

}
