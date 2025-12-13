package com.hasanoztunc.blogapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(
        name = "posts",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"slug"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @NotBlank
    @Size(
            min = 3,
            message = "Title must be at least 3 characters long"
    )
    @Column(
            name = "title",
            nullable = false
    )
    private String title;

    @NotBlank
    @Size(
            min = 10,
            message = "Content must be at least 10 characters long"
    )
    @Column(
            name = "content",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String content;

    @NotBlank
    @Size(
            min = 3,
            message = "Slug must be at least 3 characters long"
    )
    @Column(
            name = "slug",
            nullable = false,
            unique = true
    )
    private String slug;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    public Post(
            String title,
            String content,
            String slug,
            User user
    ) {
        this.title = title;
        this.content = content;
        this.slug = slug;
        this.user = user;
    }

//    @PrePersist
//    private void onCreate() {
//        this.createdAt = new Date();
//    }
}
