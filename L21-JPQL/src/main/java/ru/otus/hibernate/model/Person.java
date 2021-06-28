package ru.otus.hibernate.model;


import org.hibernate.annotations.NamedQuery;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static javax.persistence.GenerationType.SEQUENCE;

@NamedQuery(
        name = "get_person_by_id",
        query = "select p from Person p where id = :id"
)
@Entity
@Table(name = "tPerson")
public class Person {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator="personId_generator")
    @SequenceGenerator(name = "personId_generator", sequenceName = "personId", allocationSize = 1)
    private Long id;

    private String name;

    private String nickName;

    private String address;

    private Date createdOn;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.EAGER) //вариат 1 создания схемы
    //@OneToMany(cascade = CascadeType.ALL) // вариант 2 создания схемы
    private List<Phone> phones = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", address='" + address + '\'' +
                ", createdOn=" + createdOn +
                ", phones=" + phones +
                '}';
    }
}
