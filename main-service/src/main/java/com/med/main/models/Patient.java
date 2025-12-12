package com.med.main.models;

import jakarta.persistence.*;

@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // В вашей предыдущей схеме (Go) это были поля firstName/lastName,
    // но если вы хотите использовать просто 'name', оставьте так.
    // Если нужно соответствие старой базе, лучше вернуть firstName и lastName.
    private String name;

    // Геттеры и Сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}