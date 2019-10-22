package com.example.pethotel;

public class Animal {
    public String animalName;
    public PetType type;

    public Animal() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Animal(String name, PetType type){
        this.animalName = name;
        this.type = type;
    }
}
