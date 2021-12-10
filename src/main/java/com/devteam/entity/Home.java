package com.devteam.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Home {
    private Long id;
    private String title;
    private String description;
    private String link;
}
