package com.yeongju.domain.character;

import com.yeongju.domain.common.BaseTimeEntity;
import com.yeongju.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_characters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserCharacter extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 500)
    private String imageUrl;
    
    @Column(length = 500)
    private String originalPhotoUrl;
    
    @Column(length = 1000)
    private String features;
    
    @Column(length = 50)
    private String gender;
    
    @Column(length = 50)
    private String style;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    public void deactivate() {
        this.isActive = false;
    }
}
