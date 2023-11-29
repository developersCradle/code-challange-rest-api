package com.example.restdemo.utility;

import java.util.Random;

import com.example.restdemo.entity.User;
import com.github.javafaker.Faker;

public class Utilities {

	public static User generateRandomUser(int index)
	{
    	Faker faker = new Faker(); // Faking name
    	Random rand = new Random(); // Faking random phonenumber
   		// Random phone number
   		int n = rand.nextInt((int) Math.pow(10, 10));
   		String phoneNumber = String.format("%010d", n);
   		// Random name
   		String name = faker.name().fullName();
   		return new User(index, name, phoneNumber);
    }
}
