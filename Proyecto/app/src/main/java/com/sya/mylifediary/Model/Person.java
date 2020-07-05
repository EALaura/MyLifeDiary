package com.sya.mylifediary.Model;

/* Es el modelo para los uusarios que se registren en la aplicación
   cada usuario nuevo tendrá un nombre, un email, un usuario y una
   contraseña */
public class Person {
    public String name;
    public String email;
    public String user;
    public String password;

    /* Métodos get y set para cada atributo */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
