package com.example.emailverificationamigoscode.appuser;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_role",schema = "public")
public class AppUserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @SequenceGenerator(
//            name = "student_sequence",
//            sequenceName = "student_sequence",
//            allocationSize = 1
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "student_sequence"
//    )
   private Long id;
   private String name;
}
