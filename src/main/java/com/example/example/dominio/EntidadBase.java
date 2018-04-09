package com.example.example.dominio;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter @Setter
public abstract class EntidadBase{

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

}
